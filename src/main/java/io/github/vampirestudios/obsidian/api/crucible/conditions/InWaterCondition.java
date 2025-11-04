package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class InWaterCondition extends Condition {

    public InWaterCondition() {
        super(List.of("inWater", "water", "isInWater"));
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        return target.isInWater();
    }

    @Override
    public boolean applyToCaster() {
        return false;
    }
}
