package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootItemConditionalFunction.class)
public interface ConditionalLootFunctionAccessor {

    @Accessor(value = "predicates")
    List<LootItemCondition> getConditions();

}
