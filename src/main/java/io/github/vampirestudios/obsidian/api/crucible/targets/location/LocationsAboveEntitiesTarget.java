package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LocationsAboveEntitiesTarget extends LocationTarget {
	private final double radius;
	private final double height;
	private final int amount;

	public LocationsAboveEntitiesTarget(double radius, double height, int amount) {
		super(List.of("LocationsAboveEntities", "aboveEntities"));
		this.radius = radius;
		this.height = height;
		this.amount = amount;
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		List<Vec3> locations = new ArrayList<>();
		List<LivingEntity> entities = caster.level().getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius));
		entities.stream().limit(amount).forEach(entity -> locations.add(entity.position().add(0, height, 0)));
		return locations;
	}
}
