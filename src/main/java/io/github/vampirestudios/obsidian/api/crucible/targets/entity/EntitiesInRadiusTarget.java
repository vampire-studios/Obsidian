package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class EntitiesInRadiusTarget extends EntityTarget<LivingEntity> {
	private final double radius;
	private final Predicate<Entity> livingOnly;

	public EntitiesInRadiusTarget(double radius, boolean livingOnly) {
		super(List.of("EntitiesInRadius", "livingEntitiesInRadius", "livingInRadius", "allInRadius", "EIR"));
		this.radius = radius;
		if (livingOnly) {
			this.livingOnly = EntitySelector.LIVING_ENTITY_STILL_ALIVE;
		} else {
			this.livingOnly = _ -> true;
		}
	}

	@Override
	public List<LivingEntity> getTargets(LivingEntity caster) {
		return caster.level().getEntities(
				caster,
				new AABB(caster.position().subtract(radius, radius, radius), caster.position().add(radius, radius, radius)),
				livingOnly
		).stream().filter(entity -> entity.distanceTo(caster) <= radius).map(entity -> ((LivingEntity) entity)).toList();
	}
}