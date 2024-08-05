package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.render.EmiRenderable;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class LootSimplifiedRenderer implements EmiRenderable {

    private final int u;
    private final int v;
    private final ResourceLocation SPRITE_SHEET = Identifier.of(EMILoot.MOD_ID,"textures/gui/emi_recipe_textures.png");

    public LootSimplifiedRenderer(int u, int v){
        this.u = u;
        this.v = v;
    }


    @Override
    public void render(GuiGraphics draw, int x, int y, float delta) {
        draw.blit(SPRITE_SHEET, x, y, u, v, 16, 16, 32, 16);
    }
}
