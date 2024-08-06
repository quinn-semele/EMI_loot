package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.LightPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class LightPredicateParser{

    public static Component parseLightPredicate(LightPredicate predicate){
        MinMaxBounds.Ints range = predicate.composite();
        if (range.equals(MinMaxBounds.Ints.ANY)) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Undefined light predicate in table: "  + LootTableParser.currentTable);
            return Text.translatable("emi_loot.predicate.invalid");
        }
        Integer min = range.min().orElse(null);
        Integer max = range.max().orElse(null);
        if (Objects.equals(min, max) && min != null) {
            return Text.translatable("emi_loot.location_predicate.light", min);
        } else {
            return Text.translatable("emi_loot.location_predicate.light_2", min, max);
        }
    }
}
