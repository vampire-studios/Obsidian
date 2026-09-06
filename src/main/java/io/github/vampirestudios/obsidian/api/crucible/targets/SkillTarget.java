package io.github.vampirestudios.obsidian.api.crucible.targets;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class SkillTarget<T> {
	public List<String> aliases;

	public SkillTarget(List<String> aliases) {
		this.aliases = aliases;
	}

	public abstract List<T> getTargets(LivingEntity caster);
}