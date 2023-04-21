package io.github.vampirestudios.obsidian.api.obsidian.potion;

import java.util.Arrays;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectInstance {

    public ResourceLocation name;
    public int duration;
    public int amplifier;
    public String effect_type;
    public int color;

    public MobEffectCategory getEffectType() {
        return Arrays.stream(MobEffectCategory.values()).filter(e ->
                e.name().equalsIgnoreCase(effect_type)).findAny().orElse(null);
    }

}