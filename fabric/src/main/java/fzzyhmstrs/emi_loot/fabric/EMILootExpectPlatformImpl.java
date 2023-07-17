package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.fabric.mixins.LootTableAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

public class EMILootExpectPlatformImpl {
    public static LootPool[] getPools(LootTable table) {
        return ((LootTableAccessor) table).getPools();
    }
}
