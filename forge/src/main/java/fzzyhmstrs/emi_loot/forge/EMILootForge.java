package fzzyhmstrs.emi_loot.forge;

import dev.architectury.platform.forge.EventBuses;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(EMILoot.MOD_ID)
public class EMILootForge {
    public EMILootForge() {
        EventBuses.registerModEventBus(EMILoot.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.addListener(this::onRegister);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

        EMILoot.init();
    }

    public void onRegister(RegisterEvent event) {
        EMILoot.register();
    }

    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity player) {
            EMILoot.parser.registerServer(player);
        }
    }
}
