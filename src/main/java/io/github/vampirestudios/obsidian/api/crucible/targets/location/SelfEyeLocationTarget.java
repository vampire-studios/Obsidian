package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class SelfEyeLocationTarget extends LocationTarget {
	public SelfEyeLocationTarget() {
		super(List.of("SelfEyeLocation", "eyeDirection", "casterEyeLocation", "bossEyeLocation", "mobEyeLocation"));
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		return Collections.singletonList(caster.getEyePosition());
	}
}