package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.SetContainerLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetContainerLootTable.class)
public interface SetLootTableLootFunctionAccessor {

    @Accessor(value = "name")
    ResourceKey<LootTable> getId();

}
