package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.client.ClientLootTables;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EMILoot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EMILootClient {

    public static String MOD_ID = "emi_loot";
    public static ClientLootTables tables = new ClientLootTables();

    @SubscribeEvent
    public static void fmlClientSetup(FMLClientSetupEvent event) {
        tables.registerClient();
    }
}
