package fzzyhmstrs.emi_loot.parser;

import com.google.common.collect.Multimap;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.server.*;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@EventBusSubscriber(modid = EMILoot.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class LootTableParser {

    private static final Map<ResourceLocation, ChestLootTableSender> chestSenders = new HashMap<>();
    private static final Map<ResourceLocation, BlockLootTableSender> blockSenders = new HashMap<>();
    private static final Map<ResourceLocation, MobLootTableSender> mobSenders = new HashMap<>();
    private static final Map<ResourceLocation, GameplayLootTableSender> gameplaySenders = new HashMap<>();
    private static final Map<ResourceLocation, ArchaeologyLootTableSender> archaeologySenders = new HashMap<>();
    public static final Object2BooleanMap<PostProcessor> postProcessors;
    private static Map<LootDataId<?>, ?> tables = new HashMap<>();
    private static final Map<ResourceLocation,LootDataId<?>> keyLookUp = new HashMap<>();
    public static String currentTable = "none";
    public static List<ResourceLocation> parsedDirectDrops = new LinkedList<>();
    public static boolean hasParsedLootTables = false;
    public static LootDataManager lootManager = null;
    public static final ResourceLocation CLEAR_LOOTS = Identifier.of("e_l", "clear");


    static {
        Object2BooleanOpenHashMap<PostProcessor> map = new Object2BooleanOpenHashMap<>();
        for (var value : PostProcessor.values()){
            map.put(value,false);
        }
        postProcessors = map;
    }

    private static boolean hasPostProcessed(){
        for (boolean bl: postProcessors.values()){
            if (!bl) return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            event.getPlayerList().getPlayers().forEach(LootTableParser::registerServer);
        } else {
            registerServer(event.getPlayer());
        }
    }

    public static void registerServer(ServerPlayer player){
        if (!hasPostProcessed()){
            EMILoot.LOGGER.warn("Post-processing not completed for some reason, completing now...");
            for (PostProcessor process: PostProcessor.values()){
                postProcess(process);
            }
            EMILoot.LOGGER.warn("Post-processing complete!");
        }
        PacketSender.s2c(player).send(CLEAR_LOOTS, new RegistryFriendlyByteBuf(Unpooled.buffer(), player.server.registryAccess(), ConnectionType.NEOFORGE));
        if (EMILoot.config.parseChestLoot)
            chestSenders.forEach((id,chestSender) -> chestSender.send(player));
        if (EMILoot.config.parseBlockLoot)
            blockSenders.forEach((id,blockSender) -> blockSender.send(player));
        if (EMILoot.config.parseMobLoot)
            mobSenders.forEach((id,mobSender) -> mobSender.send(player));
        if (EMILoot.config.parseGameplayLoot)
            gameplaySenders.forEach((id,gameplaySender) -> gameplaySender.send(player));
        if (EMILoot.config.parseArchaeologyLoot)
            archaeologySenders.forEach((id, archaeologySender) -> archaeologySender.send(player));
    }

    public static void parseLootTables(LootDataManager manager, Map<LootDataId<?>, ?> tables) {
        keyLookUp.clear();
        LootTableParser.tables = tables;
        LootTableParser.lootManager = manager;
        for (LootDataId<?> key : LootTableParser.tables.keySet()){
            if (key.type() == LootDataType.TABLE)
                keyLookUp.put(key.location(),key);
        }
        parsedDirectDrops = new LinkedList<>();
        chestSenders.clear();
        blockSenders.clear();
        mobSenders.clear();
        gameplaySenders.clear();
        archaeologySenders.clear();
        EMILoot.LOGGER.info("parsing loot tables");
        tables.forEach((key, table) -> {
            if (table instanceof LootTable)
                parseLootTable(key.location(), (LootTable) table);
        });
        if (EMILoot.config.parseMobLoot) {
            ResourceLocation chk = Identifier.ofVanilla("pig");
            BuiltInRegistries.ENTITY_TYPE.stream().toList().forEach((type) -> {
                if (type == EntityType.SHEEP){
                    for (ResourceKey<LootTable> sheepId : ServerResourceData.SHEEP_TABLES){
                        parseEntityType(manager,type,sheepId,chk);
                    }
                }
                parseEntityType(manager,type,type.getDefaultLootTable(),chk);
            });
        }
        Multimap<ResourceLocation, LootTable> missedDrops = ServerResourceData.getMissedDirectDrops(parsedDirectDrops);
        for (Map.Entry<ResourceLocation,LootTable> entry : missedDrops.entries()){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("parsing missed direct drop table: " + entry.getKey());
            parseLootTable(entry.getKey(),entry.getValue());
        }
        EMILoot.LOGGER.info("finished parsing loot tables");
        hasParsedLootTables = true;
    }

    private static void parseLootTable(ResourceLocation id, LootTable lootTable){
        if (ServerResourceData.skipTable(id)) return;
        currentTable = id.toString();
        LootContextParamSet type = lootTable.getParamSet();
        if (type == LootContextParamSets.CHEST && EMILoot.config.parseChestLoot) {
            chestSenders.put(id, parseChestLootTable(lootTable,id));
        } else if (type == LootContextParamSets.BLOCK && EMILoot.config.parseBlockLoot) {
            blockSenders.put(id, parseBlockLootTable(lootTable,id));
        } else if ((type == LootContextParamSets.FISHING || type == LootContextParamSets.GIFT ||type == LootContextParamSets.PIGLIN_BARTER) && EMILoot.config.parseGameplayLoot){
            gameplaySenders.put(id, parseGameplayLootTable(lootTable, id));
        } else if ((type == LootContextParamSets.ARCHAEOLOGY && EMILoot.config.parseArchaeologyLoot)) {
            archaeologySenders.put(id, parseArchaeologyTable(lootTable, id));
        }
    }


    public static void postProcess(PostProcessor process){
        if (!hasParsedLootTables) return;
        for (LootSender<?> sender : chestSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntryContainer entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in chest sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : blockSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntryContainer entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in block sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : mobSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntryContainer entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in mob sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : gameplaySenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntryContainer entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in gameplay sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : archaeologySenders.values()) {
            for (LootBuilder builder : sender.getBuilders()) {
                for (LootPoolEntryContainer entry : builder.getEntriesToPostProcess(process)) {
                    if(EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in archaeology sender: " + sender.getId());
                    parseLootPoolEntry(builder, entry, process);
                }
            }
            sender.build();
        }
        postProcessors.put(process,true);
    }

    private static void parseEntityType(LootDataManager manager,EntityType<?> type, ResourceKey<LootTable> mobTableId, ResourceLocation fallback){
        ResourceLocation mobId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        LootTable mobTable = manager.getLootTable(mobTableId);
        if (type == EntityType.PIG && mobId.equals(fallback) || mobTable != LootTable.EMPTY) {
            currentTable = mobTableId.toString();
            mobSenders.put(mobTableId, parseMobLootTable(mobTable, mobTableId, mobId));
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Found empty mob table at id: " + mobTableId);
        }
    }

    private static ChestLootTableSender parseChestLootTable(LootTable lootTable, ResourceLocation id){
        ChestLootTableSender sender = new ChestLootTableSender(id);
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            NumberProvider rollProvider = pool.getRolls();
            float conditionalMultiplier = 1f;
            for (LootItemCondition condition : ((LootPoolAccessor) pool).getConditions()){
                if (condition instanceof LootItemRandomChanceCondition randomChanceLootCondition){
                    conditionalMultiplier *= randomChanceLootCondition.probability();
                }
            }
            float rollAvg = NumberProcessors.getRollAvg(rollProvider) * conditionalMultiplier;
            ChestLootPoolBuilder builder = new ChestLootPoolBuilder(rollAvg);
            List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntryContainer entry : entries) {
                    parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    private static BlockLootTableSender parseBlockLootTable(LootTable lootTable, ResourceLocation id){
        BlockLootTableSender sender = new BlockLootTableSender(id);
        parseBlockLootTableInternal(lootTable,sender, false);
        if (ServerResourceData.DIRECT_DROPS.containsKey(id) && EMILoot.config.mobLootIncludeDirectDrops){
            parsedDirectDrops.add(id);
            Collection<LootTable> directTables = ServerResourceData.DIRECT_DROPS.get(id);
            parseBlockDirectLootTable(directTables,sender);
        }
        return sender;
    }

    private static void parseBlockDirectLootTable(Collection<LootTable> tables, BlockLootTableSender sender){
        for (LootTable directTable : tables) {
            if (directTable != null) {
                parseBlockLootTableInternal(directTable, sender, true);
            }
        }
    }

    private static void parseBlockLootTableInternal(LootTable lootTable, BlockLootTableSender sender, boolean isDirect){
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            List<LootItemCondition> conditions = ((LootPoolAccessor) pool).getConditions();
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            List<LootItemFunction> functions = ((LootPoolAccessor) pool).getFunctions();
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootItemFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            NumberProvider rollProvider = pool.getRolls();
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            BlockLootPoolBuilder builder = new BlockLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntryContainer entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
    }

    private static MobLootTableSender parseMobLootTable(LootTable lootTable, ResourceLocation id, ResourceLocation mobId){
        MobLootTableSender sender = new MobLootTableSender(id, mobId);
        parseMobLootTableInternal(lootTable,sender, false);
        if (ServerResourceData.DIRECT_DROPS.containsKey(id) && EMILoot.config.mobLootIncludeDirectDrops){
            parsedDirectDrops.add(id);
            Collection<LootTable> directTables = ServerResourceData.DIRECT_DROPS.get(id);
            parseMobDirectLootTable(directTables,sender);
        }
        return sender;
    }

    private static void parseMobDirectLootTable(Collection<LootTable> tables, MobLootTableSender sender){
        for (LootTable directTable : tables) {
            if (directTable != null) {
                parseMobLootTableInternal(directTable, sender, true);
            }
        }
    }

    private static void parseMobLootTableInternal(LootTable lootTable, MobLootTableSender sender, boolean isDirect){
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            List<LootItemCondition> conditions = ((LootPoolAccessor) pool).getConditions();
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            List<LootItemFunction> functions = ((LootPoolAccessor) pool).getFunctions();
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootItemFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            NumberProvider rollProvider = pool.getRolls();
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            MobLootPoolBuilder builder = new MobLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntryContainer entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
    }

    private static GameplayLootTableSender parseGameplayLootTable(LootTable lootTable, ResourceLocation id){
        GameplayLootTableSender sender = new GameplayLootTableSender(id);
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            List<LootItemCondition> conditions = ((LootPoolAccessor) pool).getConditions();
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            List<LootItemFunction> functions = ((LootPoolAccessor) pool).getFunctions();
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootItemFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            NumberProvider rollProvider = pool.getRolls();
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            GameplayLootPoolBuilder builder = new GameplayLootPoolBuilder(rollAvg,parsedConditions,parsedFunctions);
            List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntryContainer entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }


    private static ArchaeologyLootTableSender parseArchaeologyTable(LootTable lootTable, ResourceLocation id) {
        ArchaeologyLootTableSender sender = new ArchaeologyLootTableSender(id);
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            NumberProvider rollProvider = pool.getRolls();
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            ArchaeologyLootPoolBuilder builder = new ArchaeologyLootPoolBuilder(rollAvg);
            List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntryContainer entry : entries) {
                parseLootPoolEntry(builder, entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntryContainer entry){
        parseLootPoolEntry(builder, entry,null);
    }

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntryContainer entry, @Nullable PostProcessor process){
        if(entry instanceof TagEntry tagEntry){
            if (process == PostProcessor.TAG){
                List<ItemEntryResult> result = parseTagEntry(tagEntry, false);
                result.forEach(builder::addItem);
            } else {
                builder.addEntryForPostProcessing(PostProcessor.TAG,tagEntry);
            }
        } else {
            List<ItemEntryResult> result = parseLootPoolEntry(entry,false);
            result.forEach(builder::addItem);
        }
    }

    static List<ItemEntryResult> parseLootPoolEntry(LootPoolEntryContainer entry, boolean parentIsAlternative){
        if (entry instanceof LootItem itemEntry) {
            return parseItemEntry(itemEntry, parentIsAlternative);
        } else if(entry instanceof AlternativesEntry alternativeEntry){
            return parseAlternativeEntry(alternativeEntry);
        }else if(entry instanceof EntryGroup groupEntry){
            return parseGroupEntry(groupEntry, parentIsAlternative);
        }else if(entry instanceof SequentialEntry sequenceEntry){
            return parseSequenceEntry(sequenceEntry, parentIsAlternative);
        } else if(entry instanceof TagEntry tagEntry){
            return  parseTagEntry(tagEntry, parentIsAlternative);
        } else if (entry instanceof LootTableReference lootTableEntry){
            return parseLootTableEntry(lootTableEntry, parentIsAlternative);
        }
        return List.of();
    }

    static List<ItemEntryResult> parseItemEntry(LootItem entry, boolean parentIsAlternative){
        int weight = ((LeafEntryAccessor) entry).getWeight();
        ItemStack item = new ItemStack(((ItemEntryAccessor) entry).getItem());
        List<LootItemFunction> functions = ((LeafEntryAccessor) entry).getFunctions();
        List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
        return parseItemEntry(weight, item, functions, conditions, parentIsAlternative);
    }

    static List<ItemEntryResult> parseItemEntry(int weight, ItemStack item, List<LootItemFunction> functions, List<LootItemCondition> conditions, boolean parentIsAlternative){
        FunctionApplierResult functionApplierResult = applyLootFunctionToItem(functions,item,weight,parentIsAlternative);
        List<ItemEntryResult> conditionalEntryResults = functionApplierResult.conditionalResults;
        List<TextKey> functionTexts = functionApplierResult.functionTexts;
        item = functionApplierResult.stack;
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,item,parentIsAlternative);
        List<ItemEntryResult> returnList = new LinkedList<>();
        returnList.add(new ItemEntryResult(item,weight,conditionsTexts,functionTexts));
        conditionalEntryResults.forEach(conditionalEntry->{
            conditionalEntry.conditions.addAll(conditionsTexts);
            conditionalEntry.functions.addAll(functionTexts);
            returnList.add(conditionalEntry);
        });

        return returnList;
    }

    static List<ItemEntryResult> parseTagEntry(TagEntry entry, boolean parentIsAlternative){
        TagKey<Item> items = ((TagEntryAccessor) entry).getName();
        if (EMILoot.DEBUG) EMILoot.LOGGER.info(">>> Parsing tag entry " + items.location());
        Iterable<Holder<Item>> itemsItr = BuiltInRegistries.ITEM.getTagOrEmpty(items);
        List<ItemEntryResult> returnList = new LinkedList<>();
        //if (EMILoot.DEBUG) EMILoot.LOGGER.info(itemsItr.toString());
        int weight = ((LeafEntryAccessor) entry).getWeight();
        List<LootItemFunction> functions = ((LeafEntryAccessor) entry).getFunctions();
        List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
        for (Holder<Item> item : itemsItr){
            ItemStack stack = new ItemStack(item.value());
            //if (EMILoot.DEBUG) EMILoot.LOGGER.info("> Stack: " + stack.getName());
            returnList.addAll(parseItemEntry(weight, stack, functions, conditions, parentIsAlternative));
        }
        return returnList;

    }

    static List<ItemEntryResult> parseAlternativeEntry(AlternativesEntry entry){
        List<LootPoolEntryContainer> children = ((CombinedEntryAccessor)entry).getChildren();
        List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,true);
        List<ItemEntryResult> results = new LinkedList<>();
        children.forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,true);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseGroupEntry(EntryGroup entry, boolean parentIsAlternative){
        List<LootPoolEntryContainer> children = ((CombinedEntryAccessor)entry).getChildren();
        List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,parentIsAlternative);
        List<ItemEntryResult> results = new LinkedList<>();
        children.forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,parentIsAlternative);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseSequenceEntry(SequentialEntry entry, boolean parentIsAlternative){
        List<LootPoolEntryContainer> children = ((CombinedEntryAccessor)entry).getChildren();
        List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,parentIsAlternative);
        List<ItemEntryResult> results = new LinkedList<>();
        TextKey sequenceCondition = TextKey.of("emi_loot.condition.sequence");
        children.forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,parentIsAlternative);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                resultEntry.conditions.add(sequenceCondition);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseLootTableEntry(LootTableReference entry, boolean parentIsAlternative){
        ResourceLocation id = ((LootTableEntryAccessor)entry).getId();
        if (LootTableParser.keyLookUp.containsKey(id)) {
            if (LootTableParser.tables.containsKey(keyLookUp.get(id))) {
                Object temp = LootTableParser.tables.get(keyLookUp.get(id));
                if (!(temp instanceof LootTable table)) return List.of();
                LootContextParamSet type = table.getParamSet();
                LootSender<?> results;
                List<LootItemCondition> conditions = ((LootPoolEntryAccessor) entry).getConditions();
                List<TextKey> conditionsTexts = parseLootConditionTexts(conditions, ItemStack.EMPTY, parentIsAlternative);
                if (type == LootContextParamSets.CHEST) {
                    results = parseChestLootTable(table, id);
                } else if (type == LootContextParamSets.BLOCK) {
                    results = parseBlockLootTable(table, id);
                } else if (type == LootContextParamSets.ENTITY) {
                    results = parseMobLootTable(table, id, new ResourceLocation("empty"));
                } else if (type == LootContextParamSets.FISHING) {
                    results = parseGameplayLootTable(table, id);
                } else {
                    results = new EmptyLootTableSender();
                }
                List<? extends LootBuilder> parsedBuilders = results.getBuilders();
                List<ItemEntryResult> parsedList = new LinkedList<>();
                parsedBuilders.forEach(parsedBuilder ->
                        parsedList.addAll(parsedBuilder.revert())
                );
                parsedList.forEach(result -> result.conditions.addAll(conditionsTexts));
                List<LootItemFunction> functions = ((LeafEntryAccessor) entry).getFunctions();
                return applyLootFunctionsToTableResults(functions, parsedList, parentIsAlternative);
            }
        }
        return List.of();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    static LootFunctionResult parseLootFunction(LootItemFunction function){
        return parseLootFunction(function, ItemStack.EMPTY,false);
    }

    static LootFunctionResult parseLootFunction(LootItemFunction function, ItemStack stack, boolean parentIsAlternative){
        LootItemFunctionType type;
        try {
            type = function.getType();
        } catch (Exception e){
            EMILoot.LOGGER.error("failed to determine a function type for stack " + stack.getHoverName() + " in table " + currentTable);
            e.printStackTrace();
            return LootFunctionResult.EMPTY;
        }
        List<TextKey> conditionsTexts;
        if (function instanceof LootItemConditionalFunction){
            List<LootItemCondition> conditions = ((ConditionalLootFunctionAccessor)function).getConditions();
            conditionsTexts = parseLootConditionTexts(conditions,stack,parentIsAlternative);
        } else {
            conditionsTexts = new LinkedList<>();
        }
        try {
            return LootParserRegistry.parseFunction(function,stack,type,parentIsAlternative,conditionsTexts);
        } catch(Exception e){
            EMILoot.LOGGER.error("Failed to parse LootCondition of type " + type + " for stack " + stack.getHoverName() + " in table " + currentTable);
            e.printStackTrace();
            return LootFunctionResult.EMPTY;
        }
    }

    private static List<ItemEntryResult> applyLootFunctionsToTableResults(List<LootItemFunction> functions, List<ItemEntryResult> parsedList, boolean parentIsAlternative){
        List<ItemEntryResult> conditionalEntryResults = new LinkedList<>();
        List<ItemEntryResult> processedEntryResults = new LinkedList<>();
        parsedList.forEach(itemEntry -> {
            FunctionApplierResult result = applyLootFunctionToItem(functions,itemEntry.item, itemEntry.weight, parentIsAlternative);
            List<TextKey> conditionTexts = itemEntry.conditions;
            List<TextKey> functionTexts = itemEntry.functions;
            functionTexts.addAll(result.functionTexts);
            processedEntryResults.add(new ItemEntryResult(result.stack, itemEntry.weight, conditionTexts,functionTexts));
            conditionalEntryResults.addAll(result.conditionalResults);
        });
        processedEntryResults.addAll(conditionalEntryResults);
        return processedEntryResults;
    }

    private static FunctionApplierResult applyLootFunctionToItem(List<LootItemFunction> functions, ItemStack item, int weight, boolean parentIsAlternative){
        List<TextKey> functionTexts = new LinkedList<>();
        List<ItemEntryResult> conditionalEntryResults = new LinkedList<>();
        for (LootItemFunction lootFunction : functions) {
            LootFunctionResult result = parseLootFunction(lootFunction, item, parentIsAlternative);
            TextKey lootText = result.text;
            ItemStack newStack = result.stack;
            List<TextKey> resultConditions = result.conditions;

            if (!resultConditions.isEmpty()){
                ItemStack conditionalItem;
                if (newStack != ItemStack.EMPTY){
                    conditionalItem = newStack;
                } else {
                    conditionalItem = item;
                }
                List<TextKey> conditionalFunctionTexts = new LinkedList<>();
                conditionalFunctionTexts.add(lootText);
                conditionalEntryResults.add(new ItemEntryResult(conditionalItem,weight,resultConditions,conditionalFunctionTexts));
            } else {
                if (lootText.isNotEmpty()) {
                    functionTexts.add(lootText);
                }
                if (newStack != ItemStack.EMPTY) {
                    item = newStack;
                }
            }
        }
        return new FunctionApplierResult(conditionalEntryResults,functionTexts,item);
    }

    ///////////////////////////////////////////////////////////////

    public static List<TextKey> parseLootConditionTexts(List<LootItemCondition> conditions, ItemStack item, boolean parentIsAlternative){
        List<TextKey> conditionsTexts = new LinkedList<>();
        List<LootConditionResult> parsedConditions = parseLootConditions(conditions, item, parentIsAlternative);
        for (LootConditionResult result: parsedConditions){
                    conditionsTexts.add(result.text);
        }
        return conditionsTexts;
    }

    public static List<LootConditionResult> parseLootConditions(List<LootItemCondition> conditions, ItemStack item, boolean parentIsAlternative){
        List<LootConditionResult> parsedConditions = new LinkedList<>();
        for (LootItemCondition condition: conditions){
            List<LootConditionResult> results = parseLootCondition(condition, item, parentIsAlternative);
            for (LootConditionResult result: results){
                if (result.text.isNotEmpty()){
                    parsedConditions.add(result);
                }
            }
        }
        return parsedConditions;
    }

    public static List<LootConditionResult> parseLootCondition(LootItemCondition condition, ItemStack stack){
        return parseLootCondition(condition, stack, false);
    }

    public static List<LootConditionResult> parseLootCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootItemConditionType type;
        try {
            type = condition.getType();
        } catch (Exception e){
            EMILoot.LOGGER.error("failed to determine a condition type for stack " + stack.getHoverName() + " in table " + currentTable);
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
        try {
            return LootParserRegistry.parseCondition(condition,type,stack,parentIsAlternative);
        } catch (Exception e){
            EMILoot.LOGGER.error("Failed to parse LootCondition of type " + condition.getType() + " for stack " + stack.getHoverName() + " in table " + currentTable);
            e.printStackTrace();
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Component compileConditionTexts(ItemStack stack,List<LootConditionResult> results){
        MutableComponent finalText = Text.empty();
        int size = results.size();
        for(int i = 0; i < size;i++){
            LootConditionResult result = results.get(i);
            Component resultText = result.text.process(stack,null).text();
            if (i == 0){
                finalText = resultText.copy();
            } else {
                finalText.append(resultText);
            }
            if (i<(size - 1)){
                finalText.append(Text.translatable("emi_loot.and"));
            }
        }
        return finalText;
    }

    public enum PostProcessor{
        TAG
    }

    public record FunctionApplierResult(List<ItemEntryResult> conditionalResults, List<TextKey> functionTexts, ItemStack stack){}

    public record LootFunctionResult(
            TextKey text,
            ItemStack stack,
            List<TextKey> conditions
    ){
        public static LootFunctionResult EMPTY = new LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, new LinkedList<>());
    }

    public record LootConditionResult(
            TextKey text
    ){
        public static LootConditionResult EMPTY = new LootConditionResult(TextKey.empty());

        public TextKey getText(){
            return text;
        }
    }

    public record ItemEntryResult(
            ItemStack item,
            int weight,
            List<TextKey> conditions,
            List<TextKey> functions
    ){}
}