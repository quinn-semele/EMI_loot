package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.StatePredicateParser;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collections;
import java.util.List;

public class BlockStatePropertyConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        MutableComponent bsText;
        Block block = ((LootItemBlockStatePropertyCondition)condition).block().value();
        if (block != null){
            bsText = Text.translatable("emi_loot.condition.blockstate.block",block.getName().getString());
        } else {
            StatePropertiesPredicate predicate = ((LootItemBlockStatePropertyCondition)condition).properties().orElseThrow(); // TODO?
            bsText = (MutableComponent) StatePredicateParser.parseStatePredicate(predicate);
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.blockstate",bsText.getString())));
    }
}
