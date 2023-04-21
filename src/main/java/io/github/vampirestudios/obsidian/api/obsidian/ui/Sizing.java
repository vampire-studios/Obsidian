package io.github.vampirestudios.obsidian.api.obsidian.ui;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.util.GsonHelper;

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
		sizing.type = switch (GsonHelper.getAsString(type, "type").toUpperCase(Locale.ROOT)) {
			case "CONTENT" -> SizingType.CONTENT;
			case "CONTENT_WITH_PADDING" -> SizingType.CONTENT_WITH_PADDING;
			case "FIXED" -> SizingType.FIXED;
			case "FILL" -> SizingType.FILL;
			default -> throw new IllegalStateException("Unexpected value: " + Arrays.toString(SizingType.values()));
		};
		if (GsonHelper.isNumberValue(type, "value")) sizing.value = GsonHelper.getAsInt(type, "value");
		if (GsonHelper.isNumberValue(type, "padding")) sizing.padding = GsonHelper.getAsInt(type, "padding");
		if (GsonHelper.isNumberValue(type, "percent")) sizing.percent = GsonHelper.getAsInt(type, "percent");
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

	/*public io.wispforest.owo.ui.core.Sizing get() {
		return switch (type) {
			case FIXED -> io.wispforest.owo.ui.core.Sizing.fixed(value);
			case CONTENT -> io.wispforest.owo.ui.core.Sizing.content();
			case CONTENT_WITH_PADDING -> io.wispforest.owo.ui.core.Sizing.content(padding);
			case FILL -> io.wispforest.owo.ui.core.Sizing.fill(percent);
		};
	}*/
}