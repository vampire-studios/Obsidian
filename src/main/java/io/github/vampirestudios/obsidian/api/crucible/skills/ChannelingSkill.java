package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ChannelingSkill extends Skill {
    private final int duration; // In ticks
    private final List<Effect> continuousEffects;

    public ChannelingSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, int duration, List<Effect> continuousEffects) {
        super(skillId, target, trigger);
        this.duration = duration;
        this.continuousEffects = continuousEffects;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        for (int i = 0; i < duration; i++) {
            /*caster.level().getScheduler().schedule(() -> {
                *//*if (!caster.isMoving() && !caster.()) {
                    for (Effect effect : continuousEffects) {
                        effect.apply(target);
                    }
                } else {
                    // Cancel the skill if interrupted
                    return;
                }*//*
            }, i * 20); // Apply every second*/
        }
    }
}
