package io.github.vampirestudios.obsidian;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class EntityGeometryModel<T extends Entity> extends GeometryModel{

    public EntityGeometryModel(Function<Identifier, RenderLayer> layerFactory, Identifier modelData) {
        super(layerFactory, modelData);
    }

    public abstract void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) { }
}