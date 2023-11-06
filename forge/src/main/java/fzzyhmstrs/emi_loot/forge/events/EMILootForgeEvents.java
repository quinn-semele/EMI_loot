package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;

public class EMILootForgeEvents {
    @SubscribeEvent
    public void onRegister(RegisterEvent event) {
        EMILoot.register();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity player) {
            EMILoot.parser.registerServer(player);
        }
    }
}
