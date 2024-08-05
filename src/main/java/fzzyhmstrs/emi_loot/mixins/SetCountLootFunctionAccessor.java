package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetItemCountFunction.class)
public interface SetCountLootFunctionAccessor {

    @Accessor(value = "value")
    NumberProvider getCountRange();

    @Accessor(value = "add")
    boolean getAdd();

}
