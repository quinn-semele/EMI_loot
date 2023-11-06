package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.fabric.mixins.LootTableAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

import java.nio.file.Path;
import java.util.Optional;

public class EMILootExpectPlatformImpl {
    public static LootPool[] getPools(LootTable table) {
        return ((LootTableAccessor) table).getPools();
    }

    public static String getModName(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(namespace);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }
        return namespace;
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .toAbsolutePath()
                .normalize();
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
