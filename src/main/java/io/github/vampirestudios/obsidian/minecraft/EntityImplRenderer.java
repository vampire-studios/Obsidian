package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

public class EntityImplRenderer extends MobEntityRenderer<EntityImpl, EntityModel<EntityImpl>> {
    private final Entity entity;
    public EntityImplRenderer(EntityRenderDispatcher entityRenderDispatcher, Entity entity) {
        super(entityRenderDispatcher, entity.getEntityModel(), 0.5F);
        this.entity = entity;
    }

    @Override
    public Identifier getTexture(EntityImpl entity) {
        return this.entity.getEntityTexture();
    }
}
