package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Passes when the target is close enough to the caster. The old placeholder had no way to reach the
 * target; the condition API hands it in now, so this is a plain distance check.
 */
public class TargetWithinDistanceCondition extends Condition {
	private final double distance;

	public TargetWithinDistanceCondition(double distance) {
		super(List.of("targetwithin", "nearby", "inrange", "proximity"));
		this.distance = distance;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		if (caster == null || target == null || caster == target) return false;
		if (caster.level() != target.level()) return false;

		return caster.distanceTo(target) <= distance;
	}

	@Override
	public boolean applyToCaster() {
		return false;
	}
}
