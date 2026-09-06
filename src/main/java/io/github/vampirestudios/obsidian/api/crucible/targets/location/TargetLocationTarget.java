package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TargetLocationTarget extends LocationTarget {
	public TargetLocationTarget() {
		super(List.of("TargetLocation", "targetLocation", "tl"));
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		// No last target is normal — the caster may not have hit anything yet.
		LivingEntity target = caster.getLastHurtMob();
		return target == null ? List.of() : List.of(target.position());
	}
}
