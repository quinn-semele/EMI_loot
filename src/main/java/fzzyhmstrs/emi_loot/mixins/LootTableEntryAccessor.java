package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NestedLootTable.class)
public interface LootTableEntryAccessor {

    @Accessor(value = "id")
    ResourceLocation getId();

}
