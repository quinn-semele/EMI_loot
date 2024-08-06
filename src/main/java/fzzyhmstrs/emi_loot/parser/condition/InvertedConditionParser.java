package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class InvertedConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootItemCondition term = ((InvertedLootItemCondition)condition).term();
        List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
        List<LootTableParser.LootConditionResult> finalResults = new LinkedList<>();
        termResults.forEach((result)->{
            Component resultText = result.getText().process(stack,null).text();
            finalResults.add(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.invert",resultText.getString())));
        });
        return finalResults;
    }
}
