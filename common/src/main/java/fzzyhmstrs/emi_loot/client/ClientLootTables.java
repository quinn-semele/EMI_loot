package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import lol.bai.badpackets.api.S2CPacketReceiver;

import java.util.LinkedList;
import java.util.List;

import static fzzyhmstrs.emi_loot.server.ArchaeologyLootTableSender.ARCHAEOLOGY_SENDER;
import static fzzyhmstrs.emi_loot.server.BlockLootTableSender.BLOCK_SENDER;
import static fzzyhmstrs.emi_loot.server.ChestLootTableSender.CHEST_SENDER;
import static fzzyhmstrs.emi_loot.server.GameplayLootTableSender.GAMEPLAY_SENDER;
import static fzzyhmstrs.emi_loot.server.MobLootTableSender.MOB_SENDER;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        S2CPacketReceiver.register(LootTableParser.CLEAR_LOOTS, (client, handler, buf, responseSender) -> loots.clear());

        S2CPacketReceiver.register(CHEST_SENDER, (client, handler, buf, responseSender) -> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received chest " + table.getId());
        });

        S2CPacketReceiver.register(BLOCK_SENDER, (client, handler, buf, responseSender) -> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received block " + table.getId());
        });

        S2CPacketReceiver.register(MOB_SENDER, (client, handler, buf, responseSender) -> {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received mob " + table.getId());
        });

        S2CPacketReceiver.register(GAMEPLAY_SENDER, (client, handler, buf, responseSender) -> {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });

        S2CPacketReceiver.register(ARCHAEOLOGY_SENDER, (client, handler, buf, responseSender) -> {
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
        });
    }

}
