package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ApplyBonusLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ApplyBonusFunctionParser implements FunctionParser{
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Holder<Enchantment> enchant = ((ApplyBonusLootFunctionAccessor)function).getEnchantment();
        String name = Enchantment.getFullname(enchant, 1).getString();
        String nTrim;
        if (enchant.value().getMaxLevel() != 1) {
            nTrim = name.substring(0, name.length() - 2);
        } else {
            nTrim = name;
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.bonus",nTrim), ItemStack.EMPTY, conditionTexts);
    }
}
