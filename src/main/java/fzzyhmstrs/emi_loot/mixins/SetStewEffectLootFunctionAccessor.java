package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SetStewEffectFunction.class)
public interface SetStewEffectLootFunctionAccessor {

    @Accessor(value = "effects")
    List<SetStewEffectFunction.EffectEntry> getEffects();

}
