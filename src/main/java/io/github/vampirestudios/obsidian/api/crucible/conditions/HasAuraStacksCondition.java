package io.github.vampirestudios.obsidian.api.crucible.conditions;

import io.github.vampirestudios.obsidian.api.crucible.aura.AuraManager;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Passes when an entity carries at least so many auras of one id. Stacking an aura means applying it
 * more than once, so the stack count is simply how many copies are attached.
 */
public class HasAuraStacksCondition extends Condition {
	private final String auraId;
	private final int minStacks;

	public HasAuraStacksCondition(String auraId, int minStacks) {
		super(List.of("hasaurastacks", "aura_stack_count", "aura_stack"));
		this.auraId = auraId;
		this.minStacks = minStacks;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		return AuraManager.countAura(target, auraId) >= minStacks;
	}

	@Override
	public boolean applyToCaster() {
		return false;
	}
}
