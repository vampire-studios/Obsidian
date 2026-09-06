package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlayersInRadiusLocationTarget extends LocationTarget {
	private final double radius;

	public PlayersInRadiusLocationTarget(double radius) {
		super(List.of("PlayersInRadiusLocation", "PIRL"));
		this.radius = radius;
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		return caster.level().getEntities(
				caster,
				new AABB(caster.position().subtract(radius, radius, radius), caster.position().add(radius, radius, radius)),
				entity -> entity instanceof Player player && !entity.isSpectator() && !player.isCreative() && player.isAlive()
						&& entity.distanceTo(caster) <= radius
		).stream().map(Entity::position).toList();
	}
}