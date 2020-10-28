package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

public class EntityImpl extends PathAwareEntity implements IAnimatable {

    private Entity entity;

    public EntityImpl(EntityType<EntityImpl> entityType_1, World world_1, Entity entity) {
        super(entityType_1, world_1);
        this.entity = entity;
    }

    @Override
    public float getHealth() {
        return entity.components.health.value;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }

}