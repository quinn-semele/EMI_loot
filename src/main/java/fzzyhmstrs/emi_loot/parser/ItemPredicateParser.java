package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.item.Item;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.*;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ItemPredicateParser {

    public static Text parseItemPredicate(ItemPredicate predicate){
        Optional<TagKey<Item>> tag = predicate.items().flatMap(RegistryEntryList::getTagKey);
        if (tag.isPresent()){
            return Text.translatable("emi_loot.item_predicate.tag",tag.get().id());
        }
        
        Optional<RegistryEntryList<Item>> items = predicate.items();
        if (items.isPresent() && items.get().size() > 0){
            List<MutableText> list = items.get().stream().map((item) -> (MutableText)item.value().getName()).toList();
            return Text.translatable("emi_loot.item_predicate.items", ListProcessors.buildOrList(list));
        }
        
        NumberRange.IntRange count = predicate.count();
        if (count != NumberRange.IntRange.ANY){
            int finalMax = count.max().orElse(0);
            int finalMin = count.min().orElse(0);
            return Text.translatable("emi_loot.item_predicate.count", Integer.toString(finalMin), Integer.toString(finalMax));
        }
        
        NumberRange.IntRange durability = ((DamagePredicate) predicate.subPredicates().get(ItemSubPredicateTypes.DAMAGE)).durability();
        if (durability != NumberRange.IntRange.ANY){
            int finalMax = durability.max().orElse(0);
            int finalMin = durability.min().orElse(0);
            return Text.translatable("emi_loot.item_predicate.durability", Integer.toString(finalMin), Integer.toString(finalMax));
        }
        
        List<EnchantmentPredicate> enchants = ((EnchantmentsPredicate) predicate.subPredicates().get(ItemSubPredicateTypes.ENCHANTMENTS)).getEnchantments();
        List<EnchantmentPredicate> storedEnchants = ((EnchantmentsPredicate) predicate.subPredicates().get(ItemSubPredicateTypes.STORED_ENCHANTMENTS)).getEnchantments();
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
