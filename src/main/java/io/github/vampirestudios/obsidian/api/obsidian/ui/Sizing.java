package io.github.vampirestudios.obsidian.api.obsidian.ui;

public class Sizing {
	public SizingType type;
	public int value;
	public int padding;
	public int percent;

	public static Sizing fixed(int value) {
		return new Sizing().setType(SizingType.FIXED).setValue(value);
	}

	public static Sizing content() {
		return new Sizing().setType(SizingType.CONTENT);
	}

	public static Sizing contentWithPadding(int padding) {
		return new Sizing().setType(SizingType.CONTENT_WITH_PADDING).setPadding(padding);
	}

	public static Sizing fill(int percent) {
		return new Sizing().setType(SizingType.FILL).setPercent(percent);
	}

	public Sizing setType(SizingType type) {
		this.type = type;
		return this;
	}

	public Sizing setValue(int value) {
		this.value = value;
		return this;
	}

	public Sizing setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public Sizing setPercent(int percent) {
		this.percent = percent;
		return this;
	}

	public io.wispforest.owo.ui.core.Sizing getSizing() {
		return switch (type) {
			case FIXED -> io.wispforest.owo.ui.core.Sizing.fixed(value);
			case CONTENT -> io.wispforest.owo.ui.core.Sizing.content();
			case CONTENT_WITH_PADDING -> io.wispforest.owo.ui.core.Sizing.content(padding);
			case FILL -> io.wispforest.owo.ui.core.Sizing.fill(percent);
		};
	}
}