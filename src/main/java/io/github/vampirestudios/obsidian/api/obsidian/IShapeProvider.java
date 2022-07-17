package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.Optional;
import java.util.function.Function;

public interface IShapeProvider {
	Optional<VoxelShape> getShape(BlockState state, Direction facing);
	IShapeProvider bake(Function<String, Property<?>> propertyLookup);
}