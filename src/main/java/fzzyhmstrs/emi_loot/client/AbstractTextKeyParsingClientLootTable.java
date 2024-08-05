package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

abstract public class AbstractTextKeyParsingClientLootTable<T extends LootReceiver> implements LootReceiver{

    public AbstractTextKeyParsingClientLootTable(){
        this.rawItems = new HashMap<>();
    }

    public AbstractTextKeyParsingClientLootTable(Map<List<TextKey>, ClientRawPool> map){
        this.rawItems = map;
    }

    private final Map<List<TextKey>, ClientRawPool> rawItems;
    public List<ClientBuiltPool> builtItems;

    static ResourceLocation getIdFromBuf(FriendlyByteBuf buf){
        String idToParse = buf.readUtf();
        if (idToParse.contains(":")){
            return Identifier.of(idToParse);
        } else if (idToParse.startsWith("b/")){
            return Identifier.ofVanilla("blocks/" + idToParse.substring(2));
        } else if (idToParse.startsWith("e/")){
            return Identifier.ofVanilla("entities/" + idToParse.substring(2));
        } else if (idToParse.startsWith("c/")){
            return Identifier.ofVanilla("chests/" + idToParse.substring(2));
        } else if (idToParse.startsWith("g/")){
            return Identifier.ofVanilla("gameplay/" + idToParse.substring(2));
        } else if (idToParse.startsWith("a/")) {
            return Identifier.ofVanilla("archaeology/" + idToParse.substring(2));
        } else {
            return Identifier.ofVanilla(idToParse);
        }
    }

    abstract List<Tuple<Integer, Component>> getSpecialTextKeyList(Level world, Block block);

    public void build(Level world, Block block){
        Map<List<Tuple<Integer,Component>>, Object2FloatMap<ItemStack>> builderItems = new HashMap<>();
        rawItems.forEach((list,pool)->{
            List<Tuple<Integer,Component>> applyToAllList = new LinkedList<>(getSpecialTextKeyList(world, block));
            list.forEach((textKey) -> {
                Component text = textKey.process(ItemStack.EMPTY,world).text();
                applyToAllList.add(new Tuple<>(textKey.index(),text));
            });
            pool.map().forEach((poolList,poolItemMap)->{
                List<Tuple<Integer,Component>> newPoolList = new LinkedList<>();
                Object2FloatMap<ItemStack> itemsToAdd = new Object2FloatOpenHashMap<>();
                List<ItemStack> itemsToRemove = new LinkedList<>();

                poolList.forEach((textKey) -> {
                    poolItemMap.forEach((poolStack,weight)->{
                        List<ItemStack> stacks = textKey.process(poolStack,world).stacks();
                        AtomicReference<Float> toAddWeight = new AtomicReference<>(1.0f);
                        if (!stacks.contains(poolStack)){
                            itemsToRemove.add(poolStack);
                            toAddWeight.set(poolItemMap.getFloat(poolStack));
                        }

                        stacks.forEach(stack->{
                            if(poolItemMap.containsKey(stack)){
                                toAddWeight.set(poolItemMap.getFloat(stack));
                            }
                        });
                        stacks.forEach(stack->{
                            if(!poolItemMap.containsKey(stack)){
                                itemsToAdd.put(stack,(float)toAddWeight.get());
                            }
                        });

                    });
                    Component text = textKey.process(ItemStack.EMPTY,world).text();
                    newPoolList.add(new Tuple<>(textKey.index(),text));

                });
                List<Tuple<Integer, Component>> summedList = new LinkedList<>(applyToAllList);
                summedList.addAll(newPoolList);
                if (summedList.isEmpty()){
                    summedList.add(new Tuple<>(TextKey.getIndex("emi_loot.no_conditions"), Text.translatable("emi_loot.no_conditions")));
                }
                Object2FloatMap<ItemStack> builderPoolMap = builderItems.getOrDefault(summedList, poolItemMap);
                builderPoolMap.putAll(itemsToAdd);
                itemsToRemove.forEach(builderPoolMap::removeFloat);
                builderItems.put(summedList,builderPoolMap);
            });

        });
        List<ClientBuiltPool> finalList = new LinkedList<>();
        builderItems.forEach((builtList,builtMap)->{
            Float2ObjectMap<List<ItemStack>> consolidatedMap = new Float2ObjectArrayMap<>();
            builtMap.forEach((stack,weight)->{
                List<ItemStack> consolidatedList = consolidatedMap.getOrDefault((float)weight,new LinkedList<>());
                if (!consolidatedList.contains(stack)){
                    consolidatedList.add(stack);
                }
                consolidatedMap.put((float)weight,consolidatedList);
            });
            Float2ObjectMap<EmiIngredient> emiConsolidatedMap = new Float2ObjectArrayMap<>();
            consolidatedMap.forEach((consolidatedWeight,consolidatedList)-> {
                List<EmiStack> emiStacks = new LinkedList<>();
                for (ItemStack i : consolidatedList){
                    emiStacks.add(EmiStack.of(i));
                }
                emiConsolidatedMap.put((float) consolidatedWeight, EmiIngredient.of(emiStacks));
            });
            finalList.add(new ClientBuiltPool(builtList,emiConsolidatedMap));
        });
        builtItems = finalList;
    }

