package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.server.ServerResourceData;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = LootDataManager.class, priority = 10000)
public class LootManagerMixin implements LootManagerConditionManager {

    @Shadow
    private Map<LootDataId<?>, ?> keyToValue;


    @Inject(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emi_loot_loadDirectTables(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ServerResourceData.loadDirectTables(manager);
    }

    /*@Inject(method = "validate(Ljava/util/Map;)V", at = @At("RETURN"))
    private void emi_loot_lootTablesAfterFabric(Map<LootDataType<?>, Map<Identifier, ?>> lootData, CallbackInfo ci){
        LootTableParser.parseLootTables((LootManager)(Object)this, keyToValue);
    }*/

    @Override
    public Map<LootDataId<?>, ?> getKeysToValues() {
        return keyToValue;
    }
}
