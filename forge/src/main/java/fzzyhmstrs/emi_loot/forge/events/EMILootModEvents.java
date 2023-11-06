package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;

public class EMILootModEvents {
    @SubscribeEvent
    public void onRegister(RegisterEvent event) {
        EMILoot.register();
    }
}
