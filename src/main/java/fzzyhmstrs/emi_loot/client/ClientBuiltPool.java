package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiIngredient;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import java.util.List;

public record ClientBuiltPool(List<Tuple<Integer, Component>> list, Float2ObjectMap<EmiIngredient> stackMap){}
