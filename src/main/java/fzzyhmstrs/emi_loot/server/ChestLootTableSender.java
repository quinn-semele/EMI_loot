package fzzyhmstrs.emi_loot.server;

import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ChestLootTableSender implements LootSender<ChestLootPoolBuilder> {

    public ChestLootTableSender(Identifier id){
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    HashMap<ItemStack, Float> floatMap = new HashMap<>();
    public static Identifier CHEST_SENDER = Identifier.of("e_l","c_s");

    @Override
    public void build(){
        builderList.forEach((builder) -> {
            builder.build();
            builder.builtMap.forEach((item,weight)->{
                if (floatMap.containsKey(item)) {
                    float oldWeight = floatMap.getOrDefault(item, 0f);
                    floatMap.put(item, oldWeight + weight);
                } else {
                    floatMap.put(item, weight);
                }
            });
        });
    }

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (!PacketSender.s2c(player).canSend(CHEST_SENDER)) return;
        RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), player.server.getRegistryManager(), ConnectionType.NEOFORGE);
        buf.writeString(idToSend);
        buf.writeShort(floatMap.size());
        floatMap.forEach((item, floatWeight) -> {
            ItemStack.PACKET_CODEC.encode(buf, item);
            buf.writeFloat(floatWeight);
        });
        PacketSender.s2c(player).send(CHEST_SENDER, buf);

    }

    @Override
    public void addBuilder(ChestLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<ChestLootPoolBuilder> getBuilders() {
        return builderList;
    }
}