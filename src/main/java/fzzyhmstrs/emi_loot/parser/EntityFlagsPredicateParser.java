package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import java.util.Optional;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.network.chat.Component;

public class EntityFlagsPredicateParser{


    public static Component parseEntityFlagsPredicate(EntityFlagsPredicate predicate){
        return Text.translatable("emi_loot.entity_predicate.flag",parseEntityFlagsPredicateInternal(predicate).getString());
    }

    private static Component parseEntityFlagsPredicateInternal(EntityFlagsPredicate predicate){
        Optional<Boolean> isOnFire = predicate.isOnFire();
        if (isOnFire.isPresent()){
            if (isOnFire.get()){
                return Text.translatable("emi_loot.entity_predicate.fire_true");
            } else {
                return Text.translatable("emi_loot.entity_predicate.fire_false");
            }
        }

        Optional<Boolean> isSneaking = predicate.isCrouching();
        if (isSneaking.isPresent()){
            if (isSneaking.get()){
                return Text.translatable("emi_loot.entity_predicate.sneak_true");
            } else {
                return Text.translatable("emi_loot.entity_predicate.sneak_false");
            }
        }

        Optional<Boolean> isSprinting = predicate.isSprinting();
        if (isSprinting.isPresent()){
            if (isSprinting.get()){
                return Text.translatable("emi_loot.entity_predicate.sprint_true");
            } else {
                return Text.translatable("emi_loot.entity_predicate.sprint_false");
            }
        }

        Optional<Boolean> isSwimming = predicate.isSwimming();
        if (isSwimming.isPresent()){
            if (isSwimming.get()){
                return Text.translatable("emi_loot.entity_predicate.swim_true");
            } else {
                return Text.translatable("emi_loot.entity_predicate.swim_false");
            }
        }

        Optional<Boolean> isBaby = predicate.isBaby();
        if (isBaby.isPresent()){
            if (isBaby.get()){
                return Text.translatable("emi_loot.entity_predicate.baby_true");
            } else {
                return Text.translatable("emi_loot.entity_predicate.baby_false");
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity flags predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
