package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ApplyBonusCount.class)
public interface ApplyBonusLootFunctionAccessor {

    @Accessor(value = "enchantment")
    Holder<Enchantment> getEnchantment();
}
