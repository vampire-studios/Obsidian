package io.github.vampirestudios.obsidian.api.obsidian.ui;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;

import java.util.Arrays;
import java.util.Locale;

public class Sizing {
	public SizingType type;
	public int value;
	public int padding;
	public int percent;

	public static Sizing of(String type) {
		return new Sizing().setType(SizingType.valueOf(type.toUpperCase(Locale.ROOT)));
	}

	public static Sizing of(JsonObject type) {
		Sizing sizing = new Sizing();
		sizing.type = switch (JsonHelper.getString(type, "type").toUpperCase(Locale.ROOT)) {
			case "CONTENT" -> SizingType.CONTENT;
			case "CONTENT_WITH_PADDING" -> SizingType.CONTENT_WITH_PADDING;
			case "FIXED" -> SizingType.FIXED;
			case "FILL" -> SizingType.FILL;
			default -> throw new IllegalStateException("Unexpected value: " + Arrays.toString(SizingType.values()));
		};
		if (JsonHelper.hasNumber(type, "value")) sizing.value = JsonHelper.getInt(type, "value");
		if (JsonHelper.hasNumber(type, "padding")) sizing.padding = JsonHelper.getInt(type, "padding");
		if (JsonHelper.hasNumber(type, "percent")) sizing.percent = JsonHelper.getInt(type, "percent");
		return sizing;
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

	public Sizing setType(SizingType type) {
		this.type = type;
		return this;
	}

	public io.wispforest.owo.ui.core.Sizing get() {
		return switch (type) {
			case FIXED -> io.wispforest.owo.ui.core.Sizing.fixed(value);
			case CONTENT -> io.wispforest.owo.ui.core.Sizing.content();
			case CONTENT_WITH_PADDING -> io.wispforest.owo.ui.core.Sizing.content(padding);
			case FILL -> io.wispforest.owo.ui.core.Sizing.fill(percent);
		};
	}
}