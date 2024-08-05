package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.StatePredicateParser;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.text.MutableText;

import java.util.Collections;
import java.util.List;

public class BlockStatePropertyConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        MutableText bsText;
        Block block = ((BlockStatePropertyLootCondition)condition).block().value();
        if (block != null){
            bsText = Text.translatable("emi_loot.condition.blockstate.block",block.getName().getString());
        } else {
            StatePredicate predicate = ((BlockStatePropertyLootCondition)condition).properties().orElseThrow(); // TODO?
            bsText = (MutableText) StatePredicateParser.parseStatePredicate(predicate);
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.blockstate",bsText.getString())));
    }
}
