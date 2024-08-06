package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.EntityPredicateParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.Collections;
import java.util.List;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public class EntityPropertiesConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootContext.EntityTarget entity = ((LootItemEntityPropertyCondition)condition).entityTarget();
        EntityPredicate predicate = ((LootItemEntityPropertyCondition)condition).predicate().orElseThrow(); // TODO?
        MutableComponent propText;
        if (entity == LootContext.EntityTarget.THIS){
            propText = Text.translatable("emi_loot.entity_predicate.entity_this", EntityPredicateParser.parseEntityPredicate(predicate));
        } else {
            propText = Text.translatable("emi_loot.entity_predicate.entity_killer", EntityPredicateParser.parseEntityPredicate(predicate));
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.entity_props",propText.getString())));
    }
}
