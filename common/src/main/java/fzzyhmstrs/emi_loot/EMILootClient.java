package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.client.ClientLootTables;

public class EMILootClient {

    public static String MOD_ID = "emi_loot";
    public static ClientLootTables tables;

    public static void init(ClientLootTables tables) {
        EMILootClient.tables = tables;
        tables.registerClient();
    }
}
