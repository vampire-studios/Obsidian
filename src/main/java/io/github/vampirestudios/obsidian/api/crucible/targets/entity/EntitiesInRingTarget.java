package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EntitiesInRingTarget extends EntityTarget<LivingEntity> {
    private final double minRange, maxRange;

    public EntitiesInRingTarget(double minRange, double maxRange) {
        super(List.of("EntitiesInRing", "@EIRR"));
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        return caster.level().getEntities(
                caster,
                new AABB(caster.position().subtract(maxRange, maxRange, maxRange), caster.position().add(maxRange, maxRange, maxRange)),
                EntitySelector.LIVING_ENTITY_STILL_ALIVE
        ).stream()
        .filter(entity -> entity.distanceTo(caster) >= minRange && entity.distanceTo(caster) <= maxRange)
        .map(entity -> ((LivingEntity) entity))
        .toList();
    }
}