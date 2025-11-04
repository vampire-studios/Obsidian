package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PlayersInRingTarget extends EntityTarget<Player> {
    private final double minRange, maxRange;

    public PlayersInRingTarget(double minRange, double maxRange) {
        super(List.of("PlayersInRing"));
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    @Override
    public List<Player> getTargets(LivingEntity caster) {
        return caster.level().getEntities(
                caster,
                new AABB(caster.position().subtract(maxRange, maxRange, maxRange), caster.position().add(maxRange, maxRange, maxRange)),
                entity -> entity instanceof Player player && !entity.isSpectator() && !player.isCreative() && player.isAlive()
        ).stream()
        .filter(entity -> entity.distanceTo(caster) >= minRange && entity.distanceTo(caster) <= maxRange)
        .map(entity -> ((Player) entity))
        .toList();
    }
}