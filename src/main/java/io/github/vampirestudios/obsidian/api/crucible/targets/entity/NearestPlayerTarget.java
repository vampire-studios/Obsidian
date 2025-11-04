package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

public class NearestPlayerTarget extends EntityTarget<Player> {
    private final double radius;

    public NearestPlayerTarget(double radius) {
        super(List.of("NearestPlayer"));
        this.radius = radius;
    }

    @Override
    public List<Player> getTargets(LivingEntity caster) {
        return List.of(Objects.requireNonNull(caster.level().getNearestPlayer(caster, radius)));
    }
}