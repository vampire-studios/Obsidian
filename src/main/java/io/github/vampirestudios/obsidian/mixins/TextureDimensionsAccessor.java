package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.geom.builders.MaterialDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MaterialDefinition.class)
public interface TextureDimensionsAccessor {
	@Accessor
	int getXTexSize();

	@Accessor
	int getYTexSize();
}
