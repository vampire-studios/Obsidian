package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class Effect {
    private ResourceLocation id;

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract void apply(LivingEntity target);
}
