package io.github.vampirestudios.obsidian.api.crucible.conditions;

import io.github.vampirestudios.obsidian.api.crucible.EntityVariableManager;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class VariableIsSetCondition extends Condition {
    private final String variableName;

    public VariableIsSetCondition(String variableName) {
        super(List.of("variableisset"));
        this.variableName = variableName;
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        LivingEntity entity = applyToCaster() ? caster : target;
        return EntityVariableManager.hasEntityVariable(entity, variableName);
    }

    @Override
    public boolean applyToCaster() {
        return variableName.startsWith("caster");
    }
}
