package fzzyhmstrs.emi_loot.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.Collection;

abstract public class AbstractLootPoolBuilder implements LootBuilder {

    public AbstractLootPoolBuilder(float rollWeight){
        this.rollWeight = rollWeight;
    }

    final float rollWeight;
    boolean isSimple = false;
    boolean isEmpty = false;
    ItemStack simpleStack = ItemStack.EMPTY;
    private final Multimap<LootTableParser.PostProcessor, LootPoolEntryContainer> map = ArrayListMultimap.create();

    @Override
    public void addEntryForPostProcessing(LootTableParser.PostProcessor process, LootPoolEntryContainer entry) {
        map.put(process,entry);
    }

    @Override
    public Collection<LootPoolEntryContainer> getEntriesToPostProcess(LootTableParser.PostProcessor process) {
        return map.get(process);
    }
}
