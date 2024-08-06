package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetEnchantmentsLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.List;
import java.util.Map;

public class SetEnchantmentsFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts){
        Map<Holder<Enchantment>, NumberProvider> enchantments = ((SetEnchantmentsLootFunctionAccessor)function).getEnchantments();
        boolean add = ((SetEnchantmentsLootFunctionAccessor)function).getAdd();
        if (stack.is(Items.BOOK)){
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemStack finalStack = stack;
            enchantments.forEach((enchantment, provider)->{
                float rollAvg = NumberProcessors.getRollAvg(provider);
                finalStack.enchant(enchantment, (int) rollAvg);
            });
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_book"), finalStack,conditionTexts);
        } else {
            ItemEnchantments.Mutable finalStackMap = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            if (add){
                // todo: needs holder lookup stack.getAllEnchantments
                ItemEnchantments stackMap = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                enchantments.forEach((enchantment, provider)->{
                    float rollAvg = NumberProcessors.getRollAvg(provider);
                    finalStackMap.set(enchantment, Math.max((int) rollAvg, stackMap.getLevel(enchantment)));
                });
            } else {
                enchantments.forEach((enchantment, provider)->{
                    float rollAvg = NumberProcessors.getRollAvg(provider);
                    finalStackMap.set(enchantment, Math.max((int) rollAvg, 1));
                });
            }
            stack.set(DataComponents.ENCHANTMENTS, finalStackMap.toImmutable());
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_item"), stack, conditionTexts);
        }
    }
}
