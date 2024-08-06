package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import java.util.*;

import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;

public class EntityEffectPredicateParser{

    public static Component parseEntityEffectPredicate(MobEffectsPredicate predicate){
        Map<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> effects = predicate.effectMap();
        List<MutableComponent> list = new LinkedList<>();
        for (Map.Entry<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> entry : effects.entrySet()) {
            Component name = entry.getKey().value().getDisplayName();
            MobEffectsPredicate.MobEffectInstancePredicate data = entry.getValue();
            MinMaxBounds.Ints amplifier = data.amplifier();
            if (!amplifier.equals(MinMaxBounds.Ints.ANY)){
                Optional<Integer> min = amplifier.min();
                Optional<Integer> max = amplifier.max();
                if (Objects.equals(min, max) && min.isPresent()){
                    list.add(Text.translatable("emi_loot.entity_predicate.effect.amplifier", name.getString(), min.get() + 1));
                } else if (min.isPresent() && max.isPresent()) {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.amplifier_2", name.getString(), min.get() + 1, max.get() + 1));
                } else {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.amplifier_3", name.getString()));
                }
            }

            MinMaxBounds.Ints duration = data.duration();
            if (!duration.equals(MinMaxBounds.Ints.ANY)){
                Optional<Integer> min = duration.min();
                Optional<Integer> max = duration.max();
                if (Objects.equals(min, max) && min.isPresent()){
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.duration", name.getString(), min.get() + 1));
                } else if (min.isPresent() && max.isPresent()) {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.duration_2", name.getString(), min.get() + 1, max.get() + 1));
                } else {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.duration_3", name.getString()));
                }
            }

            Optional<Boolean> ambient = data.ambient();
            if (ambient.isPresent()){
                if (ambient.get()){
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.ambient_true", name.getString()));
                } else {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.ambient_false", name.getString()));
                }
            }

            Optional<Boolean> visible = data.visible();
            if (visible.isPresent()){
                if (visible.get()){
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.visible_true", name.getString()));
                } else {
                    list.add( Text.translatable("emi_loot.entity_predicate.effect.visible_false", name.getString()));
                }
            }
            list.add(Text.translatable("emi_loot.entity_predicate.effect.fallback", name.getString()));
        
        }
        if (!list.isEmpty()){
            return Text.translatable("emi_loot.entity_predicate.effect_1", ListProcessors.buildAndList(list));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unparsable entity effect predicate in table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
}
