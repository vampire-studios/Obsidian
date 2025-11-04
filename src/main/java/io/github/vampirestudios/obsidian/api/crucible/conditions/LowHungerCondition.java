/*
package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class LowHungerCondition extends Condition {
    private final int minHunger;

    public LowHungerCondition(int minHunger) {
        super(List.of("lowhunger", "hunger", "hungerbelow"));
        this.minHunger = minHunger;
    }

    @Override
    public boolean check(LivingEntity entity) {
        if(entity instanceof Player player)
            return player.getFoodData().getFoodLevel() < minHunger; // Assuming method to get hunger level
        return false;
    }
}
*/
