package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.util.math.Vector2f;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelCuboidData.class)
public interface ModelCuboidDataAccessor {
	@Invoker("<init>")
	static ModelCuboidData create(@Nullable String string, float f, float g, float h, float i, float j, float k, float l, float m, Dilation dilation, boolean bl, float n, float o) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	String getName();

	@Accessor
	Vector3f getOffset();

	@Accessor
	Vector3f getDimensions();

	@Accessor
	Dilation getExtraSize();

	@Accessor
	boolean isMirror();

	@Accessor
	Vector2f getTextureUV();

	@Accessor
	Vector2f getTextureScale();
}
