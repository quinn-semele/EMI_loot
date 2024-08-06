package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ItemPredicateParser {

    public static Component parseItemPredicate(ItemPredicate predicate){
        Optional<TagKey<Item>> tag = predicate.items().flatMap(HolderSet::unwrapKey);
        if (tag.isPresent()){
            return Text.translatable("emi_loot.item_predicate.tag",tag.get().location());
        }
        
        Optional<HolderSet<Item>> items = predicate.items();
        if (items.isPresent() && items.get().size() > 0){
            List<MutableComponent> list = items.get().stream().map((item) -> (MutableComponent)item.value().getDescription()).toList();
            return Text.translatable("emi_loot.item_predicate.items", ListProcessors.buildOrList(list));
        }
        
        MinMaxBounds.Ints count = predicate.count();
        if (count != MinMaxBounds.Ints.ANY){
            int finalMax = count.max().orElse(0);
            int finalMin = count.min().orElse(0);
            return Text.translatable("emi_loot.item_predicate.count", Integer.toString(finalMin), Integer.toString(finalMax));
        }

        MinMaxBounds.Ints durability = ((ItemDamagePredicate) predicate.subPredicates().get(ItemSubPredicates.DAMAGE)).durability();
        if (durability != MinMaxBounds.Ints.ANY){
            int finalMax = durability.max().orElse(0);
            int finalMin = durability.min().orElse(0);
            return Text.translatable("emi_loot.item_predicate.durability", Integer.toString(finalMin), Integer.toString(finalMax));
        }
        
        List<EnchantmentPredicate> enchants = ((ItemEnchantmentsPredicate) predicate.subPredicates().get(ItemSubPredicates.ENCHANTMENTS)).enchantments();
        List<EnchantmentPredicate> storedEnchants = ((ItemEnchantmentsPredicate) predicate.subPredicates().get(ItemSubPredicates.STORED_ENCHANTMENTS)).enchantments();
        if (enchants.size() + storedEnchants.size() > 0){
            List<EnchantmentPredicate> list = new LinkedList<>();
            list.addAll(enchants);
            list.addAll(storedEnchants);
            return EnchantmentPredicateParser.parseEnchantmentPredicates(list);
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty item predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }

}
