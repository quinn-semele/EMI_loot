package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fzzyhmstrs.emi_loot.util.IconEmiWidget.FRAME_ID;

public class IconGroupEmiWidget extends Widget {

    public IconGroupEmiWidget(int x, int y, ClientBuiltPool pool){
        this.x = x;
        this.y = y;
        List<IconEmiWidget> list = new LinkedList<>();
        for (int i = 0; i < pool.list().size(); i++){
            Tuple<Integer, Component> pair = pool.list().get(i);
            int xOffset = i / 2 * 11;
            int yOffset = i % 2 * 11;
            list.add(new IconEmiWidget(x + xOffset, y + yOffset,pair.getA(),pair.getB()));
        }
        this.icons = list;
        this.iconsWidth = 12 + (((icons.size() - 1)/2) * 11);
        this.itemsWidth = 2 + pool.stackMap().size() * 20;
        this.width = iconsWidth + this.itemsWidth;
        this.bounds = new Bounds(x,y, width,23);
        List<SlotWidget> list2 = new LinkedList<>();
        int itemXOffset = iconsWidth + 2;
        for(Map.Entry<Float, EmiIngredient> entry: pool.stackMap().float2ObjectEntrySet()){
            String rounded = FloatTrimmer.trimFloatString(entry.getKey());
            SlotWidget widget = new SlotWidget(entry.getValue(),itemXOffset + x,y + 3).appendTooltip(Text.translatable("emi_loot.percent_chance",rounded));
            itemXOffset +=20;
            list2.add(widget);
        }
        items = list2;
    }

    private final List<IconEmiWidget> icons;
    private final List<SlotWidget> items;
    private final int x,y;
    private final int iconsWidth;
    private final int itemsWidth;
    private final int width;
    private final Bounds bounds;

    public int getIconsWidth(){
        return iconsWidth;
    }
    public int getWidth(){
        return width;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY){
        for (IconEmiWidget icon : icons){
            if (icon.getBounds().contains(mouseX,mouseY)) return icon.getTooltip(mouseX, mouseY);
        }
        for (SlotWidget slot: items){
            if (slot.getBounds().contains(mouseX, mouseY)) return slot.getTooltip(mouseX, mouseY);
        }
        return List.of();
    }
    
    @Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
        for (SlotWidget slot: items){
            if (slot.getBounds().contains(mouseX, mouseY)){
                if(slot.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int widthRemaining = itemsWidth;
        int xNew = x + iconsWidth;
        do{
            int newWidth = Math.min(64,widthRemaining);
            draw.blit(FRAME_ID, xNew, y, newWidth, 1, 0, 0, newWidth, 1, 64, 16);
            xNew += newWidth;
            widthRemaining -= newWidth;
        } while (widthRemaining > 0);
        draw.fill(RenderType.gui(),x,x + width,y,y+1,0x555555);
        for (IconEmiWidget icon: icons){
            icon.render(draw, mouseX, mouseY, delta);
        }
        for (SlotWidget slot: items){
            slot.render(draw, mouseX, mouseY, delta);
        }
    }
}
