package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ListProcessors {
    
    public static MutableComponent buildAndList(List<MutableComponent> list){
        return buildAndList(list, 0);
    }

    public static MutableComponent buildAndList(List<MutableComponent> list, int currentIndex){
        if (list.isEmpty()) return Text.empty();
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return Text.translatable("emi_loot.predicate.and_2",list.get(currentIndex),buildAndList(list, currentIndex + 1).getString());
        } else {
            return Text.translatable("emi_loot.predicate.and_3",list.get(currentIndex),buildAndList(list, currentIndex + 1).getString());
        }
    }
    
    public static MutableComponent buildOrList(List<MutableComponent> list){
        return buildOrList(list, 0);
    }
    
    public static MutableComponent buildOrList(List<MutableComponent> list, int currentIndex){
        if (list.isEmpty()) return Text.empty();
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return Text.translatable("emi_loot.predicate.or_2",list.get(currentIndex),buildOrList(list, currentIndex + 1).getString());
        } else {
            return Text.translatable("emi_loot.predicate.or_3",list.get(currentIndex),buildOrList(list, currentIndex + 1).getString());
        }
    }

}
