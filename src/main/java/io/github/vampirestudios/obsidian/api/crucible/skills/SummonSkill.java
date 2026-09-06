package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class SummonSkill extends Skill {
	private final EntityType<?> entityType;
	private final int summonDuration;

	public SummonSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, EntityType<?> entityType, int summonDuration) {
		super(skillId, target, trigger);
		this.entityType = entityType;
		this.summonDuration = summonDuration;
	}

	@Override
	public void applyEffect(LivingEntity caster) {
		LivingEntity summon = (LivingEntity) entityType.create(caster.level(), EntitySpawnReason.SPAWN_ITEM_USE);
		summon.setPos(caster.getX(), caster.getY(), caster.getZ());
		caster.level().addFreshEntity(summon);

		// Optionally set summon to despawn after a duration
//        caster.level().getScheduler().schedule(() -> summon.discard(), summonDuration * 20);
	}
}
