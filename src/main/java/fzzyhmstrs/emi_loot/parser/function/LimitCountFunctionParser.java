package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.LimitCountLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class LimitCountFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        IntRange operator = ((LimitCountLootFunctionAccessor)function).getLimit();
        Component limit = NumberProcessors.processBoundedIntUnaryOperator(operator);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.limit_count", limit.getString()),ItemStack.EMPTY,conditionTexts);
    }
}
