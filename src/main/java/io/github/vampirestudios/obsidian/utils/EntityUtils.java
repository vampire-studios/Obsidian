package io.github.vampirestudios.obsidian.utils;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityUtils {

    public static AttributeSupplier.Builder createGenericEntityAttributes(double maxHealth, double movementSpeed) {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                .add(Attributes.MAX_HEALTH, maxHealth);
    }

}
