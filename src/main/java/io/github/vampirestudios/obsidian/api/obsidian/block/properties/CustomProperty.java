package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Optional;

public class CustomProperty extends Property<String> {
    private final ImmutableList<String> allowedValues;

    protected CustomProperty(String name, ImmutableList<String> values) {
        super(name, String.class);
        allowedValues = values;
    }

    @Override
    public Collection<String> getPossibleValues() {
        return allowedValues;
    }

    @Override
    public String getName(String value) {
        return value;
    }

    @Override
    public Optional<String> getValue(String value) {
        return allowedValues.stream().filter(s -> s.equals(value)).findFirst();
    }

    public static CustomProperty create(String name, String... values) {
        return new CustomProperty(name, ImmutableList.copyOf(values));
    }

    public static CustomProperty create(String name, Collection<String> values) {
        return new CustomProperty(name, ImmutableList.copyOf(values));
    }
}