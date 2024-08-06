package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.SetInstrumentLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class SetInstrumentFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        TagKey<Instrument> tag = ((SetInstrumentLootFunctionAccessor)function).getInstrumentTag();
        InstrumentItem.setRandom(stack,tag, EMILoot.emiLootRandom);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), stack, conditionTexts);
    }
}
