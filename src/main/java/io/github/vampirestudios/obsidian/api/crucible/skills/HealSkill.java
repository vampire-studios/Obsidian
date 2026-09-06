package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

public class HealSkill extends Skill {
	private final float healAmount;

	public HealSkill(String id, SkillTarget<?> target, SkillTrigger trigger, float healAmount) {
		super(id, target, trigger);
		this.healAmount = healAmount;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		if (target != null) {
			target.heal(healAmount);
			System.out.println("Healed " + target.getName().getString() + " for " + healAmount + " health.");
		}
	}
}
