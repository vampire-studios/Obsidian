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
public class DoubleProperty extends Property<Double> {
	private final ImmutableSet<Double> values;

	protected DoubleProperty(String string, double i, double j) {
		super(string, Double.class);
		if (i < 0) {
			throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
		} else if (j <= i) {
			throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + i + ")");
		} else {
			Set<Double> set = Sets.newHashSet();

			for(double k = i; k <= j; ++k) {
				set.add(k);
			}

			this.values = ImmutableSet.copyOf((Collection)set);
		}
	}

	public Collection<Double> getValues() {
		return this.values;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof DoubleProperty floatProperty && super.equals(object)) {
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
	public static DoubleProperty of(String name, double min, double max) {
		return new DoubleProperty(name, min, max);
	}

	public Optional<Double> parse(String name) {
		try {
			Double integer = Double.valueOf(name);
			return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
		} catch (NumberFormatException var3) {
			return Optional.empty();
		}
	}

	public String name(Double integer) {
		return integer.toString();
	}
}
