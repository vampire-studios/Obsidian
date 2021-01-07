package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.AnimatableEntityRenderer;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class JsonEntityRenderer extends AnimatableEntityRenderer<EntityImpl, EntityJsonModel> {

    private final Entity entity;

    protected JsonEntityRenderer(EntityRenderDispatcher context, Entity entity) {
        super(context, new EntityJsonModel(entity.information.entity_model.modelLocation), 1.0F);
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