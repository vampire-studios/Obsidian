package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.TextureDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureDimensions.class)
public interface TextureDimensionsAccessor {
	@Accessor
	int getWidth();

	@Accessor
	int getHeight();
}
