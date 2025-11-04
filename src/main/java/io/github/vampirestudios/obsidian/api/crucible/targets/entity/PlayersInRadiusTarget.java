package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PlayersInRadiusTarget extends EntityTarget<Player> {
    private final double radius;

    public PlayersInRadiusTarget(double radius) {
        super(List.of("PlayersInRadius", "PIR"));
        this.radius = radius;
    }

    @Override
    public List<Player> getTargets(LivingEntity caster) {
        return caster.level().getEntities(caster, new AABB(caster.position().subtract(radius, radius, radius), caster.position().add(radius, radius, radius)),
                entity -> entity instanceof Player player && !entity.isSpectator() && !player.isCreative() && player.isAlive()
        ).stream().filter(entity -> entity.distanceTo(caster) <= radius).map(entity -> ((Player) entity)).toList();
    }
}