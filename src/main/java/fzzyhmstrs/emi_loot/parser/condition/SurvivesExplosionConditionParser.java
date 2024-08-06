package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SurvivesExplosionConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        if (parentIsAlternative) return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.survives_explosion")));
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.empty()));
    }
}
