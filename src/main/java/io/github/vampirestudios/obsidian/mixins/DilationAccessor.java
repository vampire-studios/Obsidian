package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CubeDeformation.class)
public interface DilationAccessor {
    @Accessor
    float getGrowX();

    @Accessor
    float getGrowY();

    @Accessor
    float getGrowZ();
}
