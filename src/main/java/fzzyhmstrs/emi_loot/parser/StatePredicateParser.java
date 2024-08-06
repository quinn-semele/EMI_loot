package fzzyhmstrs.emi_loot.parser;


import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.LinkedList;
import java.util.List;

public class StatePredicateParser {
  
    public static Component parseStatePredicate(StatePropertiesPredicate predicate){
        List<StatePropertiesPredicate.PropertyMatcher> list = predicate.properties();
        if (!list.isEmpty()){
            List<MutableComponent> list2 = new LinkedList<>();
            for (StatePropertiesPredicate.PropertyMatcher condition : list){
                if (condition.valueMatcher() instanceof StatePropertiesPredicate.RangedMatcher){
                    String key = condition.name();
                    String min = ((StatePropertiesPredicate.RangedMatcher) condition.valueMatcher()).minValue().orElse(null);
                    String max = ((StatePropertiesPredicate.RangedMatcher) condition.valueMatcher()).maxValue().orElse(null);
                    list2.add(Text.translatable("emi_loot.state_predicate.state_between",key,min,max));
                } else if (condition.valueMatcher() instanceof StatePropertiesPredicate.ExactMatcher){
                    String key = condition.name();
                    String value = ((StatePropertiesPredicate.ExactMatcher) condition.valueMatcher()).value();
                    list2.add(Text.translatable("emi_loot.state_predicate.state_exact",key,value));
                }
            }
            return Text.translatable("emi_loot.state_predicate.base", ListProcessors.buildAndList(list2));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable block/fluid state predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
  
}
