package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

public class SkillSkill extends Skill {

    private final Skill executingSkill;

    public SkillSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, Skill executingSkill) {
        super(skillId, target, trigger);
        this.executingSkill = executingSkill;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        executingSkill.runSkill(caster, target);
    }
}
