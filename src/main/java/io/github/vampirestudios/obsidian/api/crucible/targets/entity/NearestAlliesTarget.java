package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
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
				.filter(entity -> isAlly(caster, entity))
				.sorted(Comparator.comparingDouble(caster::distanceToSqr))
				.limit(amount)
				.toList();
	}

	/**
	 * An ally is the caster itself or anything the game already considers on its side — scoreboard team
	 * mates, and tamed animals, whose own allegiance check covers their owner. This used to always
	 * answer no, which left the target permanently empty.
	 */
	private boolean isAlly(LivingEntity caster, LivingEntity entity) {
		if (!entity.isAlive()) return false;
		return entity == caster || caster.isAlliedTo(entity) || entity.isAlliedTo(caster);
	}
}
