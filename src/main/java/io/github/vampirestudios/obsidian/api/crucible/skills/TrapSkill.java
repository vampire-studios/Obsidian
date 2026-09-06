package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class TrapSkill extends Skill {
	private final double activationRadius;
	private final List<Effect> trapEffects;
	private final int trapLifetime;

	public TrapSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, double activationRadius, List<Effect> trapEffects, int trapLifetime) {
		super(skillId, target, trigger);
		this.activationRadius = activationRadius;
		this.trapEffects = trapEffects;
		this.trapLifetime = trapLifetime;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		BlockPos trapPosition = caster.blockPosition();
//        caster.level().getScheduler().scheduleAtFixedRate(() -> {
//            if (target.distanceToSqr(trapPosition) < activationRadius * activationRadius) {
//                for (Effect effect : trapEffects) {
//                    effect.apply(target);
//                }
//                return; // Remove trap after activation
//            }
//        }, 0, 20, trapLifetime * 20);
	}
}
