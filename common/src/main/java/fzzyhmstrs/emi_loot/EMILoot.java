package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import lol.bai.badpackets.api.event.PacketSenderReadyCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Supplier;

public class EMILoot {

    public static final String MOD_ID = "emi_loot";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_loot");
    public static Random emiLootRandom = new LocalRandom(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static EmiLootConfig config = readOrCreate();
    public static boolean DEBUG = config.debugMode;

    //conditions & functions will be used in Lootify also, copying the identifier here so both mods can serialize the same conditions separately
    public static Supplier<LootConditionType> WITHER_KILL;
    public static Supplier<LootConditionType> SPAWNS_WITH;
    public static Supplier<LootConditionType> CREEPER;
    public static Supplier<LootFunctionType> SET_ANY_DAMAGE;
    public static Supplier<LootFunctionType> OMINOUS_BANNER;

    public static Enchantment RANDOM = new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()){
        @Override
        public boolean isAvailableForEnchantedBookOffer() {
            return false;
        }

        public boolean isAvailableForRandomSelection() {
            return false;
        }
    };

    public static void init() {
        PacketSenderReadyCallback.registerServer((handler, sender, server) -> {
            EMILoot.parser.registerServer(handler.player);
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static EmiLootConfig readOrCreate(){
        File dir = EMILootExpectPlatform.getConfigDir().toFile();

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
