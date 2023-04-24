package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CustomEntityRenderer extends MobRenderer<EntityImpl, EntityModel<EntityImpl>> {

    private final Entity entity;

    public CustomEntityRenderer(EntityRendererProvider.Context context, Entity entity) {
        super(context, entity.information.getNewEntityModel(context), entity.shadowSize);
        this.entity = entity;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityImpl entityImpl) {
        return entity.information.getEntityTexture();
    }

    @Override
    protected boolean shouldShowName(EntityImpl mob) {
        return false;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityImpl entity, boolean showBody, boolean translucent, boolean showOutline) {
        ResourceLocation identifier = this.getTextureLocation(entity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(identifier);
        } else if (showBody) {
            return this.model.renderType(identifier);
        } else {
            return showOutline ? RenderType.outline(identifier) : null;
        }
    }

    @Override
    protected void scale(EntityImpl entity, PoseStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(g, g, g);
    }

}