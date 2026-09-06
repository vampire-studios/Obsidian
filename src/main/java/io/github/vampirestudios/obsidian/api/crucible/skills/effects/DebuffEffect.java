package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DebuffEffect extends Effect {
	private final MobEffectInstance effect;

	public DebuffEffect(MobEffectInstance effect) {
		this.effect = effect;
	}

	@Override
	public void apply(LivingEntity target) {
		target.addEffect(new MobEffectInstance(effect));
	}
}
