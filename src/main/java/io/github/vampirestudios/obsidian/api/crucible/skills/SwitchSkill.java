package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class SwitchSkill extends Skill {
    private final Condition switchCondition;
    private final Map<String, Skill> cases;
    private final Skill defaultCase;

    public SwitchSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, Condition switchCondition, Map<String, Skill> cases, Skill defaultCase) {
        super(skillId, target, trigger);
        this.switchCondition = switchCondition;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        for (Map.Entry<String, Skill> caseEntry : cases.entrySet()) {
            String caseName = caseEntry.getKey();
            Skill caseSkill = caseEntry.getValue();

            if (switchCondition.evaluate(caster, target)) {
                caseSkill.runSkill(caster, target);
                return;
            }
        }

        // If no cases match, use the default case if available
        if (defaultCase != null) {
            defaultCase.runSkill(caster, target);
        }
    }
}
