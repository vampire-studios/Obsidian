package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.world.entity.LivingEntity;

public class HealEffect extends Effect {
	private final float amount;

	public HealEffect(float amount) {
		this.amount = amount;
	}

	@Override
	public void apply(LivingEntity target) {
		target.heal(amount);
	}
}
