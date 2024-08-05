package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.TextKey;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class ClientMobLootTable extends AbstractTextKeyParsingClientLootTable<ClientMobLootTable> {

    public static ClientMobLootTable INSTANCE = new ClientMobLootTable();
    private static final ResourceLocation EMPTY = Identifier.of("entity/empty");
    public final ResourceLocation id;
    public final ResourceLocation mobId;
    public String color = "";

    public ClientMobLootTable(){
        super();
        this.id = EMPTY;
        this.mobId = Identifier.of("empty");
    }

    public ClientMobLootTable(ResourceLocation id,ResourceLocation mobId, Map<List<TextKey>, ClientRawPool> map){
        super(map);
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(mobId)) {
            this.mobId = Identifier.of("empty");
        } else {
            if (Objects.equals(mobId, BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SHEEP))) {
                int lastSlashIndex = pth.lastIndexOf('/');
                if (lastSlashIndex != -1) {
                    this.color = pth.substring(Math.min(lastSlashIndex + 1, pth.length()));
                }
            }
            this.mobId = mobId;
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isEmpty(){
        return Objects.equals(id, EMPTY);
    }

    @Override
    List<Tuple<Integer, Component>> getSpecialTextKeyList(Level world, Block block) {
        return List.of();
    }

    @Override
    Tuple<ResourceLocation,ResourceLocation> getBufId(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = getIdFromBuf(buf);
        ResourceLocation mobId = getIdFromBuf(buf);
        return new Tuple<>(id,mobId);
    }

    @Override
    ClientMobLootTable simpleTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids,RegistryFriendlyByteBuf buf) {
        ClientRawPool simplePool = new ClientRawPool(new HashMap<>());
        Object2FloatMap<ItemStack> simpleMap = new Object2FloatOpenHashMap<>();
        ItemStack simpleStack = new ItemStack(buf.readById(BuiltInRegistries.ITEM::byId));
        simpleMap.put(simpleStack,100F);
        simplePool.map().put(new ArrayList<>(),simpleMap);
        Map<List<TextKey>, ClientRawPool> itemMap = new HashMap<>();
        itemMap.put(new ArrayList<>(),simplePool);
        return new ClientMobLootTable(ids.getA(),ids.getB(),itemMap);
    }

    @Override
    ClientMobLootTable emptyTableToReturn() {
        return new ClientMobLootTable();
    }

    @Override
    ClientMobLootTable filledTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids, Map<List<TextKey>, ClientRawPool> itemMap) {
        return new ClientMobLootTable(ids.getA(),ids.getB(),itemMap);
    }
}
