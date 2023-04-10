package io.github.vampirestudios.obsidian.api.obsidian.ui;

public class Inset {
	public Side side;

	public int top;
	public int bottom;
	public int left;
	public int right;
	public int single;
	public int vertical;
	public int horizontal;

	/*public Insets getInsets() {
		return switch (side) {
			case TOP -> Insets.top(top);
			case BOTTOM -> Insets.bottom(bottom);
			case LEFT -> Insets.left(left);
			case RIGHT -> Insets.right(right);
			case SINGLE -> Insets.of(single);
			case ALL -> Insets.of(top, bottom, left, right);
			case VERTICAL -> Insets.vertical(vertical);
			case HORIZONTAL -> Insets.horizontal(horizontal);
			case BOTH_DIRECTIONS -> Insets.both(vertical, horizontal);
		};
	}*/

	public enum Side {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		SINGLE,
		ALL,
		VERTICAL,
		HORIZONTAL,
		BOTH_DIRECTIONS
	}
}