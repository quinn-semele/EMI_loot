package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public class LocationPredicateParser {

    public static Component parseLocationPredicate(LocationPredicate predicate){
        Optional<LocationPredicate.PositionPredicate> position = predicate.position();
        if (position.isPresent()) {
            MinMaxBounds.Doubles x = position.get().x();
            if (!x.equals(MinMaxBounds.Doubles.ANY)) {
                return Text.translatable("emi_loot.location_predicate.x", x.min().orElse(null), x.max().orElse(null));
            }

            MinMaxBounds.Doubles y = position.get().y();
            if (!y.equals(MinMaxBounds.Doubles.ANY)) {
                return Text.translatable("emi_loot.location_predicate.y", y.min().orElse(null), y.max().orElse(null));
            }

            MinMaxBounds.Doubles z = position.get().z();
            if (!z.equals(MinMaxBounds.Doubles.ANY)) {
                return Text.translatable("emi_loot.location_predicate.z", z.min().orElse(null), z.max().orElse(null));
            }
        }

        Optional<ResourceKey<Level>> dim = predicate.dimension();
        if (dim.isPresent()){
            return Text.translatable("emi_loot.location_predicate.dim",dim.get().location().toString());
        }

        Optional<ResourceKey<Biome>> biome = predicate.biome();
        if (biome.isPresent()){
            return Text.translatable("emi_loot.location_predicate.biome",biome.get().location().toString());
        }

        Optional<ResourceKey<Structure>> structure = predicate.structure();
        if (structure.isPresent()){
            return Text.translatable("emi_loot.location_predicate.structure",structure.get().location().toString());
        }

        Optional<Boolean> smokey = predicate.smokey();
        if (smokey.isPresent()){
            if (smokey.get()){
                return Text.translatable("emi_loot.location_predicate.smoke_true");
            } else {
                return Text.translatable("emi_loot.location_predicate.smoke_false");
            }
        }

        Optional<LightPredicate> light = predicate.light();
        if (light.isPresent()) {
            return LightPredicateParser.parseLightPredicate(light.get());
        }

        Optional<BlockPredicate> block = predicate.block();
        if (block.isPresent()) {
            return BlockPredicateParser.parseBlockPredicate(block.get());
        }

        Optional<FluidPredicate> fluid = predicate.fluid();
        if (fluid.isPresent()){
            return FluidPredicateParser.parseFluidPredicate(fluid.get());
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable location predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
