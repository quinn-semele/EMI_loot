package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CopyNameFunction.class)
public interface CopyNameLootFunctionAccessor {

    @Accessor(value = "source")
    CopyNameFunction.NameSource getSource();

}
