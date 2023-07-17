package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EMILootFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EMILoot.init();
        EMILoot.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
            EMILoot.parser.registerServer(handler.player);
        });
    }
}
