package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CircleAroundCasterTarget extends LocationTarget {
	private final double radius;
	private final int points;
	private final double height;

	public CircleAroundCasterTarget(double radius, int points, double height) {
		super(List.of("CircleAroundCaster", "circleAround"));
		this.radius = radius;
		this.points = points;
		this.height = height;
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		List<Vec3> locations = new ArrayList<>();
		for (int i = 0; i < points; i++) {
			double angle = 2 * Math.PI * i / points;
			double x = caster.getX() + radius * Math.cos(angle);
			double z = caster.getZ() + radius * Math.sin(angle);
			locations.add(new Vec3(x, caster.getY() + height, z));
		}
		return locations;
	}
}
