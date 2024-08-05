package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.LightningBoltPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TypeSpecificPredicateParser {

    public static Component parseTypeSpecificPredicate(EntitySubPredicate predicate){
        if (predicate instanceof LightningBoltPredicate){
            return parseLightningBoltPredicate((LightningBoltPredicate)predicate);
        }
        
        if (predicate instanceof FishingHookPredicate){
            return parseFishingHookPredicate((FishingHookPredicate)predicate);
        }

        if (predicate instanceof PlayerPredicate){
            return parsePlayerPredicate((PlayerPredicate)predicate);
        }

        if (predicate instanceof SlimePredicate){
            return parseSlimePredicate((SlimePredicate)predicate);
        }

        if (predicate instanceof EntityVariantPredicate.SubPredicate<?> variantPredicate) {
            if (variantPredicate.variant() instanceof CatVariant cat) {
                ResourceLocation id = BuiltInRegistries.CAT_VARIANT.getKey(cat);
                if (id != null){
                    MutableComponent catVar = Text.translatable("emi_loot.entity_predicate.type_specific.cat." + id);
                    return Text.translatable("emi_loot.entity_predicate.type_specific.cat",catVar.getString());
                }
            } else if (variantPredicate.variant() instanceof FrogVariant frog) {
                ResourceLocation id = BuiltInRegistries.FROG_VARIANT.getKey(frog);
                if (id != null){
                    MutableComponent frogVar = Text.translatable("emi_loot.entity_predicate.type_specific.frog." + id);
                    return Text.translatable("emi_loot.entity_predicate.type_specific.frog",frogVar.getString());
                }
            }
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Type specific predicate undefined or unparsable. Affects table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
    
    
    public static Component parseLightningBoltPredicate(LightningBoltPredicate predicate){
        MinMaxBounds.Ints blocksSetOnFire = predicate.blocksSetOnFire();
        if (!blocksSetOnFire.equals(MinMaxBounds.Ints.ANY)){
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    NumberProcessors.processNumberRange(
                        blocksSetOnFire,
                        "emi_loot.entity_predicate.type_specific.lightning.blocks",
                        "emi_loot.entity_predicate.type_specific.lightning.blocks_2",
                            "emi_loot.entity_predicate.type_specific.lightning.blocks_3",
                            "emi_loot.entity_predicate.type_specific.lightning.blocks_4",
                        ""
                    )
            );
        }

        Optional<EntityPredicate> entityStruck = predicate.entityStruck();
        if(entityStruck.isPresent()){
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    Text.translatable(
                        "emi_loot.entity_predicate.type_specific.lightning.struck",
                        EntityPredicateParser.parseEntityPredicate(entityStruck.get()).getString()
                    )
            );
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Lightning bolt predicate empty or unparsable. Affects table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
    
    
    public static Component parseFishingHookPredicate(FishingHookPredicate predicate){
        Optional<Boolean> inOpenWater = predicate.inOpenWater();
        return (inOpenWater.isPresent() && inOpenWater.get()) ? Text.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_true") : Text.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_false");
    }

    public static Component parsePlayerPredicate(PlayerPredicate predicate){
        MinMaxBounds.Ints experienceLevel = predicate.level();
        if (!experienceLevel.equals(MinMaxBounds.Ints.ANY)){
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    NumberProcessors.processNumberRange(
                            experienceLevel,
                            "emi_loot.entity_predicate.type_specific.player.level",
                            "emi_loot.entity_predicate.type_specific.player.level_2",
                            "emi_loot.entity_predicate.type_specific.player.level_3",
                            "emi_loot.entity_predicate.type_specific.player.level_4",
                            ""
                    )
            );
        }

        Optional<GameMode> gameMode = predicate.gameType();
        if (gameMode.isPresent()){
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    Text.translatable("emi_loot.entity_predicate.type_specific.player.gamemode",gameMode.get().getName()));
        }

        List<PlayerPredicate.StatMatcher<?>> stats = predicate.stats();
        if (!stats.isEmpty()) {
            List<MutableComponent> list = new LinkedList<>();
            for (PlayerPredicate.StatMatcher<?> stat : stats) {
                String name = stat.stat().get().getName();
                String[] namePieces = name.split(":");
                if (namePieces.length == 2) {
                    String typeId = namePieces[0].replace('.', ':');
                    String valueId = namePieces[1].replace('.', ':');
                    MutableComponent num = NumberProcessors.processNumberRange(
                            stat.range(),
                            "emi_loot.entity_predicate.type_specific.player.stats.exact",
                            "emi_loot.entity_predicate.type_specific.player.stats.between",
                            "emi_loot.entity_predicate.type_specific.player.stats.at_least",
                            "emi_loot.entity_predicate.type_specific.player.stats.at_most",
                            "emi_loot.entity_predicate.type_specific.player.stats.fallback"
                    );
                    list.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.stats.type." + typeId, valueId, num));
                }
            }
            if (!list.isEmpty()) {
                return Text.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        Object2BooleanMap<ResourceLocation> recipes = predicate.recipes();
        if (!recipes.isEmpty()){
            List<MutableComponent> list = new LinkedList<>();
            for (Object2BooleanMap.Entry<ResourceLocation> entry: recipes.object2BooleanEntrySet()){
                list.add(
                        entry.getBooleanValue()
                        ?
                        Text.translatable("emi_loot.entity_predicate.type_specific.player.recipe_true",entry.getKey())
                        :
                        Text.translatable("emi_loot.entity_predicate.type_specific.player.recipe_false",entry.getKey())
                );
            }
            if (!list.isEmpty()){
                return Text.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements = predicate.advancements();
        if (!advancements.isEmpty()){
            List<MutableComponent> list = new LinkedList<>();
            for (Map.Entry<ResourceLocation, PlayerPredicate.AdvancementPredicate> entry: advancements.entrySet()){
                String idString = entry.getKey().toString();
                PlayerPredicate.AdvancementPredicate advancementPredicate = entry.getValue();
                if (advancementPredicate instanceof PlayerPredicate.AdvancementDonePredicate){
                    boolean done = ((PlayerPredicate.AdvancementDonePredicate) advancementPredicate).done();
                    if (done){
                        list.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.adv.id_true",idString));
                    } else {
                        list.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.adv.id_false",idString));
                    }
                } else if (advancementPredicate instanceof PlayerPredicate.AdvancementCriterionsPredicate){
                    Object2BooleanMap<String> criteria = ((PlayerPredicate.AdvancementCriterionsPredicate) advancementPredicate).criteria();
                    if (!criteria.isEmpty()) {
                        List<MutableComponent> list2 = new LinkedList<>();
                        for (Object2BooleanMap.Entry<String> criteriaEntry : criteria.object2BooleanEntrySet()){
                            if (criteriaEntry.getBooleanValue()){
                                list2.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_true",criteriaEntry.getKey()));
                            } else {
                                list2.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_false",criteriaEntry.getKey()));
                            }
                        }
                        list.add(Text.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_base", idString, ListProcessors.buildAndList(list2)));
                    }
                }
            }
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.player", ListProcessors.buildAndList(list));
        }

        Optional<EntityPredicate> entityPredicate = predicate.lookingAt();
        if (entityPredicate.isPresent()){
            return Text.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    Text.translatable("emi_loot.entity_predicate.type_specific.player.looking", EntityPredicateParser.parseEntityPredicate(entityPredicate.get())));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Lightning bolt predicate empty or unparsable. Affects table: "  + LootTableParser.currentTable);
        return Text.translatable("emi_loot.predicate.invalid");
    }
    
    public static Component parseSlimePredicate(SlimePredicate predicate){
        MinMaxBounds.Ints size = predicate.size();
        if (size.equals(MinMaxBounds.Ints.ANY)){
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Undefined slime size predicate in table: "  + LootTableParser.currentTable);
            return Text.translatable("emi_loot.predicate.invalid");
        }
        return NumberProcessors.processNumberRange(
                size,
                "emi_loot.entity_predicate.type_specific.slime",
                "emi_loot.entity_predicate.type_specific.slime_2",
                "emi_loot.entity_predicate.type_specific.slime_3",
                "emi_loot.entity_predicate.type_specific.slime_4",
                ""
        );
    }
}
