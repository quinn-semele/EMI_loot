package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class EMILootExpectPlatform {
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

    @ExpectPlatform
    public static LootTable loadLootTable(Gson gson, Identifier id, JsonElement json) {
        throw new AssertionError();
    }
}
