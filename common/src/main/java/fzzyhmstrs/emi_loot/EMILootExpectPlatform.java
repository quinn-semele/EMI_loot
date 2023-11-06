package fzzyhmstrs.emi_loot;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

import java.nio.file.Path;

public class EMILootExpectPlatform {
    @ExpectPlatform
    public static LootPool[] getPools(LootTable table) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getModName(String namespace) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        throw new AssertionError();
    }
}
