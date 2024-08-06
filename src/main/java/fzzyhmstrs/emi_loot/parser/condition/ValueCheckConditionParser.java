package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ValueCheckConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        NumberProvider value = ((ValueCheckCondition)condition).provider();
        String processedValue = NumberProcessors.processLootNumberProvider(value).getString();
        IntRange range = ((ValueCheckCondition)condition).range();
        String processedRange = NumberProcessors.processBoundedIntUnaryOperator(range).getString();
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.value_check",processedValue,processedRange)));
    }
}
