package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetStewEffectLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.LinkedList;
import java.util.List;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;

public class SetStewFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        List<SetStewEffectFunction.EffectEntry> effects = ((SetStewEffectLootFunctionAccessor)function).getEffects();
        List<MutableComponent> list = new LinkedList<>();
        for (SetStewEffectFunction.EffectEntry effect : effects){
            list.add(effect.effect().value().getDisplayName().copy());
        }
        if (list.isEmpty()){
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", Text.translatable("emi_loot.function.set_stew_unknown").getString()), ItemStack.EMPTY, conditionTexts);
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", ListProcessors.buildOrList(list).getString()), ItemStack.EMPTY, conditionTexts);
    }
}
