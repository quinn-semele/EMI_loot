package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetDamageLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetDamageFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        NumberProvider provider = ((SetDamageLootFunctionAccessor)function).getDurabilityRange();
        float rollAvg = NumberProcessors.getRollAvg(provider);
        boolean add = ((SetDamageLootFunctionAccessor)function).getAdd();
        int md = stack.getMaxDamage();
        float damage;
        if (add){
            int dmg = stack.getDamageValue();
            damage = Mth.clamp(((float )dmg)/md + (rollAvg * md),0,md);
        } else {
            damage = Mth.clamp(rollAvg * md,0,md);
        }
        stack.setDamageValue(Mth.floor(damage));
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.damage",Integer.toString((int)(rollAvg*100))), stack, conditionTexts);
    }
}
