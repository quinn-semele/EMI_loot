package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.render.EmiRenderable;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class LootSimplifiedRenderer implements EmiRenderable {

    private final int u;
    private final int v;
    private final Identifier SPRITE_SHEET = Identifier.of(EMILoot.MOD_ID,"textures/gui/emi_recipe_textures.png");

    public LootSimplifiedRenderer(int u, int v){
        this.u = u;
        this.v = v;
    }


    @Override
    public void render(DrawContext draw, int x, int y, float delta) {
        draw.drawTexture(SPRITE_SHEET, x, y, u, v, 16, 16, 32, 16);
    }
}
