package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EntityEmiStack extends EmiStack {
    private final @Nullable Entity entity;
    private final EntityRenderContext ctx;

    protected EntityEmiStack(@Nullable Entity entity) {
        this(entity,8.0);
    }

    protected EntityEmiStack(@Nullable Entity entity, double scale) {
        this.entity = entity;
        if (entity != null) {
            boolean hasTransform = ClientResourceData.MOB_ROTATIONS.containsKey(entity.getType());
            Vector3f transform = ClientResourceData.MOB_ROTATIONS.getOrDefault(entity.getType(),new Vector3f(0,0,0)).mul(0.017453292F);
            ctx = new EntityRenderContext(scale,hasTransform,transform);
        } else {
            ctx = new EntityRenderContext(scale,false,new Vector3f(0,0,0));
        }
    }

    public static EntityEmiStack of(@Nullable Entity entity) {
        return new EntityEmiStack(entity);
    }

    public static EntityEmiStack ofScaled(@Nullable Entity entity, double scale) {
        return new EntityEmiStack(entity, scale);
    }

    @Override
    public EmiStack copy() {
        EntityEmiStack stack = new EntityEmiStack(entity);
        stack.setRemainder(getRemainder().copy());
        stack.comparison = comparison;
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return entity == null;
    }

    @Override
    public void render(GuiGraphics matrices, int x, int y, float delta, int flags) {
        if (entity != null) {
            if (entity instanceof LivingEntity living)
                renderEntity(matrices.pose(),x + 8, (int) (y + 8 + ctx.size), ctx, living);
            else
                renderEntity(matrices.pose(),(int) (x + (2 * ctx.size / 2)), (int) (y + (2 * ctx.size)), ctx, entity);
        }
    }

    @Override
    public DataComponentPatch getComponentChanges() {
        return DataComponentPatch.EMPTY;
    }

    @Override
    public Object getKey() {
        return entity;
    }

    @Override
    public ResourceLocation getId() {
        if (entity == null) throw new RuntimeException("Entity is null");
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> list = new ArrayList<>();
        if (entity != null) {
            list.addAll(getTooltipText().stream().map(EmiPort::ordered).map(ClientTooltipComponent::create).toList());
            String mod = EmiUtil.getModName(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(mod, ChatFormatting.BLUE, ChatFormatting.ITALIC))));
            if (!getRemainder().isEmpty()) {
                list.add(new RemainderTooltipComponent(this));
            }
        }
        return list;
    }

    @Override
    public Component getName() {
        return entity != null ? entity.getName() : EmiPort.literal("yet another missingno");
    }

    public static void renderEntity(PoseStack matrices,int x, int y, EntityRenderContext ctx, LivingEntity entity) {
        Minecraft client = Minecraft.getInstance();

        double width = client.getWindow().getGuiScaledWidth();
        double height = client.getWindow().getGuiScaledHeight();
        float mouseX = (float)(client.mouseHandler.xpos() * width / (double)client.getWindow().getScreenWidth());
        float mouseY = (float)(client.mouseHandler.ypos() * height / (double)client.getWindow().getScreenHeight());
        double posX = mouseX - width/2 + 63;
        double posY = mouseY - height/2;
        float f = (float)Math.atan(-posX / 40.0F);
        float g = (float)Math.atan(-posY / 40.0F);

        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.mul(matrices.last().pose());
        matrixStack.translate(x, y, 1050.0f);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack2 = new PoseStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * Mth.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * Mth.sin(ctx.transform.z));
        if (ctx.hasTransform){
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x,ctx.transform.y,ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        matrixStack2.mulPose(quaternion);
        float h = entity.yBodyRot;
        float i = entity.getYRot();
        float j = entity.getXRot();
        float k = entity.yHeadRotO;
        float l = entity.yHeadRot;
        entity.yBodyRot = 180.0F + (f * 20.0F * Mth.cos(ctx.transform.z) + (g * 20.0F * Mth.sin(ctx.transform.z)));
        entity.setYRot(180.0F + (f * 40.0F * Mth.cos(ctx.transform.z) + (g * 40.0F * Mth.sin(ctx.transform.z))));
        entity.setXRot((-g * 20.0F * Mth.cos(ctx.transform.z)) + (- f * 20.0F * Mth.sin(ctx.transform.z)) );
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
        matrixStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public static void renderEntity(PoseStack matrices,int x, int y, EntityRenderContext ctx, Entity entity) {
        Minecraft client = Minecraft.getInstance();
        MouseHandler mouse = client.mouseHandler;
        float w = 1920;
        float h = 1080;
        Screen screen = client.screen;
        if (screen != null) {
            w = screen.width;
            h = screen.height;
        }
        float mouseX = (float) ((w + 51) - mouse.xpos());
        float mouseY = (float) ((h + 75 - 50) - mouse.ypos());
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.mul(matrices.last().pose());
        matrixStack.translate(x, y, 1050.0f);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack2 = new PoseStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * Mth.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * Mth.sin(ctx.transform.z));
        if (ctx.hasTransform){
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x,ctx.transform.y,ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        matrixStack2.mulPose(quaternion);
        float i = entity.getYRot();
        float j = entity.getXRot();
        entity.setYRot(180.0F + (f * 40.0F * Mth.cos(ctx.transform.z) + (g * 40.0F * Mth.sin(ctx.transform.z))));
        entity.setXRot((-g * 20.0F * Mth.cos(ctx.transform.z)) + (- f * 20.0F * Mth.sin(ctx.transform.z)) );
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        entity.setYRot(i);
        entity.setXRot(j);
        matrixStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    private record EntityRenderContext(double size, boolean hasTransform, Vector3f transform){
        static EntityRenderContext EMPTY = new EntityRenderContext(8.0,false,new Vector3f(0,0,0));
    }
}
