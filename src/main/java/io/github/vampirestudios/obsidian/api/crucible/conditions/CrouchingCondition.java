package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class CrouchingCondition extends Condition {
	private final boolean expectedOutcome;
	private final boolean applyToCaster;

	public CrouchingCondition(boolean expectedOutcome, boolean applyToCaster) {
		super(List.of("crouching", "iscrouching", "sneaking", "issneaking"));
		this.expectedOutcome = expectedOutcome;
		this.applyToCaster = applyToCaster;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		LivingEntity entityToCheck = applyToCaster ? caster : target;
		boolean isCrouching = entityToCheck.isCrouching();
		return isCrouching == expectedOutcome;
	}

	@Override
	public boolean applyToCaster() {
		return applyToCaster;
	}
}
