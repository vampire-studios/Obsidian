package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public record SkillContext(LivingEntity caster, LivingEntity target, BlockPos position) {
	public boolean hasTarget() {
		return target != null;
	}

	public boolean hasPosition() {
		return position != null;
	}
}