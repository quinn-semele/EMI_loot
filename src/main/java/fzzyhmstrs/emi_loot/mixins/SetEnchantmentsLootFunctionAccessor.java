package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SetEnchantmentsFunction.class)
public interface SetEnchantmentsLootFunctionAccessor {

    @Accessor(value = "enchantments")
    Map<Holder<Enchantment>, NumberProvider> getEnchantments();

    @Accessor(value = "add")
    boolean getAdd();

}
