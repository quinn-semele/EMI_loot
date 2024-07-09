package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import lol.bai.badpackets.api.play.PlayPackets;
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

        PlayPackets.registerClientReceiver(LootTableParser.CLEAR_LOOTS, (minecraftClient, playNetworkHandler, buf, sender) ->
                loots.clear()
        );
        
        PlayPackets.registerClientReceiver(CHEST_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received chest " + table.getId());
        });
        
        PlayPackets.registerClientReceiver(BLOCK_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received block " + table.getId());
        });
        
        PlayPackets.registerClientReceiver(MOB_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received mob " + table.getId());
        });
        
        PlayPackets.registerClientReceiver(GAMEPLAY_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });

        PlayPackets.registerClientReceiver(ARCHAEOLOGY_SENDER, (minecraftClient, playNetworkHandler, buf, sender) -> {
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
        });
    }
    
    public void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        loots.clear();
    }

}
