package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

@SuppressWarnings("deprecation")
public class ClientBlockLootTable extends AbstractTextKeyParsingClientLootTable<ClientBlockLootTable> {

    public static ClientBlockLootTable INSTANCE = new ClientBlockLootTable();
    private static final ResourceLocation EMPTY = Identifier.of("blocks/empty");
    public final ResourceLocation id;
    public final ResourceLocation blockId;

    public ClientBlockLootTable(){
        super();
        this.id = EMPTY;
        this.blockId = Identifier.of("air");
    }

    public ClientBlockLootTable(ResourceLocation id, Map<List<TextKey>, ClientRawPool> map){
        super(map);
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        int lastSlashIndex = pth.lastIndexOf('/');
        if (lastSlashIndex == -1){
            blockId = Identifier.of(ns,pth);
        } else {
            blockId = Identifier.of(ns,pth.substring(Math.min(lastSlashIndex + 1,pth.length())));
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
        String tool = "";
        if (block.builtInRegistryHolder().is(BlockTags.MINEABLE_WITH_PICKAXE)){
            tool = "pickaxe";
        } else if (block.builtInRegistryHolder().is(BlockTags.MINEABLE_WITH_AXE)){
            tool = "axe";
        } else if (block.builtInRegistryHolder().is(BlockTags.MINEABLE_WITH_SHOVEL)){
            tool = "shovel";
        } else if (block.builtInRegistryHolder().is(BlockTags.MINEABLE_WITH_HOE)){
            tool = "hoe";
        }
        List<Tuple<Integer,Component>> toolNeededList = new LinkedList<>();
        if (!Objects.equals(tool,"")){
            String type;
            if (block.builtInRegistryHolder().is(BlockTags.NEEDS_STONE_TOOL)){
                type = "stone";
            } else if (block.builtInRegistryHolder().is(BlockTags.NEEDS_IRON_TOOL)){
                type = "iron";
            } else if (block.builtInRegistryHolder().is(BlockTags.NEEDS_DIAMOND_TOOL)){
                type = "diamond";
            } else{
                type = "wood";
            }
            String keyString = "emi_loot." + tool + "." + type;
            int keyIndex = TextKey.getIndex(keyString);
            if (keyIndex != -1){
                toolNeededList.add(new Tuple<>(keyIndex, Text.translatable(keyString)));
            }
        }
        return toolNeededList;
    }

    @Override
    Tuple<ResourceLocation,ResourceLocation> getBufId(RegistryFriendlyByteBuf buf) {
        return new Tuple<>(getIdFromBuf(buf),EMPTY);
    }

    @Override
    ClientBlockLootTable simpleTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids,RegistryFriendlyByteBuf buf) {
        ClientRawPool simplePool = new ClientRawPool(new HashMap<>());
        Object2FloatMap<ItemStack> simpleMap = new Object2FloatOpenHashMap<>();
        ItemStack simpleStack = new ItemStack(buf.readById(BuiltInRegistries.ITEM::byId));
        simpleMap.put(simpleStack,100F);
        simplePool.map().put(new ArrayList<>(),simpleMap);
        Map<List<TextKey>, ClientRawPool> itemMap = new HashMap<>();
        itemMap.put(new ArrayList<>(),simplePool);
        return new ClientBlockLootTable(ids.getA(),itemMap);
    }

    @Override
    ClientBlockLootTable emptyTableToReturn() {
        return new ClientBlockLootTable();
    }

    @Override
    ClientBlockLootTable filledTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids, Map<List<TextKey>, ClientRawPool> itemMap) {
        return new ClientBlockLootTable(ids.getA(),itemMap);
    }
}
