package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class EntityImpl extends PathAwareEntity/* implements IAnimatable*/ {
//    AnimationFactory factory = new AnimationFactory(this);

    private Entity entity;

    public EntityImpl(EntityType<EntityImpl> entityType_1, World world_1, Entity entity) {
        super(entityType_1, world_1);
        this.entity = entity;
    }

    /*private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        for (String animation : entity.entity_model.animationNames) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, true));
        }
        return PlayState.CONTINUE;
    }*/

    @Override
    public float getHealth() {
        return entity.components.health.value;
    }

    /*@Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }*/

}