package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class IconEmiWidget extends Widget {

    public IconEmiWidget(int x, int y, int keyIndex, Component text){
        this.tex = TextKey.getSpriteId(keyIndex);
        this.x = x;
        this.y = y;
        this.bounds = new Bounds(x,y,12,12);
        this.tooltipText = Collections.singletonList(ClientTooltipComponent.create(text.getVisualOrderText()));
    }

    static final ResourceLocation FRAME_ID = Identifier.of(EMILoot.MOD_ID,"textures/gui/icon_frame.png");
    //private static final Identifier SPRITE_ID = Identifier.of(EMILoot.MOD_ID,"textures/gui/icon_sprites.png");

    private final ResourceLocation tex;
    private int x, y;
    private final Bounds bounds;
    private final List<ClientTooltipComponent> tooltipText;



    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY){
        return tooltipText;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.blit(FRAME_ID, x, y, 12, 12, 0, 0, 12, 12, 64, 16);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.blit(tex, x + 2, y + 2, 8, 8, 0, 0, 8, 8, 8, 8);
    }
}
