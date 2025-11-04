package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class NearestAlliesTarget extends EntityTarget<LivingEntity> {
    private final int amount;
    private final double radius;

    public NearestAlliesTarget(int amount, double radius) {
        super(List.of("NearestAllies", "allies"));
        this.amount = amount;
        this.radius = radius;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        return caster.level().getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius))
                .stream()
                .filter(entity -> isAlly(caster, entity))  // Define isAlly logic as needed
                .limit(amount)
                .toList();
    }

    private boolean isAlly(LivingEntity caster, LivingEntity entity) {
        // Custom logic to determine if entity is an ally of caster
        return false; // Placeholder
    }
}
