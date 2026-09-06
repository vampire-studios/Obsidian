package io.github.vampirestudios.obsidian.api.crucible.aura;

import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Aura {
	private String auraId;
	private final double radius;
	private final int duration; // Duration in ticks
	private final int tickInterval; // Interval in ticks
	private final List<Condition> conditions; // Conditions to apply the aura
	private final List<Effect> effects; // Effects to apply
	private final boolean affectsCaster; // Whether the caster is affected by the aura
	private LivingEntity caster;
	private int ticksRemaining;

	public Aura(LivingEntity caster, String auraId, double radius, int duration, int tickInterval, List<Condition> conditions, List<Effect> effects, boolean affectsCaster) {
		this.caster = caster;
		this.auraId = auraId;
		this.radius = radius;
		this.duration = duration;
		this.tickInterval = tickInterval;
		this.conditions = conditions;
		this.effects = effects;
		this.affectsCaster = affectsCaster;
		this.ticksRemaining = duration;
	}

	public void tick() {
		// A zero or negative interval would either divide by zero or fire every tick by accident.
		if (tickInterval > 0 && ticksRemaining % tickInterval == 0) {
			applyEffects();
		}
		ticksRemaining--;
	}

	/** An aura dies with whatever it is attached to, not just when its timer runs out. */
	public boolean isOrphaned() {
		return caster == null || !caster.isAlive() || caster.isRemoved();
	}

	private void applyEffects() {
		List<LivingEntity> entitiesInRadius = getEntitiesInRadius(caster, radius);
		for (LivingEntity entity : entitiesInRadius) {
			if (!affectsCaster && entity.equals(caster)) continue;

			boolean passesConditions = conditions.stream().allMatch(condition -> condition.evaluate(caster, entity));
			if (passesConditions) {
				effects.forEach(effect -> effect.apply(entity));
			}
		}
	}

	public boolean isExpired() {
		return ticksRemaining <= 0;
	}

	private List<LivingEntity> getEntitiesInRadius(LivingEntity caster, double radius) {
		return caster.level().getEntitiesOfClass(LivingEntity.class,
				new AABB(caster.position(), caster.position()).inflate(radius),
				LivingEntity::isAlive);
	}

	public LivingEntity getCaster() {
		return caster;
	}

	public String getId() {
		return auraId;
	}
}
