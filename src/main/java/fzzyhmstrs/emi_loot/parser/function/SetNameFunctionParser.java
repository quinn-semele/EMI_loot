package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetNameLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.text.Text;

import java.util.List;

public class SetNameFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Text text = ((SetNameLootFunctionAccessor)function).getName().orElseThrow(); // TODO?
        stack.set(DataComponentTypes.CUSTOM_NAME, text);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), stack, conditionTexts);
    }
}
