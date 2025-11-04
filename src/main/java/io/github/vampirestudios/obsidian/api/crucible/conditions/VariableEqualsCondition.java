package io.github.vampirestudios.obsidian.api.crucible.conditions;

import io.github.vampirestudios.obsidian.api.crucible.EntityVariableManager;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class VariableEqualsCondition extends Condition {
    private final String variableName;
    private final Object expectedValue;

    public VariableEqualsCondition(String variableName, Object expectedValue) {
        super(List.of("variableEquals"));
        this.variableName = variableName;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        LivingEntity entity = applyToCaster() ? caster : target;
        Object variableValue = EntityVariableManager.getEntityVariable(entity, variableName);
        return expectedValue.equals(variableValue);
    }

    @Override
    public boolean applyToCaster() {
        return variableName.startsWith("caster");
    }
}
