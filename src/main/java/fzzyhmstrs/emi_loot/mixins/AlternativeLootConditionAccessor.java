package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeLootItemCondition.class)
public interface AlternativeLootConditionAccessor {

    @Accessor(value = "terms")
    List<LootItemCondition> getConditions();

}
