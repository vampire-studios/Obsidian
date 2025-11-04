package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TargetLocationTarget extends LocationTarget {
    public TargetLocationTarget() {
        super(List.of("TargetLocation", "targetLocation", "tl"));
    }

    @Override
    public List<Vec3> getTargets(LivingEntity caster) {
        return Collections.singletonList(Objects.requireNonNull(caster.getLastHurtMob()).position());
    }
}
