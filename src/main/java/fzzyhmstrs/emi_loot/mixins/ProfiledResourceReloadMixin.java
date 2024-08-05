package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ProfiledReloadInstance.class)
public class ProfiledResourceReloadMixin {

    @Inject(method = "method_18355", at = @At("RETURN"), cancellable = true)
    private static void emi_loot_readTablesAfterFabricForRealProfiled(Executor executor, PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager resourceManager, PreparableReloadListener reloader, Executor prepare, Executor apply, CallbackInfoReturnable<CompletableFuture> cir){
        if (reloader instanceof LootDataManager){
            cir.setReturnValue(cir.getReturnValue().thenRun(() -> LootTableParser.parseLootTables((LootDataManager) reloader,((LootManagerConditionManager)reloader).getKeysToValues())));
        }
    }


}
