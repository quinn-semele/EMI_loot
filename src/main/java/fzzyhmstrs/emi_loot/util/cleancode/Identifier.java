package fzzyhmstrs.emi_loot.util.cleancode;

import net.minecraft.resources.ResourceLocation;

public class Identifier {
    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation of(String namespaceAndPath) {
        return ResourceLocation.parse(namespaceAndPath);
    }

    public static ResourceLocation ofVanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
