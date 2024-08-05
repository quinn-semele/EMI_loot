package fzzyhmstrs.emi_loot.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.BufferedReader;
import java.util.*;

public class ServerResourceData {

    public static final Multimap<ResourceLocation, LootTable> DIRECT_DROPS = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
    public static final List<ResourceKey<LootTable>> SHEEP_TABLES;
    public static final List<ResourceLocation> TABLE_EXCLUSIONS = new LinkedList<>();
    private static final Gson GSON = new Gson();
    private static final int DIRECT_DROPS_PATH_LENGTH = "direct_drops/".length();
    private static final int FILE_SUFFIX_LENGTH = ".json".length();

    public static void loadDirectTables(ResourceManager resourceManager){
        DIRECT_DROPS.clear();
        resourceManager.listResources("direct_drops",path -> path.getPath().endsWith(".json")).forEach(ServerResourceData::loadDirectTable);
        resourceManager.listResources("emi_loot_data",path -> path.getPath().endsWith(".json")).forEach(ServerResourceData::loadTableExclusion);
    }

    private static void loadDirectTable(ResourceLocation id, Resource resource){
        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading direct drop table from file: " + id.toString());
        String path = id.getPath();
        ResourceLocation id2 = Identifier.of(id.getNamespace(), path.substring(DIRECT_DROPS_PATH_LENGTH, path.length() - FILE_SUFFIX_LENGTH));
        String path2 = id2.getPath();
        if (!(path2.startsWith("blocks/") || path2.startsWith("entities/"))){
            EMILoot.LOGGER.error("File path for [" + id + "] not correct; needs a 'blocks' or 'entities' subfolder. Skipping.");
            EMILoot.LOGGER.error("Example: [./data/mod_id/direct_drops/blocks/cobblestone.json] is a valid block direct drop table path for a block added by [mod_id].");
            return;
        }
        try {
            BufferedReader reader = resource.openAsReader();
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            LootTable lootTable = GSON.fromJson(json, LootTable.class);
            if (lootTable != null) {
                DIRECT_DROPS.put(id2, lootTable);
            } else {
                EMILoot.LOGGER.error("Loot table in file [" + id + "] is empty!");
            }
        } catch(Exception e){
            EMILoot.LOGGER.error("Failed to open or read direct drops loot table file: " + id);
        }
    }

    private static void loadTableExclusion(ResourceLocation id, Resource resource){
        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading exclusion table from file: " + id.toString());
        try {
            BufferedReader reader = resource.openAsReader();
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement list = json.get("exclusions");
            if (list != null && list.isJsonArray()){
                list.getAsJsonArray().forEach(element -> {
                    if (element.isJsonPrimitive()){
                        ResourceLocation identifier = Identifier.of(element.getAsString());
                        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding exclusion: " + identifier);
                        TABLE_EXCLUSIONS.add(identifier);
                    } else {
                        EMILoot.LOGGER.error("Exclusion element not properly formatted: " + element);
                    }
                });
            } else {
                EMILoot.LOGGER.error("Exclusions in file: " + id + " not readable.");
            }
        } catch(Exception e){
            EMILoot.LOGGER.error("Failed to open or read table exclusions file: " + id);
        }
    }

    public static boolean skipTable(ResourceLocation id){
        return TABLE_EXCLUSIONS.contains(id);
    }

    public static Multimap<ResourceLocation, LootTable> getMissedDirectDrops(List<Identifier> parsedList){
        Multimap<ResourceLocation, LootTable> missedDrops = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
        for (Map.Entry<ResourceLocation,LootTable> entry : DIRECT_DROPS.entries()){
            if (!parsedList.contains(entry.getKey())){
                missedDrops.put(entry.getKey(),entry.getValue());
            }
        }
        return missedDrops;
    }

    static{
        SHEEP_TABLES = List.of(
                BuiltInLootTables.SHEEP_WHITE,
                BuiltInLootTables.SHEEP_ORANGE,
                BuiltInLootTables.SHEEP_MAGENTA,
                BuiltInLootTables.SHEEP_LIGHT_BLUE,
                BuiltInLootTables.SHEEP_YELLOW,
                BuiltInLootTables.SHEEP_LIME,
                BuiltInLootTables.SHEEP_PINK,
                BuiltInLootTables.SHEEP_GRAY,
                BuiltInLootTables.SHEEP_LIGHT_GRAY,
                BuiltInLootTables.SHEEP_CYAN,
                BuiltInLootTables.SHEEP_PURPLE,
                BuiltInLootTables.SHEEP_BLUE,
                BuiltInLootTables.SHEEP_BROWN,
                BuiltInLootTables.SHEEP_GREEN,
                BuiltInLootTables.SHEEP_RED,
                BuiltInLootTables.SHEEP_BLACK
        );
    }
}
