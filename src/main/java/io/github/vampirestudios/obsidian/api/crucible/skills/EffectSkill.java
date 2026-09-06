package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

public class EffectSkill extends Skill {

	private final Effect effect;

	public EffectSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, Effect effect) {
		super(skillId, target, trigger);
		this.effect = effect;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		effect.apply(target);
	}
}
