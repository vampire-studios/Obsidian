package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DynamicShape {
	private static final Codec<IShapeProvider> SHAPE_CODEC = CodecExtras.makeChoiceCodec(
			CodecExtras.toSubclass(ConditionalShape.CODEC, ConditionalShape.class),
			CodecExtras.toSubclass(CombinedShape.CODEC, CombinedShape.class),
			CodecExtras.toSubclass(BasicShape.CODEC, BasicShape.class)
	);

	public static final Codec<DynamicShape> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			SHAPE_CODEC.fieldOf("shape").forGetter(shape -> shape.shape),
			CodecExtras.PROPERTY_CODEC.optionalFieldOf("shape_rotation").forGetter(shape -> Optional.ofNullable(shape.facing))
	).apply(instance, (shape, facing) -> new DynamicShape(shape, (Property<Direction>) facing.orElse(null))));

	private static final DynamicShape EMPTY = new DynamicShape(new CombinedShape(BooleanBiFunction.OR, Collections.emptyList()), null);

	public static DynamicShape empty() {
		return EMPTY;
	}

	public static Codec<IShapeProvider> shapeCodec() {
		return SHAPE_CODEC;
	}

	public static DynamicShape parseShape(JsonElement element, @Nullable Property<Direction> facingProperty, Map<String, Property<?>> propertiesByName) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
			String name = element.getAsString();
			DynamicShape shape = Registries.DYNAMIC_SHAPES.get(new Identifier(name));
			if (shape == null)
				throw new IllegalStateException("No shape known with name " + name);
			return shape;
		} else {
			return fromJson(element, facingProperty, propertiesByName::get);
		}
	}

	private final Map<BlockState, VoxelShape> shapeCache = new IdentityHashMap<>();
	private final IShapeProvider shape;
	@Nullable
	private final Property<Direction> facing;

	public DynamicShape(IShapeProvider shape, @Nullable Property<Direction> facing) {
		this.shape = shape;
		this.facing = facing;
	}

	public VoxelShape getShape(BlockState blockstate) {
		return shapeCache.computeIfAbsent(blockstate, state -> {
			Direction d = facing != null ? state.get(facing) : Direction.NORTH;
			return shape.getShape(state, d).orElseGet(VoxelShapes::fullCube);
		});
	}

	public static DynamicShape fromJson(JsonElement data, @Nullable Property<Direction> facingProperty, Function<String, Property<?>> properties) {
		IShapeProvider shape = SHAPE_CODEC.decode(JsonOps.INSTANCE, data).getOrThrow(false, str -> {
		}).getFirst();
		shape = shape.bake(properties);
		return new DynamicShape(shape, facingProperty);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	public static VoxelShape cuboidWithRotation(Direction facing, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (facing) {
			case NORTH -> VoxelShapes.cuboid(x1, y1, z1, x2, y2, z2);
			case SOUTH -> VoxelShapes.cuboid(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
			case WEST -> VoxelShapes.cuboid(z1, y1, 1 - x2, z2, y2, 1 - x1);
			case EAST -> VoxelShapes.cuboid(1 - z2, y1, x1, 1 - z1, y2, x2);
			case UP -> VoxelShapes.cuboid(1 - y1, x1, z1, 1 - y2, x2, z2);
			case DOWN -> VoxelShapes.cuboid(y1, 1 - x1, z1, y2, 1 - x2, z2);
		};
	}
}