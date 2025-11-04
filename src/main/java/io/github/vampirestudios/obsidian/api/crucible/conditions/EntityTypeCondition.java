package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EntityTypeCondition extends Condition {
    private final EntityType<?> entityType;

    public EntityTypeCondition(EntityType<?> entityType) {
        super(List.of("entityType", "type", "isType"));
        this.entityType = entityType;
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        return target.getType() == entityType;
    }

    @Override
    public boolean applyToCaster() {
        return false;
    }
}
