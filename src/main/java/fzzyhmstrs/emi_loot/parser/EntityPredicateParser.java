package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.*;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class EntityPredicateParser {

    public static Component parseEntityPredicate(EntityPredicate predicate){
        return Text.translatable("emi_loot.entity_predicate.base",parseEntityPredicateInternal(predicate).getString());
    }

    private static Component parseEntityPredicateInternal(EntityPredicate predicate){
        //if (predicate.equals(EntityPredicate.ANY)) {
        //    if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Entity predicate empty in table: "  + LootTableParser.currentTable);
        //    return LText.empty();
        //}

        //entity type check
        Optional<EntityTypePredicate> typePredicate = predicate.entityType();
        if (typePredicate.isPresent()) {
           return EntityTypePredicateParser.parseEntityTypePredicate(typePredicate.get());
        }

        //distance check
        Optional<DistancePredicate> distancePredicate = predicate.distanceToPlayer();
        if (distancePredicate.isPresent()){
            return DistancePredicateParser.parseDistancePredicate(distancePredicate.get());
        }

        //location check
        Optional<LocationPredicate> locationPredicate = predicate.location().located();
        if (locationPredicate.isPresent()){
            return LocationPredicateParser.parseLocationPredicate(locationPredicate.get());
        }

        //stepping on check
        Optional<LocationPredicate> steppingOnPredicate = predicate.location().steppingOn();
        if (steppingOnPredicate.isPresent()){
            return LocationPredicateParser.parseLocationPredicate(locationPredicate.get());
        }

        //effects check
        Optional<MobEffectsPredicate> entityEffectPredicate = predicate.effects();
        if (entityEffectPredicate.isPresent()){
            return EntityEffectPredicateParser.parseEntityEffectPredicate(entityEffectPredicate.get());
        }

        //nbt check
        Optional<NbtPredicate> nbt = predicate.nbt();
        if (nbt.isPresent()){
            return NbtPredicateParser.parseNbtPredicate(nbt.get());
        }

        //flags check
        Optional<EntityFlagsPredicate> entityFlagsPredicate = predicate.flags();
        if (entityFlagsPredicate.isPresent()){
            return EntityFlagsPredicateParser.parseEntityFlagsPredicate(entityFlagsPredicate.get());
        }

        //equipment check
        Optional<EntityEquipmentPredicate> entityEquipmentPredicate = predicate.equipment();
        if (entityEquipmentPredicate.isPresent()){
            return EntityEquipmentPredicateParser.parseEntityEquipmentPredicate(entityEquipmentPredicate.get());
        }

        //Type Specific checks
        Optional<EntitySubPredicate> typeSpecificPredicate = predicate.subPredicate();
        if (typeSpecificPredicate.isPresent()){
            return TypeSpecificPredicateParser.parseTypeSpecificPredicate(typeSpecificPredicate.get());
        }

        //vehicle checks
        Optional<EntityPredicate> vehicle = predicate.vehicle();
        if (vehicle.isPresent()){
            return EntityPredicateParser.parseEntityPredicate(vehicle.get());
        }

        //passenger checks
        Optional<EntityPredicate> passenger = predicate.passenger();
        if (passenger.isPresent()){
            return EntityPredicateParser.parseEntityPredicate(passenger.get());
        }

        //targeted entity checks
        Optional<EntityPredicate> targetedEntity = predicate.targetedEntity();
        if (targetedEntity.isPresent()){
            return EntityPredicateParser.parseEntityPredicate(targetedEntity.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Entity predicate undefined in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");

    }

}
