package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EnchantmentPredicateParser{

    public static Component parseEnchantmentPredicates(List<EnchantmentPredicate> list){
        List<MutableComponent> list2 = new LinkedList<>();
        for (EnchantmentPredicate predicate : list){
            Optional<HolderSet<Enchantment>> enchantments = predicate.enchantments();
            if (enchantments.isPresent()) {
                for (Holder<Enchantment> enchantment : enchantments.get()) {
                    list2.add((MutableComponent) Enchantment.getFullname(enchantment, 1));
                }
            }
        }
        if (!list2.isEmpty()){
            return Text.translatable("emi_loot.item_predicate.enchant", ListProcessors.buildAndList(list2));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable enchantment predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
}