    abstract Tuple<ResourceLocation,ResourceLocation> getBufId(RegistryFriendlyByteBuf buf);

    abstract T simpleTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids,RegistryFriendlyByteBuf buf);

    abstract T emptyTableToReturn();

    abstract T filledTableToReturn(Tuple<ResourceLocation,ResourceLocation> ids, Map<List<TextKey>, ClientRawPool> itemMap);

    @Override
    public LootReceiver fromBuf(RegistryFriendlyByteBuf buf) {
        boolean isEmpty = true;

        Tuple<ResourceLocation,ResourceLocation> ids = getBufId(buf);
        ResourceLocation id = ids.getA();
        if (EMILoot.DEBUG) EMILoot.LOGGER.info("parsing table " + id);
        int builderCount = buf.readShort();
        if (builderCount == -1){
            return simpleTableToReturn(ids,buf);
        }

        Map<List<TextKey>, ClientRawPool> itemMap = new HashMap<>();
        //shortcut -1 means a simple table. One guaranteed drop of quantity 1 with no conditions.

        for (int b = 0; b < builderCount; b++) {

            List<TextKey> qualifierList = new LinkedList<>();

            int conditionSize = buf.readShort();
            for (int i = 0; i < conditionSize; i++) {
                try {
                    TextKey key = TextKey.fromBuf(buf);
                    qualifierList.add(key);
                } catch (DecoderException e){
                    EMILoot.LOGGER.error("Client table " + id + " had a TextKey decoding error while reading a loot condition!");
                }
            }

            int functionSize = buf.readShort();
            for (int i = 0; i < functionSize; i++) {
                try{
                    TextKey key = TextKey.fromBuf(buf);
                    qualifierList.add(key);
                } catch (DecoderException e){
                    EMILoot.LOGGER.error("Client table " + id + " had a TextKey decoding error while reading a loot function!");
                }
            }

            ClientRawPool pool = itemMap.getOrDefault(qualifierList,new ClientRawPool(new HashMap<>()));

            int pileSize = buf.readShort();
            for (int i = 0; i < pileSize; i++) {

                List<TextKey> pileQualifierList = new LinkedList<>();

                int pileQualifierSize = buf.readShort();
                for (int j = 0; j < pileQualifierSize; j++) {
                    try{
                        TextKey key = TextKey.fromBuf(buf);
                        pileQualifierList.add(key);
                    } catch (DecoderException e){
                        EMILoot.LOGGER.error("Client table " + id + " had a TextKey decoding error while reading an item pile qualifier!");
                    }
                }

                Object2FloatMap<ItemStack> pileItemMap = pool.map().getOrDefault(pileQualifierList,new Object2FloatOpenHashMap<>());

                int pileItemSize = buf.readShort();
                for (int j = 0; j < pileItemSize; j++) {
                    ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
                    float weight = buf.readFloat();
                    pileItemMap.put(stack,weight);
                    isEmpty = false;
                }
                pool.map().put(pileQualifierList,pileItemMap);
            }

            itemMap.put(qualifierList,pool);
        }
        if (isEmpty) return emptyTableToReturn();

        return filledTableToReturn(ids, itemMap);
    }

}
