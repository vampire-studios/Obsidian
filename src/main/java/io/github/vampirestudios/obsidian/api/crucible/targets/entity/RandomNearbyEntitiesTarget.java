package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;

public class RandomNearbyEntitiesTarget extends EntityTarget<LivingEntity> {
    private final int amount;
    private final double radius;

    public RandomNearbyEntitiesTarget(int amount, double radius) {
        super(List.of("RandomNearbyEntities", "randomEntities"));
        this.amount = amount;
        this.radius = radius;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        List<LivingEntity> allTargets = caster.level().getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius));
        Collections.shuffle(allTargets);
        return allTargets.stream().limit(amount).toList();
    }
}
