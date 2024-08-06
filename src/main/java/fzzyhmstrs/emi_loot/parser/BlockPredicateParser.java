package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import java.util.List;
import java.util.Optional;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockPredicateParser {

    public static Component parseBlockPredicate(BlockPredicate predicate){
        return Text.translatable("emi_loot.block_predicate.base", parseBlockPredicateInternal(predicate).getString());
    }

    private static Component parseBlockPredicateInternal(BlockPredicate predicate){
        Optional<TagKey<Block>> tag = predicate.blocks().flatMap(HolderSet::unwrapKey);
        if (tag.isPresent()){
            return Text.translatable("emi_loot.block_predicate.tag",tag.get().location().toString());
        }

        Optional<HolderSet<Block>> blocks = predicate.blocks();
        if (blocks.isPresent() && blocks.get().size() > 0){
            List<MutableComponent> list = blocks.get().stream().map(entry -> entry.value().getName()).toList();
            return Text.translatable("emi_loot.block_predicate.list_1", ListProcessors.buildOrList(list));
        }

        Optional<StatePropertiesPredicate> statePredicate = predicate.properties();
        if (statePredicate.isPresent()){
            return StatePredicateParser.parseStatePredicate(statePredicate.get());
        }

        Optional<NbtPredicate> nbt = predicate.nbt();
        if (nbt.isPresent()){
            return NbtPredicateParser.parseNbtPredicate(nbt.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable block predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
}
