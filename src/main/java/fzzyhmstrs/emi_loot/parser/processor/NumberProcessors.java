package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.BoundedIntUnaryOperatorAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.providers.number.*;

import java.util.Objects;
import java.util.Optional;

public class NumberProcessors {

    public static MutableComponent processBoolean(Boolean input, String keyTrue, String keyFalse, Object ... args){
        if (input != null){
            if (input){
                return Text.translatable(keyTrue, args);
            } else {
                return Text.translatable(keyFalse, args);
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Boolean null for keys: " + keyTrue + " / " + keyFalse + " in table: "  + LootTableParser.currentTable);
        return Text.empty();
    }

    public static MutableComponent processNumberRange(MinMaxBounds<?> range, String exact, String between, String atLeast, String atMost, String fallback, Object ... args){
        if (!range.equals(MinMaxBounds.Ints.ANY) && !range.equals(MinMaxBounds.Doubles.ANY)){
            Optional<? extends Number> min = range.min();
            Optional<? extends Number> max = range.max();
            if (Objects.equals(min, max) && min.isPresent()){
                return Text.translatable(exact, min.get(), args);
            } else if (min.isPresent() && max.isPresent()) {
                return Text.translatable(between, min.get(), max.get(), args);
            }else if (min.isPresent()) {
                return Text.translatable(atLeast, min.get(), args);
            }else if (max.isPresent()) {
                return Text.translatable(atMost, max.get(), args);
            } else {
                if (fallback.isEmpty()) return Text.empty();
                return Text.translatable(fallback);
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Non-specific number range for keys: " + exact + " / " + between + " in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

    public static MutableComponent processBoundedIntUnaryOperator(IntRange operator){
        NumberProvider min = ((BoundedIntUnaryOperatorAccessor)operator).getMin();
        NumberProvider max = ((BoundedIntUnaryOperatorAccessor)operator).getMax();
        if (min != null && max != null){
            if (min.getType() == NumberProviders.CONSTANT && max.getType() == NumberProviders.CONSTANT) {
                float minVal = ((ConstantValue)min).value();
                float maxVal = ((ConstantValue)max).value();
                if (minVal == maxVal) {
                    return processLootNumberProvider(min);
                }
            }
            return Text.translatable("emi_loot.operator.between", processLootNumberProvider(min), processLootNumberProvider(max));

        } else if (min != null){
            return Text.translatable("emi_loot.operator.min", processLootNumberProvider(min));
        } else if (max != null){
            return Text.translatable("emi_loot.operator.max", processLootNumberProvider(max));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Null or undefined bounded int unary operator in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.operator.unknown");
    }

    public static MutableComponent processLootNumberProvider(NumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == NumberProviders.CONSTANT){
            return Text.translatable ("emi_loot.number_provider.constant",((ConstantValue)provider).value());
        } else if(type == NumberProviders.BINOMIAL){
            NumberProvider n = ((BinomialDistributionGenerator)provider).n();
            NumberProvider p = ((BinomialDistributionGenerator)provider).p();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            MutableComponent nValText = processLootNumberProvider(n);
            MutableComponent pValText = processLootNumberProvider(p);
            float avg = nVal * pVal;
            return Text.translatable("emi_loot.number_provider.binomial",nValText,pValText,avg);
        } else if(type == NumberProviders.UNIFORM){
            NumberProvider min = ((UniformGenerator)provider).min();
            NumberProvider max = ((UniformGenerator)provider).max();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            MutableComponent minValText = processLootNumberProvider(min);
            MutableComponent maxValText = processLootNumberProvider(max);
            float avg = (minVal + maxVal) / 2f;
            return Text.translatable("emi_loot.number_provider.uniform",minValText,maxValText,avg);
        } else if (type == NumberProviders.SCORE){
            //LootScoreProvider lootScoreProvider = ((ScoreLootNumberProvider)provider).target();
            String lootScore = ((ScoreboardValue)provider).score();
            float lootScale = ((ScoreboardValue)provider).scale();
            return Text.translatable("emi_loot.number_provider.score",lootScore,lootScale);
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Non-specific or undefined number provider in table: "  + LootTableParser.currentTable);
            return Text.translatable("emi_loot.number_provider.unknown");
        }
    }

    public static float getRollAvg(NumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == NumberProviders.CONSTANT){
            return ((ConstantValue)provider).value();
        } else if(type == NumberProviders.BINOMIAL){
            NumberProvider n = ((BinomialDistributionGenerator)provider).n();
            NumberProvider p = ((BinomialDistributionGenerator)provider).p();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            return nVal * pVal;
        } else if(type == NumberProviders.UNIFORM){
            NumberProvider min = ((UniformGenerator)provider).min();
            NumberProvider max = ((UniformGenerator)provider).max();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            return (minVal + maxVal) / 2f;
        } else if (type == NumberProviders.SCORE){
            return 0f;
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Loot number provider with unknown type: " + provider.getType().toString());
            return 0f;
        }
    }


}
