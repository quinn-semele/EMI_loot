package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.TimeCheck;

public class TimeCheckConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        Optional<Long> period = ((TimeCheck)condition).period(); // TODO?
        IntRange value = ((TimeCheck)condition).value();
        String processedValue = NumberProcessors.processBoundedIntUnaryOperator(value).getString();
        if (period.isPresent()){
            return Collections.singletonList(
                    new LootTableParser.LootConditionResult(TextKey.of(
                            "emi_loot.condition.time_check_period",
                            period.get().toString(),
                            processedValue
                    )
                    )
            );
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.time_check",processedValue)));
    }
}
