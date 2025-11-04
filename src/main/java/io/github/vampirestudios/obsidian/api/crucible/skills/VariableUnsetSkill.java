package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.EntityVariableManager;
import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

public class VariableUnsetSkill extends Skill {
    private final String variableName;

    public VariableUnsetSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, String variableName) {
        super(skillId, target, trigger);
        this.variableName = variableName;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        EntityVariableManager.unsetEntityVariable(caster, variableName);
    }
}
