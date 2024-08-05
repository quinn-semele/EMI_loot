package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimitCount.class)
public interface LimitCountLootFunctionAccessor {

    @Accessor(value = "limiter")
    IntRange getLimit();

}