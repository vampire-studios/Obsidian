package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.world.entity.LivingEntity;

public class DamageEffect extends Effect {
	private final float amount;

	public DamageEffect(float amount) {
		this.amount = amount;
	}

	@Override
	public void apply(LivingEntity target) {
		target.hurtOrSimulate(target.damageSources().magic(), amount);
	}
}
