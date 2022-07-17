package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.TextureDimensions;
import net.minecraft.client.model.TexturedModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TexturedModelData.class)
public interface TexturedModelDataAccessor {
	@Invoker("<init>")
	static TexturedModelData create(ModelData modelData, TextureDimensions textureDimensions) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	ModelData getData();

	@Accessor
	TextureDimensions getDimensions();
}
