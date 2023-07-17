package fzzyhmstrs.emi_loot.forge;

import fzzyhmstrs.emi_loot.forge.mixins.LootTableAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

public class EMILootExpectPlatformImpl {
    public static LootPool[] getPools(LootTable table) {
        return ((LootTableAccessor) table).getPools().toArray(LootPool[]::new);
    }
}
