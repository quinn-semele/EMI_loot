package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class EMILootClientFabric implements ClientModInitializer {
    public static ClientLootTables tables = new ClientLootTables();

    @Override
    public void onInitializeClient() {
        EMILootClient.init(tables);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> tables.getLoots().clear());
    }
}
