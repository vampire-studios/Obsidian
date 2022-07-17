package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombinedShape implements IShapeProvider {
	public static final BiMap<String, BooleanBiFunction> BOOLEAN_OPERATORS = ImmutableBiMap.<String, BooleanBiFunction>builder()
			.put("false", BooleanBiFunction.FALSE)
			.put("not_or", BooleanBiFunction.NOT_OR)
			.put("only_second", BooleanBiFunction.ONLY_SECOND)
			.put("not_first", BooleanBiFunction.NOT_FIRST)
			.put("only_first", BooleanBiFunction.ONLY_FIRST)
			.put("not_second", BooleanBiFunction.NOT_SECOND)
			.put("not_same", BooleanBiFunction.NOT_SAME)
			.put("not_and", BooleanBiFunction.NOT_AND)
			.put("and", BooleanBiFunction.AND)
			.put("same", BooleanBiFunction.SAME)
			.put("second", BooleanBiFunction.SECOND)
			.put("causes", BooleanBiFunction.CAUSES)
			.put("first", BooleanBiFunction.FIRST)
			.put("caused_by", BooleanBiFunction.CAUSED_BY)
			.put("or", BooleanBiFunction.OR)
			.put("true", BooleanBiFunction.TRUE)
			.build();
	public static final Codec<BooleanBiFunction> BOOLEAN_OP_CODEC = CodecExtras.mappingCodec(Codec.STRING, BOOLEAN_OPERATORS::get, BOOLEAN_OPERATORS.inverse()::get);
	public static final Codec<CombinedShape> LIST_CODEC = CodecExtras.lazy(DynamicShape::shapeCodec).listOf().flatComapMap(
			list -> new CombinedShape(BooleanBiFunction.OR, list),
			shape -> shape.operator == BooleanBiFunction.OR
					? DataResult.success(shape.boxes)
					: DataResult.error("Cannot use CombinedShape.LIST_CODEC to encode a CombinedShape whose boolean function is not OR")
	);
	public static final Codec<CombinedShape> OBJECT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			BOOLEAN_OP_CODEC.fieldOf("op").forGetter(shape -> shape.operator),
			CodecExtras.lazy(DynamicShape::shapeCodec).listOf().fieldOf("shapes").forGetter(shape -> shape.boxes)
	).apply(instance, CombinedShape::new));
	public static final Codec<CombinedShape> CODEC = CodecExtras.makeChoiceCodec(LIST_CODEC, OBJECT_CODEC);

	public final BooleanBiFunction operator;
	public final List<IShapeProvider> boxes = Lists.newArrayList();

	public CombinedShape(BooleanBiFunction operator, Collection<IShapeProvider> boxes) {
		this.operator = operator;
		this.boxes.addAll(boxes);
	}

	@Override
	public Optional<VoxelShape> getShape(BlockState state, Direction facing) {
		return boxes.stream()
				.map(shape -> shape.getShape(state, facing))
				.reduce(Optional.empty(), (a, b) -> a.map(aa -> b.map(bb -> VoxelShapes.combine(aa, bb, operator)).or(() -> a)).orElse(b))
				.map(VoxelShape::simplify);
	}

	@Override
	public IShapeProvider bake(Function<String, Property<?>> propertyLookup) {
		return new CombinedShape(operator, boxes.stream().map(s -> s.bake(propertyLookup)).collect(Collectors.toList()));
	}
}