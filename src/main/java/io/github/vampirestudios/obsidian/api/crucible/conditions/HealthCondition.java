package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;
import java.util.List;

public class HealthCondition extends Condition {
    private final ComparisonOperator operator;
    private final double healthThreshold;
    private final boolean includeAbsorption;
    private final boolean applyToCaster;

    public HealthCondition(ComparisonOperator operator, double healthThreshold, boolean includeAbsorption, boolean applyToCaster) {
        super(List.of("health", "hp", "healthCondition"));
        this.operator = operator;
        this.healthThreshold = healthThreshold;
        this.includeAbsorption = includeAbsorption;
        this.applyToCaster = applyToCaster;
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        LivingEntity entityToCheck = applyToCaster ? caster : target;
        double currentHealth = includeAbsorption ? entityToCheck.getHealth() + entityToCheck.getAbsorptionAmount() : entityToCheck.getHealth();

        return switch (operator) {
            case LESS_THAN -> currentHealth < healthThreshold;
            case LESS_THAN_OR_EQUAL -> currentHealth <= healthThreshold;
            case EQUAL -> currentHealth == healthThreshold;
            case GREATER_THAN_OR_EQUAL -> currentHealth >= healthThreshold;
            case GREATER_THAN -> currentHealth > healthThreshold;
        };
    }

    @Override
    public boolean applyToCaster() {
        return applyToCaster;
    }

    public enum ComparisonOperator {
        LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL, GREATER_THAN_OR_EQUAL, GREATER_THAN
    }
}
