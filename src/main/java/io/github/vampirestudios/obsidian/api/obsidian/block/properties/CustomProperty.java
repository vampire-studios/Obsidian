package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import com.google.common.collect.ImmutableList;
import net.minecraft.state.property.Property;

import java.util.Collection;
import java.util.Optional;

public class CustomProperty extends Property<String> {
	private final ImmutableList<String> allowedValues;

	protected CustomProperty(String name, ImmutableList<String> values) {
		super(name, String.class);
		allowedValues = values;
	}

	public static CustomProperty create(String name, String... values) {
		return new CustomProperty(name, ImmutableList.copyOf(values));
	}

	public static CustomProperty create(String name, Collection<String> values) {
		return new CustomProperty(name, ImmutableList.copyOf(values));
	}

	@Override
	public Collection<String> getValues() {
		return allowedValues;
	}

	@Override
	public String name(String value) {
		return value;
	}

	@Override
	public Optional<String> parse(String value) {
		return allowedValues.stream().filter(s -> s.equals(value)).findFirst();
	}
}