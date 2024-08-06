package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.predicates.ConditionReference;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ReferenceConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        ResourceLocation id = ((ConditionReference)condition).name().location();
        if (LootTableParser.lootManager != null){
            LootItemCondition referenceCondition = LootTableParser.lootManager.getElement(new LootDataId<>(LootDataType.PREDICATE, id));
            if (referenceCondition != null && referenceCondition.getType() != LootItemConditions.REFERENCE){
                return LootTableParser.parseLootCondition(referenceCondition,stack,parentIsAlternative);
            }
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.reference",id.toString())));
    }
}
