package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class OnFireCondition extends Condition {

    public OnFireCondition() {
        super(List.of("onFire", "burning", "isBurning"));
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        return target.isOnFire();
    }

    @Override
    public boolean applyToCaster() {
        return false;
    }
}
