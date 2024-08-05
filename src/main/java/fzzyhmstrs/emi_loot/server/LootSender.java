package fzzyhmstrs.emi_loot.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface LootSender<T extends LootBuilder> {
    String getId();
    void send(ServerPlayer player);
    void addBuilder(T builder);
    List<T> getBuilders();
    void build();

    static String getIdToSend(ResourceLocation id){
        if (id.getNamespace().equals("minecraft")){
            String path = id.getPath();
            if (path.contains("blocks/")){
                return "b/" + path.substring(7);
            } else if (path.contains("entities/")){
                return "e/"+ path.substring(9);
            } else if (path.contains("chests/")){
                return "c/" + path.substring(7);
            } else if (path.contains("gameplay/")){
                return "g/" + path.substring(9);
            } else if (path.contains("archaeology/")) {
                return "a/" + path.substring(12);
            } else {
                return path;
            }
        }
        return id.toString();
    }
}
