package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.registry.Registry;

public class PropertyTypes {
	public static final PropertyType BOOLEAN_PROPERTY = register("boolean", new PropertyType.BoolType());
	public static final PropertyType INTEGER_PROPERTY = register("int", new PropertyType.RangeType<>(IntProperty.class, IntProperty::of, js -> js.getAsJsonPrimitive().getAsInt()));
	public static final PropertyType FLOAT_PROPERTY = register("float", new PropertyType.RangeType<>(FloatProperty.class, FloatProperty::of, js -> js.getAsJsonPrimitive().getAsFloat()));
	public static final PropertyType DOUBLE_PROPERTY = register("double", new PropertyType.RangeType<>(DoubleProperty.class, DoubleProperty::of, js -> js.getAsJsonPrimitive().getAsDouble()));
	public static final PropertyType LONG_PROPERTY = register("long", new PropertyType.RangeType<>(LongProperty.class, LongProperty::of, js -> js.getAsJsonPrimitive().getAsLong()));
	public static final PropertyType STRING_PROPERTY = register("string", new PropertyType.StringType());
	public static final PropertyType DIRECTION_PROPERTY = register("direction", new PropertyType.DirectionType());
	public static final PropertyType ENUM_PROPERTY = register("enum", new PropertyType.EnumType());

	public static PropertyType register(String name, PropertyType propertyType) {
		return Registry.register(Obsidian.PROPERTY_TYPES, name, propertyType);
	}

	public static void init() {}
}