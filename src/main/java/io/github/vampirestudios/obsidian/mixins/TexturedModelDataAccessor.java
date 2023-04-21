package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerDefinition.class)
public interface TexturedModelDataAccessor {
	@Invoker("<init>")
	static LayerDefinition create(MeshDefinition modelData, MaterialDefinition textureDimensions) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	MeshDefinition getData();

	@Accessor
	MaterialDefinition getDimensions();
}
