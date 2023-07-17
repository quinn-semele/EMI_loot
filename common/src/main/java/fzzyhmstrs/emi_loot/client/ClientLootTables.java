package fzzyhmstrs.emi_loot.client;

import dev.architectury.networking.NetworkManager;
import fzzyhmstrs.emi_loot.EMILoot;

import java.util.LinkedList;
import java.util.List;

import static fzzyhmstrs.emi_loot.server.BlockLootTableSender.BLOCK_SENDER;
import static fzzyhmstrs.emi_loot.server.ChestLootTableSender.CHEST_SENDER;
import static fzzyhmstrs.emi_loot.server.GameplayLootTableSender.GAMEPLAY_SENDER;
import static fzzyhmstrs.emi_loot.server.MobLootTableSender.MOB_SENDER;

@SuppressWarnings("UnstableApiUsage")
public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHEST_SENDER, null, (buf, context) -> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received chest " + table.getId());
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, BLOCK_SENDER, null, (buf, context) -> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received block " + table.getId());
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, MOB_SENDER, null, (buf, context) -> {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received mob " + table.getId());
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GAMEPLAY_SENDER, null, (buf, context) -> {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });
    }

}
