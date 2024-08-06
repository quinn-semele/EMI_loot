package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collections;
import java.util.List;

public interface ConditionParser{
    ConditionParser EMPTY = (condition, stack, parentIsAlternative) -> Collections.singletonList(LootTableParser.LootConditionResult.EMPTY);
    
    List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative);
}
