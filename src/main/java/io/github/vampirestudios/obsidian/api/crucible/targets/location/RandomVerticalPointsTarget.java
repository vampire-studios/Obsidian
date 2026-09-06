package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RandomVerticalPointsTarget extends LocationTarget {
	private final int amount;
	private final double radius;
	private final double height;

	public RandomVerticalPointsTarget(int amount, double radius, double height) {
		super(List.of("RandomVerticalPoints", "randomVertical"));
		this.amount = amount;
		this.radius = radius;
		this.height = height;
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		List<Vec3> locations = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			double angle = Math.random() * 2 * Math.PI;
			double dist = Math.random() * radius;
			double y = caster.getY() + Math.random() * height;
			double x = caster.getX() + dist * Math.cos(angle);
			double z = caster.getZ() + dist * Math.sin(angle);
			locations.add(new Vec3(x, y, z));
		}
		return locations;
	}
}
