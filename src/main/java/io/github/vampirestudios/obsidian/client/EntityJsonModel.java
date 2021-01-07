package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.EntityGeometryModel;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class EntityJsonModel extends EntityGeometryModel<EntityImpl> {

    public EntityJsonModel(Identifier model) {
        super(RenderLayer::getEntityCutoutNoCull, model);
    }

    @Override
    public void setAngles(EntityImpl entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

}