package io.github.vampirestudios.obsidian.api.nexo;

import net.minecraft.world.entity.LivingEntity;

public record EffectArgs(LivingEntity entity, int duration, double gravity, float tickTime) {
}
