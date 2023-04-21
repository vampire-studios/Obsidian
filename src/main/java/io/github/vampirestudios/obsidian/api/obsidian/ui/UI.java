package io.github.vampirestudios.obsidian.api.obsidian.ui;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class UI {
	public String type;
	public String orientation;

	public ResourceLocation id;

//	@SerializedName("horizontal_alignment") public HorizontalAlignment horizontalAlignment;
//	@SerializedName("vertical_alignment") public VerticalAlignment verticalAlignment;

	public int rows;
	public int columns;

	public Inset margin;
	public Inset padding;

	public List<Component> components = new ArrayList<>();

	public Sizing sizing(JsonObject jsonObject, String type) {
		if(GsonHelper.isStringValue(jsonObject, type)) {
			return Sizing.of(GsonHelper.getAsString(jsonObject, type));
		} else if(GsonHelper.isObjectNode(jsonObject, type)) {
			return Sizing.of(GsonHelper.getAsJsonObject(jsonObject, type));
		} else {
			return new Sizing().setType(SizingType.CONTENT);
		}
	}

	public Surface surface(JsonObject jsonObject) {
		if(GsonHelper.isStringValue(jsonObject, "surface")) {
			return new Surface().setSurfaceType(Surface.SurfaceType.valueOf(GsonHelper.getAsString(jsonObject, "surface").toUpperCase(Locale.ROOT)));
		} else if(GsonHelper.isObjectNode(jsonObject, "surface")) {
			return Surface.of(GsonHelper.getAsJsonObject(jsonObject, "surface"));
		} else {
			return new Surface().setSurfaceType(Surface.SurfaceType.PANEL);
		}
	}

	public LayoutType getLayoutType() {
		return LayoutType.valueOf(type.toUpperCase(Locale.ROOT));
	}

	public LayoutOrientation getLayoutOrientation() {
		return LayoutOrientation.valueOf(orientation.toUpperCase(Locale.ROOT));
	}

	/*public io.wispforest.owo.ui.core.Component getLayout(JsonObject jsonObject) {
		Sizing horizontalSizing = sizing(jsonObject, "horizontal_sizing");
		Sizing verticalSizing = sizing(jsonObject, "vertical_sizing");
		return switch (getLayoutType()) {
			case GRID -> Containers.grid(horizontalSizing.get(), verticalSizing.get(), rows, columns);
			case FLOW_PANEL -> switch (getLayoutOrientation()) {
					case VERTICAL -> Containers.verticalFlow(horizontalSizing.get(), verticalSizing.get());
					case HORIZONTAL -> Containers.horizontalFlow(horizontalSizing.get(), verticalSizing.get());
			};
		};
	}*/

	public static class Surface {
		public SurfaceType type;

		public ResourceLocation texture;
		public int textureWidth;
		public int textureHeight;

		public String flatColor;

		public String outlineColor;

		public String topLeftColor;
		public String topRightColor;
		public String bottomRightColor;
		public String bottomLeftColor;

		public static Surface of(JsonObject jsonObject) {
			Surface surface = new Surface();
			surface.type = switch (GsonHelper.getAsString(jsonObject, "type").toUpperCase(Locale.ROOT)) {
				case "CONTENT" -> Surface.SurfaceType.PANEL;
				case "DARK_PANEL" -> Surface.SurfaceType.DARK_PANEL;
				case "VANILLA_TRANSLUCENT" -> Surface.SurfaceType.VANILLA_TRANSLUCENT;
				case "OPTIONS_BACKGROUND" -> Surface.SurfaceType.OPTIONS_BACKGROUND;
				case "BLANK" -> Surface.SurfaceType.BLANK;
				case "FLAT" -> Surface.SurfaceType.FLAT;
				case "OUTLINE" -> Surface.SurfaceType.OUTLINE;
				case "TILED" -> Surface.SurfaceType.TILED;
				case "GRADIENT" -> Surface.SurfaceType.GRADIENT;
				default -> throw new IllegalStateException("Unexpected value: " + Arrays.toString(Surface.SurfaceType.values()));
			};

			if (GsonHelper.isStringValue(jsonObject, "texture")) surface.texture = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "texture"));

			if (GsonHelper.isNumberValue(jsonObject, "textureWidth")) surface.textureWidth = GsonHelper.getAsInt(jsonObject, "textureWidth");
			if (GsonHelper.isNumberValue(jsonObject, "textureHeight")) surface.textureHeight = GsonHelper.getAsInt(jsonObject, "textureHeight");

			if (GsonHelper.isStringValue(jsonObject, "flatColor")) surface.flatColor = GsonHelper.getAsString(jsonObject, "flatColor");

			if (GsonHelper.isStringValue(jsonObject, "outlineColor")) surface.outlineColor = GsonHelper.getAsString(jsonObject, "outlineColor");

			if (GsonHelper.isStringValue(jsonObject, "topLeftColor")) surface.topLeftColor = GsonHelper.getAsString(jsonObject, "topLeftColor");
			if (GsonHelper.isStringValue(jsonObject, "topRightColor")) surface.topRightColor = GsonHelper.getAsString(jsonObject, "topRightColor");
			if (GsonHelper.isStringValue(jsonObject, "bottomRightColor")) surface.bottomRightColor = GsonHelper.getAsString(jsonObject, "bottomRightColor");
			if (GsonHelper.isStringValue(jsonObject, "bottomLeftColor")) surface.bottomLeftColor = GsonHelper.getAsString(jsonObject, "bottomLeftColor");
			return surface;
		}

		public Surface setSurfaceType(SurfaceType surfaceType) {
			this.type = surfaceType;
			return this;
		}

		/*public io.wispforest.owo.ui.core.Surface getSurface() {
			return switch (type) {
				case PANEL -> io.wispforest.owo.ui.core.Surface.PANEL;
				case DARK_PANEL -> io.wispforest.owo.ui.core.Surface.DARK_PANEL;
				case VANILLA_TRANSLUCENT -> io.wispforest.owo.ui.core.Surface.VANILLA_TRANSLUCENT;
				case OPTIONS_BACKGROUND -> io.wispforest.owo.ui.core.Surface.OPTIONS_BACKGROUND;
				case BLANK -> io.wispforest.owo.ui.core.Surface.BLANK;
				case FLAT -> io.wispforest.owo.ui.core.Surface.flat(parseColor(flatColor));
				case OUTLINE -> io.wispforest.owo.ui.core.Surface.outline(parseColor(outlineColor));
				case TILED -> io.wispforest.owo.ui.core.Surface.tiled(texture, textureWidth, textureHeight);
				case GRADIENT -> (matrices, component) -> Drawer.drawGradientRect(matrices,
						component.x(), component.y(), component.width(), component.height(),
						parseColor(topLeftColor), parseColor(topRightColor),
						parseColor(bottomRightColor), parseColor(bottomLeftColor)
				);
			};
		}*/

		public int parseColor(String color) {
			String color1 = !color.isEmpty() && !color.isBlank()
					? flatColor.replace("#", "").replace("0x", "")
					: "ffffff";
			return Integer.parseInt(color1, 16);
		}

		public enum SurfaceType {
			PANEL,
			DARK_PANEL,
			VANILLA_TRANSLUCENT,
			OPTIONS_BACKGROUND,
			BLANK,
			FLAT,
			OUTLINE,
			TILED,
			GRADIENT
		}
	}
}
