package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.AnimatableEntityRenderer;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class JsonEntityRenderer extends AnimatableEntityRenderer<EntityImpl, EntityJsonModel> {

    private final Entity entity;

    protected JsonEntityRenderer(EntityRendererFactory.Context context, Entity entity) {
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

}