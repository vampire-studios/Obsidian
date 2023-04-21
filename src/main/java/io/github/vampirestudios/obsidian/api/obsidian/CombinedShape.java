package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CombinedShape implements IShapeProvider {
	public static final BiMap<String, BooleanOp> BOOLEAN_OPERATORS = ImmutableBiMap.<String, BooleanOp>builder()
			.put("false", BooleanOp.FALSE)
			.put("not_or", BooleanOp.NOT_OR)
			.put("only_second", BooleanOp.ONLY_SECOND)
			.put("not_first", BooleanOp.NOT_FIRST)
			.put("only_first", BooleanOp.ONLY_FIRST)
			.put("not_second", BooleanOp.NOT_SECOND)
			.put("not_same", BooleanOp.NOT_SAME)
			.put("not_and", BooleanOp.NOT_AND)
			.put("and", BooleanOp.AND)
			.put("same", BooleanOp.SAME)
			.put("second", BooleanOp.SECOND)
			.put("causes", BooleanOp.CAUSES)
			.put("first", BooleanOp.FIRST)
			.put("caused_by", BooleanOp.CAUSED_BY)
			.put("or", BooleanOp.OR)
			.put("true", BooleanOp.TRUE)
			.build();
	public static final Codec<BooleanOp> BOOLEAN_OP_CODEC = CodecExtras.mappingCodec(Codec.STRING, BOOLEAN_OPERATORS::get, BOOLEAN_OPERATORS.inverse()::get);
	public static final Codec<CombinedShape> LIST_CODEC = CodecExtras.lazy(DynamicShape::shapeCodec).listOf().flatComapMap(
			list -> new CombinedShape(BooleanOp.OR, list),
			shape -> shape.operator == BooleanOp.OR
					? DataResult.success(shape.boxes)
					: DataResult.error(() -> "Cannot use CombinedShape.LIST_CODEC to encode a CombinedShape whose boolean function is not OR")
	);
	public static final Codec<CombinedShape> OBJECT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			BOOLEAN_OP_CODEC.fieldOf("op").forGetter(shape -> shape.operator),
			CodecExtras.lazy(DynamicShape::shapeCodec).listOf().fieldOf("shapes").forGetter(shape -> shape.boxes)
	).apply(instance, CombinedShape::new));
	public static final Codec<CombinedShape> CODEC = CodecExtras.makeChoiceCodec(LIST_CODEC, OBJECT_CODEC);

	public final BooleanOp operator;
	public final List<IShapeProvider> boxes = Lists.newArrayList();

	public CombinedShape(BooleanOp operator, Collection<IShapeProvider> boxes) {
		this.operator = operator;
		this.boxes.addAll(boxes);
	}

	@Override
	public Optional<VoxelShape> getShape(BlockState state, Direction facing) {
		return boxes.stream()
				.map(shape -> shape.getShape(state, facing))
				.reduce(Optional.empty(), (a, b) -> a.map(aa -> b.map(bb -> Shapes.joinUnoptimized(aa, bb, operator)).or(() -> a)).orElse(b))
				.map(VoxelShape::optimize);
	}

	@Override
	public IShapeProvider bake(Function<String, Property<?>> propertyLookup) {
		return new CombinedShape(operator, boxes.stream().map(s -> s.bake(propertyLookup)).collect(Collectors.toList()));
	}
}