package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(CubeDefinition.class)
public interface ModelCuboidDataAccessor {
	@Invoker("<init>")
	static CubeDefinition createModelCuboidData(
		@Nullable String string,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l,
		float m,
		CubeDeformation dilation,
		boolean bl,
		float n,
		float o,
		Set<Direction> set
	) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	String getComment();

	@Accessor
	Vector3f getOrigin();

	@Accessor
	Vector3f getDimensions();

	@Accessor
	CubeDeformation getGrow();

	@Accessor
	boolean isMirror();

	@Accessor
	UVPair getTexCoord();

	@Accessor
	UVPair getTexScale();

	@Accessor
	Set<Direction> getVisibleFaces();
}
