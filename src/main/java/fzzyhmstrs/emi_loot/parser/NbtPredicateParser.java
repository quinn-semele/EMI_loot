package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class NbtPredicateParser {

    public static Component parseNbtPredicate(NbtPredicate predicate){
        CompoundTag nbt = predicate.tag();
        if (nbt.isEmpty()){
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable nbt predicate in table: "  + LootTableParser.currentTable);
            return Text.translatable("emi_loot.predicate.invalid");
        }
        return Text.translatable("emi_loot.entity_predicate.nbt", nbt.toString());
    }
}
