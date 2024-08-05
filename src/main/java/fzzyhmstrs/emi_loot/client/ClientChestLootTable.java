package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClientChestLootTable implements LootReceiver {

    public static ClientChestLootTable INSTANCE = new ClientChestLootTable();
    public final ResourceLocation id;
    public final Object2FloatMap<ItemStack> items;

    public ClientChestLootTable(){
        this.id = Identifier.of("empty");
        this.items = new Object2FloatOpenHashMap<>();
    }

    public ClientChestLootTable(ResourceLocation id, Object2FloatMap<ItemStack> map){
        this.id = id;
        this.items = map;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public LootReceiver fromBuf(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = AbstractTextKeyParsingClientLootTable.getIdFromBuf(buf);
        int mapCount = buf.readShort();
        Object2FloatMap<ItemStack> itemMap = new Object2FloatOpenHashMap<>();
        for (int i = 0; i < mapCount; i++){
            ItemStack item = ItemStack.STREAM_CODEC.decode(buf);
            float itemWeight = buf.readFloat();
            if (item.is(Items.AIR)) continue;
            itemMap.put(item,itemWeight);
        }
        return new ClientChestLootTable(id,itemMap);
    }
}
