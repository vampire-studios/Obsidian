package io.github.vampirestudios.obsidian.api.crucible.aura;

import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import net.minecraft.world.entity.LivingEntity;

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
        if (ticksRemaining % tickInterval == 0) {
            applyEffects();
        }
        ticksRemaining--;
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
        // Implement your method for finding all entities within the given radius around the caster.
        return List.of();
    }

    public LivingEntity getCaster() {
        return caster;
    }

    public String getId() {
        return auraId;
    }
}
