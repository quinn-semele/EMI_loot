package fzzyhmstrs.emi_loot.parser;


import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.StatePredicate.Condition;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;

public class StatePredicateParser {
  
    public static Text parseStatePredicate(StatePredicate predicate){
        List<Condition> list = predicate.conditions();
        if (!list.isEmpty()){
            List<MutableText> list2 = new LinkedList<>();
            for (Condition condition : list){
                if (condition.valueMatcher() instanceof StatePredicate.RangedValueMatcher){
                    String key = condition.key();
                    String min = ((StatePredicate.RangedValueMatcher) condition.valueMatcher()).min().orElse(null);
                    String max = ((StatePredicate.RangedValueMatcher) condition.valueMatcher()).max().orElse(null);
                    list2.add(Text.translatable("emi_loot.state_predicate.state_between",key,min,max));
                } else if (condition.valueMatcher() instanceof StatePredicate.ExactValueMatcher){
                    String key = condition.key();
                    String value = ((StatePredicate.ExactValueMatcher) condition.valueMatcher()).value();
                    list2.add(Text.translatable("emi_loot.state_predicate.state_exact",key,value));
                }
            }
            return Text.translatable("emi_loot.state_predicate.base", ListProcessors.buildAndList(list2));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable block/fluid state predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
  
}
