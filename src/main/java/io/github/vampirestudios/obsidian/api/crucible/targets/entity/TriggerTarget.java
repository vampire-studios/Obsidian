package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;

public class TriggerTarget extends EntityTarget<LivingEntity> {
	public TriggerTarget() {
		super(List.of("Trigger", "trigger"));
	}

	@Override
	public List<LivingEntity> getTargets(LivingEntity caster) {
		return Collections.singletonList(caster);
	}
}
