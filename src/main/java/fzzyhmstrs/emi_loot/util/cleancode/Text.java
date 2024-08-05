package fzzyhmstrs.emi_loot.util.cleancode;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Text {

    public static MutableComponent translatable(String key){
        return Component.translatable(key);
    }
    
    public static MutableComponent translatable(String key, Object ... args){
        return Component.translatable(key, args);
    }

    public static MutableComponent literal(String msg){
        return Component.literal(msg);
    }
    
    public static MutableComponent empty(){
        return Component.empty();
    }

}
