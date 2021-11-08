package io.github.vampirestudios.obsidian.api.obsidian.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.state.property.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a property that has integer values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class FloatProperty extends Property<Float> {
	private final ImmutableSet<Float> values;

	protected FloatProperty(String string, float i, float j) {
		super(string, Float.class);
		if (i < 0) {
			throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
		} else if (j <= i) {
			throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + i + ")");
		} else {
			Set<Float> set = Sets.newHashSet();

			for(float k = i; k <= j; ++k) {
				set.add(k);
			}

			this.values = ImmutableSet.copyOf((Collection)set);
		}
	}

	public Collection<Float> getValues() {
		return this.values;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof FloatProperty floatProperty && super.equals(object)) {
			return this.values.equals(floatProperty.values);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.values.hashCode();
	}

	/**
	 * Creates an integer property.
	 * 
	 * <p>Note that this method computes all possible values.
	 * 
	 * @throws IllegalArgumentException if {@code 0 <= min < max} is not
	 * satisfied
	 * 
	 * @param name the name of the property; see {@linkplain Property#name(Comparable)}  the note on the
	 * name}
	 * @param min the minimum value the property contains
	 * @param max the maximum value the property contains
	 */
	public static FloatProperty of(String name, float min, float max) {
		return new FloatProperty(name, min, max);
	}

	public Optional<Float> parse(String name) {
		try {
			Float integer = Float.valueOf(name);
			return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String name(Float integer) {
		return integer.toString();
	}
}
