package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.aura.Aura;
import io.github.vampirestudios.obsidian.api.crucible.aura.AuraManager;
import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class AuraSkill extends Skill {
	private final String auraId;
	private final double radius;
	private final int duration;
	private final int tickInterval;
	private final List<Condition> conditions;
	private final List<Effect> effects;
	private final boolean affectsCaster;

	public AuraSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger,
	                 String auraId, double radius, int duration, int tickInterval,
	                 List<Condition> conditions, List<Effect> effects, boolean affectsCaster) {
		super(skillId, target, trigger);
		this.auraId = auraId;
		this.radius = radius;
		this.duration = duration;
		this.tickInterval = tickInterval;
		this.conditions = conditions;
		this.effects = effects;
		this.affectsCaster = affectsCaster;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		Aura aura = new Aura(target, auraId, radius, duration, tickInterval, conditions, effects, affectsCaster);
		AuraManager.addAura(aura);
	}
}
