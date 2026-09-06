package io.github.vampirestudios.obsidian.registry.properties;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ListProperty extends Property<String> {
	private final List<String> allowedValues;

	protected ListProperty(String name, List<String> allowedValues) {
		super(name, String.class);
		this.allowedValues = allowedValues;
	}

	public static ListProperty create(String name, List<String> allowedValues) {
		return new ListProperty(name, allowedValues);
	}

	@Override
	public @NotNull List<String> getPossibleValues() {
		return allowedValues;
	}

	@Override
	public @NotNull String getName(String value) {
		return value;
	}

	@Override
	public @NotNull Optional<String> getValue(String value) {
		return allowedValues.contains(value) ? Optional.of(value) : Optional.empty();
	}

	@Override
	public int getInternalIndex(@NotNull String comparable) {
		return this.allowedValues.indexOf(comparable);
	}
}
