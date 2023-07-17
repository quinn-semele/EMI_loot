package fzzyhmstrs.emi_loot;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

public class EMILootExpectPlatform {
    @ExpectPlatform
    public static LootPool[] getPools(LootTable table) {
        throw new AssertionError();
    }
}
