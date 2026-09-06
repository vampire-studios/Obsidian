package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class SelfLocationTarget extends LocationTarget {
	public SelfLocationTarget() {
		super(List.of("SelfLocation", "casterLocation", "bossLocation", "mobLocation"));
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		return Collections.singletonList(caster.position());
	}
}