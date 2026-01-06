package io.github.vampirestudios.obsidian.api.obsidian.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Arrays;

public class EffectInstance {

    public Identifier name;
    public int duration;
    public int amplifier;
    public String effect_type;
    public int color;

    public MobEffectCategory getEffectType() {
        return Arrays.stream(MobEffectCategory.values()).filter(e ->
                e.name().equalsIgnoreCase(effect_type)).findAny().orElse(null);
    }

}