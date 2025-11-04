package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EntitiesAboveTarget extends EntityTarget<LivingEntity> {
    private final double radius;
    private final double height;

    public EntitiesAboveTarget(double radius, double height) {
        super(List.of("EntitiesAbove", "above"));
        this.radius = radius;
        this.height = height;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        return caster.level().getEntitiesOfClass(LivingEntity.class, new AABB(caster.getX() - radius, caster.getY(), caster.getZ() - radius, caster.getX() + radius, caster.getY() + height, caster.getZ() + radius))
                .stream()
                .filter(entity -> entity != caster)
                .toList();
    }
}
