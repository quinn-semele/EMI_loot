package fzzyhmstrs.emi_loot.forge;

import fzzyhmstrs.emi_loot.forge.mixins.LootTableAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Optional;

public class EMILootExpectPlatformImpl {
    public static LootPool[] getPools(LootTable table) {
        return ((LootTableAccessor) table).getPools().toArray(LootPool[]::new);
    }

    public static String getModName(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<? extends ModContainer> container = ModList.get().getModContainerById(namespace);
        if (container.isPresent()) {
            return container.get().getModInfo().getDisplayName();
        }
        return namespace;
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }
}
