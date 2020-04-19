package io.github.vampirestudios.obsidian.api.potion;

import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class EffectInstance {

    public Identifier name;
    public int duration;
    public int amplifier;
    public String effect_type;
    public int color;

    public StatusEffectType getEffectType() {
        return Arrays.stream(StatusEffectType.values()).filter(e ->
                e.name().equalsIgnoreCase(effect_type)).findAny().orElse(null);
    }

}