package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CustomEntityRenderer extends MobEntityRenderer<EntityImpl, EntityModel<EntityImpl>> {

    private final Entity entity;

    public CustomEntityRenderer(EntityRenderDispatcher context, Entity entity) {
        super(context, entity.information.getEntityModel(), 1.0F);
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

    protected void scale(EntityImpl entity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(g, g, g);
    }
}