package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SimpleConditionParser implements ConditionParser{
    
    private final TextKey key;
    
    public SimpleConditionParser(String key){
        this.key = TextKey.of(key);
    }
    
    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        return Collections.singletonList(new LootTableParser.LootConditionResult(key));
    }
}
