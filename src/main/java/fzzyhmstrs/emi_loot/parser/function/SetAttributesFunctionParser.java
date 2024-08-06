package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.LinkedList;
import java.util.List;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        List<SetAttributesFunction.Modifier> attributes = ((SetAttributesFunction)function).modifiers;
        List<MutableComponent> list = new LinkedList<>();
        for (SetAttributesFunction.Modifier attribute : attributes) {
            String name = attribute.id().toString();
            NumberProvider amount = attribute.amount();
            AttributeModifier.Operation operation = attribute.operation();
            if (operation == AttributeModifier.Operation.ADD_VALUE){
                list.add(Text.translatable("emi_loot.function.set_attributes.add",NumberProcessors.getRollAvg(amount),name));
            } else if (operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE){
                list.add(Text.translatable("emi_loot.function.set_attributes.multiply_base",NumberProcessors.getRollAvg(amount) + 1,name));
            } else {
                list.add(Text.translatable("emi_loot.function.set_attributes.multiply",NumberProcessors.getRollAvg(amount) + 1,name));
            }
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_attributes", ListProcessors.buildAndList(list).getString()), ItemStack.EMPTY, conditionTexts);
    }
}
