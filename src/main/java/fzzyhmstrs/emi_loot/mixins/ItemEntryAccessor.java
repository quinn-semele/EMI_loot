package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootItem.class)
public interface ItemEntryAccessor {

    @Accessor(value = "item")
    Holder<Item> getItem();

}
