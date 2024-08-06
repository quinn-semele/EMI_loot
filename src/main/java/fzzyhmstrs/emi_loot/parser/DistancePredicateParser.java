package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;

public class DistancePredicateParser{

    public static Component parseDistancePredicate(DistancePredicate predicate){
        MinMaxBounds.Doubles abs = predicate.absolute();
        if (!abs.equals(MinMaxBounds.Doubles.ANY)){
            return Text.translatable("emi_loot.entity_predicate.distance_abs",abs.min().orElse(null),abs.max().orElse(null));
        }
        MinMaxBounds.Doubles hor = predicate.horizontal();
        if (!hor.equals(MinMaxBounds.Doubles.ANY)){
            return Text.translatable("emi_loot.entity_predicate.distance_hor",hor.min().orElse(null),hor.max().orElse(null));
        }
        MinMaxBounds.Doubles x = predicate.x();
        if (!x.equals(MinMaxBounds.Doubles.ANY)){
            return Text.translatable("emi_loot.entity_predicate.distance_x",x.min().orElse(null),x.max().orElse(null));
        }
        MinMaxBounds.Doubles y = predicate.y();
        if (!y.equals(MinMaxBounds.Doubles.ANY)){
            return Text.translatable("emi_loot.entity_predicate.distance_y",y.min().orElse(null),y.max().orElse(null));
        }
        MinMaxBounds.Doubles z = predicate.z();
        if (!z.equals(MinMaxBounds.Doubles.ANY)){
            return Text.translatable("emi_loot.entity_predicate.distance_z",z.min().orElse(null),z.max().orElse(null));
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unparsable distance predicate in table: " + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
