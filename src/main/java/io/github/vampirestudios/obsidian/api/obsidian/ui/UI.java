package io.github.vampirestudios.obsidian.api.obsidian.ui;

import blue.endless.jankson.annotation.SerializedName;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class UI {
	public LayoutType type;
	public LayoutOrientation orientation;

	public Identifier id;

	@SerializedName("horizontal_sizing") public Object horizontalSizing;
	@SerializedName("vertical_sizing") public Object verticalSizing;

	@SerializedName("horizontal_alignment") public HorizontalAlignment horizontalAlignment;
	@SerializedName("vertical_alignment") public VerticalAlignment verticalAlignment;

	public int rows;
	public int columns;

	public Inset margin;
	public Inset padding;

	public Surface surface;

	public List<Component> components = new ArrayList<>();

	public Sizing getHorizontalSizing() {
		if (!(horizontalSizing instanceof Sizing sizing)) {
			return new Sizing().setType(SizingType.CONTENT);
		} else {
			return sizing;
		}
	}

	public Sizing getVerticalSizing() {
		if (!(verticalSizing instanceof Sizing sizing)) {
			return new Sizing().setType(SizingType.CONTENT);
		} else {
			return sizing;
		}
	}

	public io.wispforest.owo.ui.core.Component getLayout() {
		return switch (type) {
			case GRID -> Containers.grid(getHorizontalSizing().getSizing(), getVerticalSizing().getSizing(), rows, columns);
			case FLOW_PANEL -> switch (orientation) {
					case VERTICAL -> Containers.verticalFlow(getHorizontalSizing().getSizing(), getVerticalSizing().getSizing());
					case HORIZONTAL -> Containers.horizontalFlow(getHorizontalSizing().getSizing(), getVerticalSizing().getSizing());
			};
		};
	}

	public static class Surface {
		public SurfaceType type;

		public Identifier texture;
		public int textureWidth;
		public int textureHeight;

		public String flatColor;

		public String outlineColor;

		public String topLeftColor;
		public String topRightColor;
		public String bottomRightColor;
		public String bottomLeftColor;

		public io.wispforest.owo.ui.core.Surface getSurface() {
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
		}

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
