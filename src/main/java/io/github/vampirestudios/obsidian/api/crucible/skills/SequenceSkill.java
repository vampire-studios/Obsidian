package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runs a list of step-skills in order.
 * Conditions/TargetConditions/TriggerConditions live on this wrapper, not on each step.
 */
public final class SequenceSkill extends Skill {

    private final List<Skill> steps;

    public SequenceSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, List<Skill> steps) {
        super(skillId, target, trigger);
        this.steps = new ArrayList<>(Objects.requireNonNullElse(steps, List.of()));
    }

    public List<Skill> steps() {
        return steps;
    }

    @Override
    public void applyEffect(LivingEntity caster) {
        // If targetless, run steps as targetless
        for (Skill s : steps) {
            if (s == null) continue;
            s.applyEffect(caster);
        }
    }

    @Override
    public void applyEffect(LivingEntity caster, BlockPos pos) {
        for (Skill s : steps) {
            if (s == null) continue;
            s.applyEffect(caster, pos);
        }
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity targetEntity) {
        for (Skill s : steps) {
            if (s == null) continue;
            s.applyEffect(caster, targetEntity);
        }
    }

    @Override
    public boolean evaluateConditions(LivingEntity caster, LivingEntity targetEntity) {
        // Wrapper conditions first
        if (!super.evaluateConditions(caster, targetEntity)) return false;

        // Then allow step conditions (if you keep them on steps) — optional:
        for (Skill s : steps) {
            if (s == null) continue;

            List<Condition> conds = s.getConditions();
            List<Condition> tconds = s.getTargetConditions();
            if (conds != null || tconds != null) {
                // If step has no conditions set, these lists might be null
                if (conds != null) {
                    for (Condition c : conds) if (!c.evaluate(caster, caster)) return false;
                }
                if (tconds != null) {
                    for (Condition c : tconds) if (!c.evaluate(caster, targetEntity)) return false;
                }
            }
        }
        return true;
    }
}
