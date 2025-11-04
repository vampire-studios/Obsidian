package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EntitiesInConeTarget extends EntityTarget<LivingEntity> {
    private final double radius;
    private final double angle;

    public EntitiesInConeTarget(double radius, double angle) {
        super(List.of("EntitiesInCone", "cone"));
        this.radius = radius;
        this.angle = angle;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        List<LivingEntity> targets = new ArrayList<>();
        Vec3 forward = caster.getLookAngle();
        for (LivingEntity entity : caster.level().getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius))) {
            if (entity != caster && entity.position().distanceTo(caster.position()) <= radius) {
                Vec3 toTarget = entity.position().subtract(caster.position()).normalize();
                double angleToTarget = Math.acos(forward.dot(toTarget));
                if (Math.toDegrees(angleToTarget) <= angle / 2) {
                    targets.add(entity);
                }
            }
        }
        return targets;
    }
}
