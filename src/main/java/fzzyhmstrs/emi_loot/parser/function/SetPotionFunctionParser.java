package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetPotionLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class SetPotionFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        RegistryEntry<Potion> potion = ((SetPotionLootFunctionAccessor)function).getPotion();
        stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
        Text potionName = Text.translatable(Potion.finishTranslationKey(Optional.of(potion), Items.POTION.getTranslationKey() + ".effect."));
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.potion",potionName.getString()), ItemStack.EMPTY,conditionTexts);
    }
}
