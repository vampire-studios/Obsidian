package io.github.vampirestudios.obsidian.api.nexo;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class Effect {
	private final String name;
	private final Consumer<LivingEntity> applyEffect;
	private final Consumer<LivingEntity> revertEffect;
	private final int duration;
	private int ticksRemaining;

	public Effect(String name, Consumer<LivingEntity> applyEffect, Consumer<LivingEntity> revertEffect, int duration) {
		this.name = name;
		this.applyEffect = applyEffect;
		this.revertEffect = revertEffect;
		this.duration = duration;
		this.ticksRemaining = duration * 20; // Convert seconds to ticks
	}

	public void apply(LivingEntity entity) {
		applyEffect.accept(entity);
	}

	public void tick(LivingEntity entity) {
		if (ticksRemaining > 0) {
			ticksRemaining--;
			if (ticksRemaining == 0) {
				revertEffect.accept(entity);
			}
		}
	}

	public String getName() {
		return name;
	}

	public boolean isExpired() {
		return ticksRemaining <= 0;
	}
}
