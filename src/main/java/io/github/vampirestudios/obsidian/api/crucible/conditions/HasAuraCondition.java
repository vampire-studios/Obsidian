package io.github.vampirestudios.obsidian.api.crucible.conditions;

import io.github.vampirestudios.obsidian.api.crucible.aura.AuraManager;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class HasAuraCondition extends Condition {
	private final String auraId;
	private final boolean expectedOutcome;

	public HasAuraCondition(String auraId, boolean expectedOutcome) {
		super(List.of("hasaura", "hasAuraCondition"));
		this.auraId = auraId;
		this.expectedOutcome = expectedOutcome;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		boolean hasAura = AuraManager.hasAura(target, auraId);
		return hasAura == expectedOutcome;
	}

	@Override
	public boolean applyToCaster() {
		return false;
	}
}
