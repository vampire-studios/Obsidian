package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class EntityImpl extends PathAwareEntity {
    private Entity entity;

    public EntityImpl(EntityType<EntityImpl> entityType_1, World world_1, Entity entity) {
        super(entityType_1, world_1);
        this.entity = entity;
    }

    @Override
    public boolean hasCustomName() {
        return super.hasCustomName();
    }
}