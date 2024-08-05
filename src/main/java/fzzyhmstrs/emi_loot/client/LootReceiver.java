package fzzyhmstrs.emi_loot.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface LootReceiver {
    boolean isEmpty();
    ResourceLocation getId();
    LootReceiver fromBuf(RegistryFriendlyByteBuf buf);
}
