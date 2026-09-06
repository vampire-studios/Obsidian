package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class HasPotionEffectCondition extends Condition {
	private final MobEffect potionEffect;
	private final int minAmplifier;

	public HasPotionEffectCondition(MobEffect potionEffect, int minAmplifier) {
		super(List.of("hasPotionEffect", "potion", "haspotion"));
		this.potionEffect = potionEffect;
		this.minAmplifier = minAmplifier;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		MobEffectInstance effect = target.getEffect(Holder.direct(potionEffect));
		return effect != null && effect.getAmplifier() >= minAmplifier;
	}

	@Override
	public boolean applyToCaster() {
		return false;
	}
}
