package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

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
        
        NeoForge.EVENT_BUS.addListener(this::loggingOut);

        PlayPackets.registerClientReceiver(LootTableParser.CLEAR_LOOTS, (context, payload) -> {
            loots.clear();
        });

        PlayPackets.registerClientReceiver(CHEST_SENDER, (context, payload) -> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf((RegistryFriendlyByteBuf) payload);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received chest " + table.getId());
        });

        PlayPackets.registerClientReceiver(BLOCK_SENDER, (context, payload) -> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf((RegistryFriendlyByteBuf) payload);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received block " + table.getId());
        });

        PlayPackets.registerClientReceiver(MOB_SENDER, (context, payload) -> {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf((RegistryFriendlyByteBuf) payload);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received mob " + table.getId());
        });

        PlayPackets.registerClientReceiver(GAMEPLAY_SENDER, (context, payload) -> {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf((RegistryFriendlyByteBuf) payload);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });

        PlayPackets.registerClientReceiver(ARCHAEOLOGY_SENDER, (context, payload) -> {
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf((RegistryFriendlyByteBuf) payload);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
        });
    }
    
    public void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        loots.clear();
    }

}
