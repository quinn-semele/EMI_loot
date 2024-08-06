package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ExplosionDecayFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        if (parentIsAlternative) return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.decay"), ItemStack.EMPTY, conditionTexts);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, conditionTexts);
    }
}
