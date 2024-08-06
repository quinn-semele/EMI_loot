package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RandomChanceWithLootingConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootItemCondition condition, ItemStack stack, boolean parentIsAlternative){
        float chance = ((LootItemRandomChanceWithEnchantedBonusCondition)condition).unenchantedChance();
        float multiplier = ((LootItemRandomChanceWithEnchantedBonusCondition)condition).enchantedChance().calculate(stack.getEnchantmentLevel(((LootItemRandomChanceWithEnchantedBonusCondition) condition).enchantment()));
        List<String> args = new LinkedList<>(Arrays.stream(new String[]{Float.toString((chance*100)), Float.toString((multiplier*100))}).toList());
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", args)));
    }
}
