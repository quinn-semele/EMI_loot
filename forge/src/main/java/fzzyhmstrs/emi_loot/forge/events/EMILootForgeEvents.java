package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;

public class EMILootForgeEvents {
    @SubscribeEvent
    public void onRegister(RegisterEvent event) {
        EMILoot.register();
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        EMILoot.parser.registerServer(event.getPlayer());
    }
}
