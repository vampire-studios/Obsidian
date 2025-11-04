package io.github.vampirestudios.obsidian.api.crucible.targets;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class EntityTarget<T extends LivingEntity> extends SkillTarget<T> {
	public EntityTarget(List<String> aliases) {
		super(aliases);
	}
}