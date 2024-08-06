package fzzyhmstrs.emi_loot.parser.registry;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.condition.*;
import fzzyhmstrs.emi_loot.parser.function.*;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class LootParserRegistry{

    private static final Map<LootItemConditionType, ConditionParser> CONDITION_PARSERS = new HashMap<>();
    private static final Map<LootItemFunctionType, FunctionParser> FUNCTION_PARSERS = new HashMap<>();

    public static void registerCondition(LootItemConditionType type, ConditionParser parser, String registrationContext){
        if (!CONDITION_PARSERS.containsKey(type)){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering condition for type: " + type + " from: " + registrationContext);
            CONDITION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate condition registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static void registerFunction(LootItemFunctionType type, FunctionParser parser, String registrationContext){
        if (!FUNCTION_PARSERS.containsKey(type)){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering function for type: " + type + " from: " + registrationContext);
            FUNCTION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate function registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, LootItemConditionType type, ItemStack stack, boolean parentIsAlternative){
        ConditionParser parser = CONDITION_PARSERS.getOrDefault(type,ConditionParser.EMPTY);
        return parser.parseCondition(condition,stack, parentIsAlternative);
    }
    
    public static LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack, LootItemFunctionType type,boolean parentIsAlternative, List<TextKey> conditionTexts){
        FunctionParser parser = FUNCTION_PARSERS.getOrDefault(type,FunctionParser.EMPTY);
        return parser.parseFunction(function,stack,parentIsAlternative, conditionTexts);
    }

    static{

        registerFunction(LootItemFunctions.APPLY_BONUS,new ApplyBonusFunctionParser(),"Registering vanilla apply bonus function parser");
        registerFunction(LootItemFunctions.SET_POTION,new SetPotionFunctionParser(),"Registering vanilla set potion function parser");
        registerFunction(LootItemFunctions.SET_COUNT,new SetCountFunctionParser(),"Registering vanilla set count function parser");
        registerFunction(LootItemFunctions.ENCHANT_WITH_LEVELS,new EnchantWithLevelsFunctionParser(),"Registering vanilla enchant with levels function parser");
        registerFunction(LootItemFunctions.ENCHANT_RANDOMLY,new EnchantRandomlyFunctionParser(),"Registering vanilla enchant randomly function parser");
        registerFunction(LootItemFunctions.SET_ENCHANTMENTS,new SetEnchantmentsFunctionParser(),"Registering vanilla set enchantments function parser");
        registerFunction(LootItemFunctions.FURNACE_SMELT,new SimpleFunctionParser("emi_loot.function.smelt"),"Registering vanilla furnace smelt function parser");
        registerFunction(LootItemFunctions.ENCHANTED_COUNT_INCREASE,new SimpleFunctionParser("emi_loot.function.looting"),"Registering vanilla looting function parser");
        registerFunction(LootItemFunctions.EXPLORATION_MAP,new ExplorationMapFunctionParser(),"Registering vanilla exploration map function parser");
        registerFunction(LootItemFunctions.SET_NAME,new SetNameFunctionParser(),"Registering vanilla set name function parser");
        registerFunction(LootItemFunctions.SET_CONTENTS,new SimpleFunctionParser("emi_loot.function.set_contents"),"Registering vanilla set contents function parser");
        registerFunction(LootItemFunctions.SET_DAMAGE,new SetDamageFunctionParser(),"Registering vanilla set damage function parser");
        registerFunction(LootItemFunctions.SET_INSTRUMENT,new SetInstrumentFunctionParser(),"Registering vanilla set instrument function parser");
        registerFunction(LootItemFunctions.COPY_STATE,new SimpleFunctionParser("emi_loot.function.copy_state"),"Registering vanilla copy state function parser");
        registerFunction(LootItemFunctions.COPY_NAME,new CopyNameFunctionParser(),"Registering vanilla copy name function parser");
        registerFunction(LootItemFunctions.COPY_COMPONENTS,new SimpleFunctionParser("emi_loot.function.copy_nbt"),"Registering vanilla copy nbt function parser");
        registerFunction(LootItemFunctions.EXPLOSION_DECAY,new ExplosionDecayFunctionParser(),"Registering vanilla explosion decay function parser");
        registerFunction(LootItemFunctions.FILL_PLAYER_HEAD,new SimpleFunctionParser("emi_loot.function.fill_player_head"),"Registering vanilla fill-player-head function parser");
        registerFunction(LootItemFunctions.LIMIT_COUNT,new LimitCountFunctionParser(),"Registering vanilla limit-count function parser");
        registerFunction(LootItemFunctions.SET_ATTRIBUTES,new SetAttributesFunctionParser(),"Registering vanilla set attributes function parser");
        registerFunction(LootItemFunctions.SET_BANNER_PATTERN,new SimpleFunctionParser("emi_loot.function.banner"),"Registering vanilla set banner function parser");
        registerFunction(LootItemFunctions.SET_LORE,new SimpleFunctionParser("emi_loot.function.lore"),"Registering vanilla set lore function parser");
        registerFunction(LootItemFunctions.SET_STEW_EFFECT,new SetStewFunctionParser(),"Registering vanilla set stew effect function parser");
        registerFunction(LootItemFunctions.SET_COMPONENTS,new SimpleFunctionParser("emi_loot.function.set_nbt"),"Registering vanilla set nbt function parser");
        registerFunction(LootItemFunctions.SET_LOOT_TABLE,new SetLootTableFunctionParser(),"Registering vanilla set loot table function parser");
        //registerFunction(EMILoot.OMINOUS_BANNER,new OminousBannerFunctionParser(),"Registering Lootify ominous banner function parser");
        //registerFunction(EMILoot.SET_ANY_DAMAGE,new SetAnyDamageFunctionParser(),"Registering Lootify set-any-damage function parser");

        registerCondition(LootItemConditions.SURVIVES_EXPLOSION, new SurvivesExplosionConditionParser(),"Registering vanilla survives-explosion condition parser");
        registerCondition(LootItemConditions.BLOCK_STATE_PROPERTY, new BlockStatePropertyConditionParser(),"Registering vanilla block state property condition parser");
        registerCondition(LootItemConditions.TABLE_BONUS, new TableBonusConditionParser(),"Registering vanilla table bonus condition parser");
        registerCondition(LootItemConditions.INVERTED, new InvertedConditionParser(),"Registering vanilla inverted condition parser");
        registerCondition(LootItemConditions.KILLED_BY_PLAYER, new SimpleConditionParser("emi_loot.condition.killed_player"),"Registering vanilla killed-by-player condition parser");
        registerCondition(LootItemConditions.RANDOM_CHANCE, new RandomChanceConditionParser(),"Registering vanilla random chance condition parser");
        registerCondition(LootItemConditions.RANDOM_CHANCE_WITH_ENCHANTED_BONUS, new RandomChanceWithLootingConditionParser(),"Registering vanilla random chance with looting condition parser");
        registerCondition(LootItemConditions.DAMAGE_SOURCE_PROPERTIES, new DamageSourceConditionParser(),"Registering vanilla damage source properties condition parser");
        registerCondition(LootItemConditions.LOCATION_CHECK, new LocationCheckConditionParser(),"Registering vanilla location check condition parser");
        registerCondition(LootItemConditions.ENTITY_PROPERTIES, new EntityPropertiesConditionParser(),"Registering vanilla entity properties condition parser");
        registerCondition(LootItemConditions.MATCH_TOOL, new MatchToolConditionParser(),"Registering vanilla match-tool condition parser");
        registerCondition(LootItemConditions.ENTITY_SCORES, new SimpleConditionParser("emi_loot.condition.entity_scores"),"Registering vanilla entity scores condition parser");
        registerCondition(LootItemConditions.REFERENCE, new ReferenceConditionParser(),"Registering vanilla reference condition parser");
        registerCondition(LootItemConditions.TIME_CHECK, new TimeCheckConditionParser(),"Registering vanilla time check condition parser");
        registerCondition(LootItemConditions.VALUE_CHECK, new ValueCheckConditionParser(),"Registering vanilla value check condition parser");
        registerCondition(LootItemConditions.WEATHER_CHECK, new WeatherCheckConditionParser(),"Registering vanilla weather check condition parser");
        //registerCondition(EMILoot.SPAWNS_WITH, new SimpleConditionParser("emi_loot.condition.spawns_with"),"Registering lootify spawns with condition parser");
        //registerCondition(EMILoot.CREEPER, new SimpleConditionParser("emi_loot.condition.creeper"),"Registering lootify creeper condition parser");
        //registerCondition(EMILoot.WITHER_KILL, new SimpleConditionParser("emi_loot.condition.wither_kill"),"Registering lootify wither-killed condition parser");
    }
}















