package io.github.vampirestudios.obsidian.client.renderer;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CustomEntityRenderer extends MobEntityRenderer<EntityImpl, EntityModel<EntityImpl>> {

    private final Entity entity;

    public CustomEntityRenderer(EntityRendererFactory.Context context, Entity entity) {
        super(context, entity.information.getNewEntityModel(context), entity.shadowSize);
        this.entity = entity;
    }

    @Override
    public Identifier getTexture(EntityImpl entityImpl) {
        return entity.information.getEntityTexture();
    }

    @Override
    protected boolean hasLabel(EntityImpl livingEntity) {
        return false;
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(EntityImpl entity, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        } else if (showBody) {
            return this.model.getLayer(identifier);
        } else {
            return showOutline ? RenderLayer.getOutline(identifier) : null;
        }
    }

    @Override
    protected void scale(EntityImpl entity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(g, g, g);
    }

}