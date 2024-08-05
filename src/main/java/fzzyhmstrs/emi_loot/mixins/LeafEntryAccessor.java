package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPoolSingletonContainer.class)
public interface LeafEntryAccessor {

    @Accessor(value = "weight")
    int getWeight();

    @Accessor(value = "functions")
    List<LootItemFunction> getFunctions();
}
