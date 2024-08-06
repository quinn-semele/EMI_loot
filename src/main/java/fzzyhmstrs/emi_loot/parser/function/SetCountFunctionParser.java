package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetCountLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetCountFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        NumberProvider provider = ((SetCountLootFunctionAccessor)function).getCountRange();
        float rollAvg = NumberProcessors.getRollAvg(provider);
        boolean add = ((SetCountLootFunctionAccessor)function).getAdd();
        if (add){
            stack.setCount(Math.max(stack.getCount() + (int)rollAvg,1));
        } else {
            stack.setCount(Math.max((int)rollAvg,1));
        }
        if (add){
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_count_add"),ItemStack.EMPTY,conditionTexts);
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_count_set"),ItemStack.EMPTY,conditionTexts);
    }
}
