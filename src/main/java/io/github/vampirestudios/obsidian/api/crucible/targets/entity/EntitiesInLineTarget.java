package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EntitiesInLineTarget extends EntityTarget<LivingEntity> {
    private final double length;
    private final double width;

    public EntitiesInLineTarget(double length, double width) {
        super(List.of("EntitiesInLine", "line"));
        this.length = length;
        this.width = width;
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster) {
        List<LivingEntity> targets = new ArrayList<>();
        Vec3 start = caster.position();
        Vec3 end = start.add(caster.getLookAngle().scale(length));
        AABB lineBox = new AABB(start, end).inflate(width);
        for (LivingEntity entity : caster.level().getEntitiesOfClass(LivingEntity.class, lineBox)) {
            targets.add(entity);
        }
        return targets;
    }
}
