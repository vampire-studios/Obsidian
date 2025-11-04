package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class TargetTarget extends EntityTarget<LivingEntity> {
    public TargetTarget() {
        super(List.of("Target", "target"));
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        if (caster instanceof ServerPlayer player) {
            // For players, target the entity they are looking at if within range
            LivingEntity lookedAtEntity = getLookedAtEntity(player, 10.0); // 10-block range
            return lookedAtEntity != null ? List.of(lookedAtEntity) : Collections.emptyList();
        } else {
            // For mobs, return their target if available
            LivingEntity targetEntity = caster.getLastHurtMob();
            return targetEntity != null ? List.of(targetEntity) : Collections.emptyList();
        }
    }

    private LivingEntity getLookedAtEntity(ServerPlayer player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle().scale(range);
        Vec3 targetPosition = eyePosition.add(lookVector);

        AABB searchBox = new AABB(eyePosition, targetPosition).inflate(1.0);

        for (Entity entity : player.level().getEntities(player, searchBox)) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                AABB entityBox = entity.getBoundingBox().inflate(0.5);
                if (entityBox.contains(eyePosition) || entityBox.clip(eyePosition, targetPosition).isPresent()) {
                    return livingEntity;
                }
            }
        }
        return null;
    }

}
