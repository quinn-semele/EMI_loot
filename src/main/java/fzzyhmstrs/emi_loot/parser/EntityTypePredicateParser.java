package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class EntityTypePredicateParser{

    public static Component parseEntityTypePredicate(EntityTypePredicate predicate){
        HolderSet<EntityType<?>> registryEntryList = predicate.types();
        Optional<TagKey<EntityType<?>>> tag = registryEntryList.unwrapKey();
        if (tag.isPresent()) {
            return Text.translatable("emi_loot.entity_predicate.type_tag", tag.get().location());
        } else if (registryEntryList.size() == 1) {
            return Text.translatable("emi_loot.entity_predicate.type_single", registryEntryList.get(0).value().getDescription().getString());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity type predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
}
