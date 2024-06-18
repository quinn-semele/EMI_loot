package fzzyhmstrs.emi_loot.forge;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.forge.events.EMILootClientForgeEvents;
import fzzyhmstrs.emi_loot.forge.events.EMILootClientModEvents;
import fzzyhmstrs.emi_loot.forge.events.EMILootForgeEvents;
import fzzyhmstrs.emi_loot.forge.events.EMILootModEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(EMILoot.MOD_ID)
public class EMILootForge {
    public EMILootForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        //EventBuses.registerModEventBus(EMILoot.MOD_ID, MOD_BUS);

        MinecraftForge.EVENT_BUS.register(new EMILootForgeEvents());
        MOD_BUS.register(new EMILootModEvents());

        if (FMLLoader.getDist().isClient()) {
            MinecraftForge.EVENT_BUS.register(new EMILootClientForgeEvents());
            MOD_BUS.register(new EMILootClientModEvents());
        }

        EMILoot.init();
    }
}
