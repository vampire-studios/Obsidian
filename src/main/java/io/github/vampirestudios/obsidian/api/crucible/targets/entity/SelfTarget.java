package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;

public class SelfTarget extends EntityTarget<LivingEntity> {
    public SelfTarget() {
        super(List.of("Self", "self", "caster", "boss", "mob"));
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        return Collections.singletonList(caster);
    }
}