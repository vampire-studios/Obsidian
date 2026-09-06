package io.github.vampirestudios.obsidian.api.obsidian.ui;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

public class GUI {
	public Identifier id;

	public Identifier texture;
	public String title;
	public Widget[] widgets;
	public Slot[] slots;
	public int containerSize;
	public int textureWidth = 176;
	public int textureHeight = 166;

	public static class Widget {
		public String type;
		public Position position;
		public String text;
		public String placeholder;
		public boolean customPosString;
		public String buttonType;
		public boolean hasText;
		public WidgetSprites widgetSprites = new WidgetSprites(
				Identifier.withDefaultNamespace("widget/button"), Identifier.withDefaultNamespace("widget/button_disabled"), Identifier.withDefaultNamespace("widget/button_highlighted")
		);
		public EditBoxOptions editBox;
		public ProgressBar progressBar;
		public boolean defaultState;
		public String onText;
		public String offText;
		public int activeColor = 16777215;
		public int defaultTextColor = 10526880;
		public int disabledTextColor;

		public static class EditBoxOptions {
			public boolean multiLine;
			public boolean fitting;
			public int maxLength;
			public int defaultTextColor;
			public int disabledTextColor;
			public boolean bordered;
			public boolean editable;
		}

		public static class ProgressBar {
			public Identifier texture;
			public Orientation orientation;

			public enum Orientation {
				HORIZONTAL,
				VERTICAL
			}
		}
	}

	public static class Slot {
		public String type;
		public int x;
		public int y;
		public String filter; // Only for filtered slots
	}

	public static class Position {
		public int x;
		public int y;
		public int width;
		public int height;
	}
}
