package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

@Mod(EMILoot.MOD_ID)
public class EMILoot {

    public static final String MOD_ID = "emi_loot";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_loot");
    public static RandomSource emiLootRandom = new SingleThreadedRandomSource(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static EmiLootConfig config = readOrCreate();
    public static boolean DEBUG = config.debugMode;

    /*
    //conditions & functions will be used in Lootify also, copying the identifier here so both mods can serialize the same conditions separately
    public static LootConditionType WITHER_KILL = LootConditionTypes.register("lootify:wither_kill", new KilledByWitherLootCondition.Serializer());
    public static LootConditionType SPAWNS_WITH = LootConditionTypes.register("lootify:spawns_with", new MobSpawnedWithLootCondition.Serializer());
    public static LootConditionType CREEPER = LootConditionTypes.register("lootify:creeper", new BlownUpByCreeperLootCondition.Serializer());
    public static LootFunctionType SET_ANY_DAMAGE = LootFunctionTypes.register("lootify:set_any_damage", new SetAnyDamageLootFunction.Serializer());
    public static LootFunctionType OMINOUS_BANNER = LootFunctionTypes.register("lootify:ominous_banner", new OminousBannerLootFunction.Serializer());
     */

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, MOD_ID);

    public static final DeferredHolder<Enchantment, Enchantment> RANDOM = ENCHANTMENTS.register("random", () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.TRIDENT, EquipmentSlot.values()){
        @Override
        public boolean isTradeable() {
            return false;
        }

        @Override
        public boolean isDiscoverable() {
            return false;
        }
    });

    public EMILoot(IEventBus modEventBus) {
        ENCHANTMENTS.register(modEventBus);
        PlayPackets.registerServerReadyCallback(context -> LootTableParser.registerServer(context.player()));
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static EmiLootConfig readOrCreate(){
        File dir = FMLPaths.CONFIGDIR.get().toFile();
        
        if (!dir.exists() && !dir.mkdirs()) {
            LOGGER.error("EMI Loot could not find or create config directory, using default configs");
            return new EmiLootConfig();
        }
        String f_old_name = "EmiLootConfig.json";
        String f_name = "EmiLootConfig_v1.json";
        
        File f_old = new File(dir,f_old_name);
        
        try{
            if (f_old.exists()){
                EmiLootConfigOld oldConfig = gson.fromJson(new InputStreamReader(new FileInputStream(f_old)),EmiLootConfigOld.class);
                EmiLootConfig newConfig = oldConfig.generateNewConfig();
                File f = new File(dir,f_name);
                if (f.exists()){
                    f_old.delete();
                    return gson.fromJson(new InputStreamReader(new FileInputStream(f)),EmiLootConfig.class);
                } else if (!f.createNewFile()){
                    LOGGER.error("Failed to create new config file, using old config with new defaults.");
                } else {
                    f_old.delete();
                    FileWriter fw = new FileWriter(f);
                    String json = gson.toJson(newConfig);
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info(json);
                    fw.write(json);
                    fw.close();
                }
                return newConfig;
            } else {
                File f = new File(dir,f_name);
                if (f.exists()) {
                    return gson.fromJson(new InputStreamReader(new FileInputStream(f)), EmiLootConfig.class);
                } else if (!f.createNewFile()) {
                    throw new UnsupportedOperationException("couldn't generate config file");
                } else {
                    FileWriter fw = new FileWriter(f);
                    EmiLootConfig emc = new EmiLootConfig();
                    String json = gson.toJson(emc);
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info(json);
                    fw.write(json);
                    fw.close();
                    return emc;
                }
            }
        } catch(Exception e){
            LOGGER.error("Emi Loot failed to create or read it's config file!");
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return new EmiLootConfig();
        }
    }
    
    public static class EmiLootConfig{
        public boolean debugMode = false;

        public boolean parseChestLoot = true;
        
        public boolean parseBlockLoot = true;
        
        public boolean parseMobLoot = true;
    
        public boolean parseGameplayLoot = true;

        public boolean chestLootCompact = true;

        public boolean chestLootAlwaysStackSame = false;

        public boolean mobLootIncludeDirectDrops = true;
		public boolean parseArchaeologyLoot = true;
	}

    public static class EmiLootConfigOld{
        public boolean parseChestLoot = true;

        public boolean parseBlockLoot = true;

        public boolean parseMobLoot = true;

        public boolean parseGameplayLoot = true;

        public EmiLootConfig generateNewConfig(){
            EmiLootConfig newConfig = new EmiLootConfig();
            newConfig.parseChestLoot = this.parseChestLoot;
            newConfig.parseBlockLoot = this.parseBlockLoot;
            newConfig.parseMobLoot = this.parseMobLoot;
            newConfig.parseGameplayLoot = this.parseGameplayLoot;
            return newConfig;
        }
    }
}
