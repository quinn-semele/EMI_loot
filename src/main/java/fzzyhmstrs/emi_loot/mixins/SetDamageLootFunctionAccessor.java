package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetItemDamageFunction.class)
public interface SetDamageLootFunctionAccessor {

    @Accessor(value = "damage")
    NumberProvider getDurabilityRange();

    @Accessor(value = "add")
    boolean getAdd();

}
