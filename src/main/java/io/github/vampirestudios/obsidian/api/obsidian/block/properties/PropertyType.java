package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function3;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class PropertyType {
	public static Property<?> deserialize(String name, JsonObject data) {
		String key = JsonHelper.getString(data, "type");
		PropertyType prop = Obsidian.PROPERTY_TYPES.get(new Identifier(key));
		if (prop == null)
			throw new IllegalStateException("Property type not found " + key);
		return prop.read(name, data);
	}

	public static JsonObject serialize(Property<?> property) {
		for (Map.Entry<RegistryKey<PropertyType>, PropertyType> entry : Obsidian.PROPERTY_TYPES.getEntries()) {
			String key = entry.getKey().getValue().toString();
			PropertyType prop = entry.getValue();
			if (prop.handles(property)) {
				JsonObject data = new JsonObject();
				prop.write(data, property);
				data.addProperty("type", key);
				return data;
			}
		}
		throw new IllegalStateException("No serializer can handle the given property " + property);
	}

	public abstract boolean handles(Property<?> property);

	public abstract Property<?> read(String name, JsonObject data);

	public abstract void write(JsonObject data, Property<?> property);

	public String toString() {
		return "PropertyType{" + Obsidian.PROPERTY_TYPES.getId(this) + "}";
	}

	public static class BoolType extends PropertyType {
		@Override
		public boolean handles(Property<?> property) {
			return property instanceof BooleanProperty;
		}

		@Override
		public Property<?> read(String name, JsonObject data) {
			return BooleanProperty.of(name);
		}

		@Override
		public void write(JsonObject data, Property<?> property) {
			// Nothing to do
		}
	}

	public static class RangeType<T extends Comparable<T>, P extends Property<T>> extends PropertyType {
		private final Class<P> cls;
		private final Function3<String, T, T, P> factory;
		private final Function<JsonElement, T> parseBound;

		public RangeType(Class<P> cls, Function3<String, T, T, P> factory, Function<JsonElement, T> parseBound) {
			this.cls = cls;
			this.factory = factory;
			this.parseBound = parseBound;
		}

		@Override
		public boolean handles(Property<?> property) {
			return cls.isInstance(property);
		}

		@Override
		public Property<?> read(String name, JsonObject data) {
			if (!data.has("min"))
				throw new IllegalStateException("Requires a value 'min' of the right type.");
			if (!data.has("max"))
				throw new IllegalStateException("Requires a value 'max' of the right type.");
			T min = parseBound.apply(data.get("min"));
			T max = parseBound.apply(data.get("max"));
			return factory.apply(name, min, max);
		}

		@Override
		public void write(JsonObject data, Property<?> property) {
			property.getValues().stream().min(Comparable::compareTo)
					.ifPresent(v -> data.addProperty("min", v.toString()));
			property.getValues().stream().max(Comparable::compareTo)
					.ifPresent(v -> data.addProperty("max", v.toString()));
		}
	}

	public static class StringType extends PropertyType {
		@Override
		public boolean handles(Property<?> property) {
			return property instanceof CustomProperty;
		}

		@Override
		public Property<?> read(String name, JsonObject data) {
			List<String> valid_values = Lists.newArrayList();
			if (data.has("values")) {
				JsonArray values = data.get("values").getAsJsonArray();
				for (JsonElement e : values) {
					String val = e.getAsJsonPrimitive().getAsString();
					valid_values.add(val);
				}
				return CustomProperty.create(name, valid_values);
			}
			return CustomProperty.create(name);
		}

		@Override
		public void write(JsonObject data, Property<?> property) {
			Collection<String> valid_values = ((CustomProperty) property).getValues();
			JsonArray list = new JsonArray();
			valid_values.forEach(list::add);
			data.add("values", list);
		}
	}

	public static class DirectionType extends PropertyType {
		@Override
		public boolean handles(Property<?> property) {
			return property instanceof BooleanProperty;
		}

		@Override
		public Property<?> read(String name, JsonObject data) {
			List<Direction> valid_values = Lists.newArrayList();
			if (data.has("values")) {
				JsonArray values = data.get("values").getAsJsonArray();
				for (JsonElement e : values) {
					String val = e.getAsJsonPrimitive().getAsString();
					valid_values.add(Direction.byName(val));
				}
				return DirectionProperty.of(name, valid_values);
			}
			return DirectionProperty.of(name);
		}

		@Override
		public void write(JsonObject data, Property<?> property) {
			Collection<Direction> valid_values = ((DirectionProperty) property).getValues();
			Direction[] values = Direction.values();
			if (values.length > valid_values.size()) {
				JsonArray list = new JsonArray();
				valid_values.stream().map(Direction::asString).forEach(list::add);
				data.add("values", list);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static class EnumType extends PropertyType {
		@Override
		public boolean handles(Property<?> property) {
			return property instanceof EnumProperty;
		}

		@Override
		public Property<?> read(String name, JsonObject data) {
			String className = JsonHelper.getString(data, "class");

			Class cls;
			try {
				cls = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Error getting class " + className, e);
			}

			if (!cls.isEnum()) {
				throw new IllegalStateException("Not an enum type " + className);
			}

			if (!StringIdentifiable.class.isAssignableFrom(cls)) {
				throw new IllegalStateException("Enum type " + className + " not IStringSerializable");
			}

			List valid_values = Lists.newArrayList();
			if (data.has("values")) {
				Object[] enum_values = cls.getEnumConstants();
				StringIdentifiable[] serializables = Arrays.stream(enum_values).map(StringIdentifiable.class::cast).toArray(StringIdentifiable[]::new);

				JsonArray values = data.get("values").getAsJsonArray();
				for (JsonElement e : values) {
					String val = e.getAsJsonPrimitive().getAsString();
					for (StringIdentifiable s : serializables) {
						if (s.asString().equals(val)) {
							valid_values.add(s);
						}
					}
				}
			}

			return EnumProperty.of(name, cls, valid_values);
		}

		@Override
		public void write(JsonObject data, Property<?> property) {
			Collection<?> valid_values = property.getValues();
			Class<?> cls = valid_values.stream().findFirst().get().getClass();
			Object[] enum_values = cls.getEnumConstants();
			if (enum_values.length > valid_values.size()) {
				JsonArray list = new JsonArray();
				valid_values.stream().map(s -> ((StringIdentifiable) s).asString()).forEach(list::add);
				data.add("values", list);
			}
			data.addProperty("class", cls.getName());
		}
	}
}