package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

import java.util.Optional;

public class FluidPredicateParser {

    public static Text parseFluidPredicate(FluidPredicate predicate){
        return Text.translatable("emi_loot.fluid_predicate.base",parseFluidPredicateInternal(predicate).getString());
    }

    private static Text parseFluidPredicateInternal(FluidPredicate predicate){

        Optional<TagKey<Fluid>> tag = predicate.fluids().flatMap(RegistryEntryList::getTagKey);
        if (tag.isPresent()){
            return Text.translatable("emi_loot.fluid_predicate.tag",tag.get().id().toString());
        }

        Optional<RegistryEntryList<Fluid>> fluids = predicate.fluids();
        if (fluids.isPresent()){
            return Text.translatable("emi_loot.fluid_predicate.fluid", Registries.FLUID.getId(fluids.get().value()).toString());
        }

        Optional<StatePredicate> statePredicate = predicate.state();
        if (statePredicate.isPresent()){
            return StatePredicateParser.parseStatePredicate(statePredicate.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable fluid predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
