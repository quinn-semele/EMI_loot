package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(SetNameFunction.class)
public interface SetNameLootFunctionAccessor {

    @Accessor(value = "name")
    Optional<Component> getName();

}
