package io.github.vampirestudios.obsidian.api.obsidian.potion;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class EffectInstance {

    public Identifier name;
    public int duration;
    public int amplifier;
    public String effect_type;
    public int color;

    public StatusEffectCategory getEffectType() {
        return Arrays.stream(StatusEffectCategory.values()).filter(e ->
                e.name().equalsIgnoreCase(effect_type)).findAny().orElse(null);
    }

}