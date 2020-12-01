package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.GeometryLivingEntityRenderer;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.EntityImpl;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class JsonEntityRenderer extends GeometryLivingEntityRenderer<EntityImpl, EntityJsonModel> {

    private final Entity entity;

    protected JsonEntityRenderer(EntityRendererFactory.Context context, Entity entity) {
        super(context, new EntityJsonModel(entity.entity_model.modelLocation), 1.0F);
        this.entity = entity;
    }

    @Override
    public Identifier getTexture(EntityImpl entityImpl) {
        return entity.getEntityTexture();
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