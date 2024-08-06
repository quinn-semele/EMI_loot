package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.AlternativeLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AlternativesConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        List<LootItemCondition> terms = ((AlternativeLootConditionAccessor)condition).getConditions();
        int size = terms.size();
        if (size == 1){
            List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(terms.getFirst(), stack);
            Component termText = LootTableParser.compileConditionTexts(stack,termResults);
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates",termText.getString())));
        } else if (size == 2){
            List<LootTableParser.LootConditionResult> termResults1 = LootTableParser.parseLootCondition(terms.getFirst(), stack);
            List<LootTableParser.LootConditionResult> termResults2 = LootTableParser.parseLootCondition(terms.getLast(), stack);
            Component termText1 = LootTableParser.compileConditionTexts(stack,termResults1);
            Component termText2;
            if (termResults2.size() == 1){
                TextKey key = termResults2.getFirst().getText();
                if (key.args().size() == 1){
                    termText2 = Component.nullToEmpty(key.args().getFirst());
                } else {
                    termText2 = LootTableParser.compileConditionTexts(stack,termResults2);
                }
            } else {
                termText2 = LootTableParser.compileConditionTexts(stack, termResults2);
            }
            List<String> args = new LinkedList<>(Arrays.stream(new String[]{termText1.getString(), termText2.getString()}).toList());
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates_2",args)));
        } else {
            List<String> args = new LinkedList<>();
            terms.forEach((term)-> {
                List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
                Component termText = LootTableParser.compileConditionTexts(stack,termResults);
                args.add(termText.getString());
            });
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates_3",args)));
        }
    }
}
