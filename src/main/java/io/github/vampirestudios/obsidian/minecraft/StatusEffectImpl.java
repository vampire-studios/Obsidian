package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class StatusEffectImpl extends StatusEffect {

    public StatusEffectImpl(StatusEffectType type, int color) {
        super(type, color);
    }

}