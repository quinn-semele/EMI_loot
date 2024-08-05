package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadInstance.class)
public class SimpleResourceReloadMixin {

    @Inject(method = "method_18368", at = @At("RETURN"), cancellable = true)
    private static void emi_loot_readTablesAfterFabricForRealSimple(Executor executor, PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager resourceManager, PreparableReloadListener reloader, Executor prepare, Executor apply, CallbackInfoReturnable<CompletableFuture> cir){
        if (reloader instanceof LootDataManager){
            cir.setReturnValue(cir.getReturnValue().thenRun(() -> LootTableParser.parseLootTables((LootDataManager) reloader,((LootManagerConditionManager)reloader).getKeysToValues())));
        }
    }


}
