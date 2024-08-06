package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import java.util.Optional;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidPredicateParser {

    public static Component parseFluidPredicate(FluidPredicate predicate){
        return Text.translatable("emi_loot.fluid_predicate.base",parseFluidPredicateInternal(predicate).getString());
    }

    private static Component parseFluidPredicateInternal(FluidPredicate predicate){

        Optional<TagKey<Fluid>> tag = predicate.fluids().flatMap(HolderSet::unwrapKey);
        if (tag.isPresent()){
            return Text.translatable("emi_loot.fluid_predicate.tag",tag.get().location().toString());
        }

        // todo: check size == 1
        Optional<Holder<Fluid>> fluid = predicate.fluid();
        if (fluid.isPresent()){
            return Text.translatable("emi_loot.fluid_predicate.fluid", BuiltInRegistries.FLUID.getKey(fluid.get().value()).toString());
        }

        Optional<StatePropertiesPredicate> statePredicate = predicate.properties();
        if (statePredicate.isPresent()){
            return StatePredicateParser.parseStatePredicate(statePredicate.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable fluid predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
