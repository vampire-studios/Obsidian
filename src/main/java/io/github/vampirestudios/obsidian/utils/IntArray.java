package io.github.vampirestudios.obsidian.utils;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class IntArray {
	private final int[] array;

	public IntArray(int size) {
		this.array = new int[size];
	}

	public IntArray(int[] array) {
		this.array = Arrays.copyOf(array, array.length);
	}

	// New constructor with min and max values
	public IntArray(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("Min cannot be greater than max.");
		}
		this.array = IntStream.rangeClosed(min, max).toArray();
	}

	public void set(int index, int value) {
		array[index] = value;
	}

	public int get(int index) {
		return array[index];
	}

	public int size() {
		return array.length;
	}

	public int[] getArray() {
		return array;
	}

	// Method to get the minimum value in the array
	public int getMin() {
		return Arrays.stream(array).min().orElseThrow(IllegalStateException::new);
	}

	// Method to get the maximum value in the array
	public int getMax() {
		return Arrays.stream(array).max().orElseThrow(IllegalStateException::new);
	}

	public void forEach(IntConsumer action) {
		for (int value : array) {
			action.accept(value);
		}
	}


	public void forEachIndexed(BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < array.length; i++) {
			action.accept(i, array[i]);
		}
	}

	public IntArray map(IntUnaryOperator mapper) {
		IntArray result = new IntArray(size());
		for (int i = 0; i < array.length; i++) {
			result.set(i, mapper.applyAsInt(array[i]));
		}
		return result;
	}

	public IntArray filter(IntPredicate predicate) {
		return new IntArray(
				IntStream.of(array)
						.filter(predicate)
						.toArray()
		);
	}

	@Override
	public String toString() {
		return Arrays.toString(array);
	}
}