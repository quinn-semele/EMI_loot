package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTableReference.class)
public interface LootTableEntryAccessor {

    @Accessor(value = "id")
    ResourceLocation getId();

}
