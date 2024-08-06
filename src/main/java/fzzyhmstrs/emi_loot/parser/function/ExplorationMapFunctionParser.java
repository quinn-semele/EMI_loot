package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ExplorationMapLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import java.util.List;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ExplorationMapFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootItemFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        ItemStack mapStack;
        String typeKey = "emi_loot.map.unknown";
        if (!stack.is(Items.MAP)){
            mapStack = stack;
        } else {
            Holder<MapDecorationType> decoration = ((ExplorationMapLootFunctionAccessor)function).getDecoration();
            TagKey<Structure> destination = ((ExplorationMapLootFunctionAccessor)function).getDestination();
            mapStack = new ItemStack(Items.FILLED_MAP);
            MapItemSavedData.addTargetDecoration(mapStack, BlockPos.ZERO,"+",decoration);
            typeKey = "emi_loot.map."+ destination.location().getPath();
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.map", Text.translatable(typeKey).getString()), mapStack, conditionTexts);
    }
}
