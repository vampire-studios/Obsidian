package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class PotionSkill extends Skill {
	private final MobEffect effectType;
	private final int duration;
	private final int amplifier;
	private final boolean showParticles;

	public PotionSkill(String id, SkillTarget<?> target, SkillTrigger trigger, MobEffect effectType, int duration, int amplifier, boolean showParticles) {
		super(id, target, trigger);
		this.effectType = effectType;
		this.duration = duration;
		this.amplifier = amplifier;
		this.showParticles = showParticles;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		if (target != null) {
			MobEffectInstance effectInstance = new MobEffectInstance(Holder.direct(effectType), duration, amplifier, false, showParticles);
			target.addEffect(effectInstance);
			System.out.println("Applied " + effectType.getDescriptionId() + " to " + target.getName().getString() +
					" for " + duration + " ticks at amplifier level " + amplifier);
		}
	}
}
