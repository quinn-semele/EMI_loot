package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class DataPackContentsMixin {

    @Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
    private void emi_loot_postProcessBuildersAfterTagReload(CallbackInfo ci){
        LootTableParser.postProcess(LootTableParser.PostProcessor.TAG);
    }

}
