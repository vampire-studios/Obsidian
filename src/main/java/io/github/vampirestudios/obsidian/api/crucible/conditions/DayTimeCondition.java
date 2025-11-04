package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class DayTimeCondition extends Condition {

    public DayTimeCondition() {
        super(List.of("dayTime", "isDay", "day"));
    }

    @Override
    public boolean evaluate(LivingEntity caster, LivingEntity target) {
        Level level = caster.level();
        return level.isBrightOutside();
    }

    @Override
    public boolean applyToCaster() {
        return true;
    }
}
