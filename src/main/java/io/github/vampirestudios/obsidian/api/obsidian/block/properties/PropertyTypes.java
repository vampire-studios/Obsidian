package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PropertyTypes {
    public static final PropertyType BOOLEAN_PROPERTY = register("boolean", new PropertyType.BoolType());
    public static final PropertyType INTEGER_PROPERTY = register("int", new PropertyType.RangeType<>(IntegerProperty.class, IntegerProperty::create, js -> js.getAsJsonPrimitive().getAsInt()));
    public static final PropertyType STRING_PROPERTY = register("string", new PropertyType.StringType());
    public static final PropertyType DIRECTION_PROPERTY = register("direction", new PropertyType.DirectionType());
    public static final PropertyType ENUM_PROPERTY = register("enum", new PropertyType.EnumType());

    public static PropertyType register(String name, PropertyType propertyType) {
        return Registry.register(Registries.PROPERTY_TYPES, name, propertyType);
    }

    public static void init() {}
}