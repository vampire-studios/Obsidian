package io.github.vampirestudios.obsidian.api.obsidian.potion;

import io.github.vampirestudios.obsidian.minecraft.obsidian.StatusEffectImpl;
import net.minecraft.resources.ResourceLocation;

public class Potion {

    public ResourceLocation name;
    public EffectInstance[] effects;

    public EffectInstance getEffects() {
        for (EffectInstance effectInstance : effects) {
            return effectInstance;
        }
        return null;
    }

    public StatusEffectImpl getEffectType() {
        for (EffectInstance instance : effects) {
            return new StatusEffectImpl(instance.getEffectType(), instance.color);
        }
        return null;
    }

}