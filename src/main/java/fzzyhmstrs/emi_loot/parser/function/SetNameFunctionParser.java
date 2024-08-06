package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetNameLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class SetNameFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Component text = ((SetNameLootFunctionAccessor)function).getName().orElseThrow(); // TODO?
        stack.set(DataComponents.CUSTOM_NAME, text);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), stack, conditionTexts);
    }
}
