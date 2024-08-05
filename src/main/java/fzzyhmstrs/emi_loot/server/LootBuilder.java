package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.Collection;
import java.util.List;

public interface LootBuilder{
    void addEntryForPostProcessing(LootTableParser.PostProcessor process, LootPoolEntryContainer entry);
    Collection<LootPoolEntryContainer> getEntriesToPostProcess(LootTableParser.PostProcessor process);
    void addItem(LootTableParser.ItemEntryResult result);
    void build();
    List<LootTableParser.ItemEntryResult> revert();
}
