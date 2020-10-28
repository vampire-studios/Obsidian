package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.entity.Entity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class GeckoEntityModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

    public Entity entity;

    public GeckoEntityModel(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Identifier getAnimationFileLocation(T entityIn) {
        return entity.entity_model.animationFileLocation;
    }

    @Override
    public Identifier getModelLocation(T entityIn) {
        return entity.entity_model.modelLocation;
    }

    @Override
    public Identifier getTextureLocation(T entityIn) {
        return entity.entity_model.textureLocation;
    }
}
