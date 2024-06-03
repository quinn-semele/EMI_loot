package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EMILootFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EMILoot.init();
        EMILoot.register();

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->{
            EMILoot.parser.registerServer(player);
        });
    }
}
