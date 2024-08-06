package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class SimpleFunctionParser implements FunctionParser{
    
    private final TextKey key;
  
    public SimpleFunctionParser(String key){
      this.key = TextKey.of(key);
    }
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        return new LootTableParser.LootFunctionResult(key,ItemStack.EMPTY,conditionTexts);
    }
}
