package io.github.vampirestudios.obsidian.api.obsidian.ui;

import blue.endless.jankson.annotation.SerializedName;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class UI {
	public LayoutType type;
	public Identifier id;
	@SerializedName("horizontal_sizing") public Sizing horizontalSizing;
	@SerializedName("vertical_sizing") public Sizing verticalSizing;
	public int rows;
	public int columns;
	public List<Component> components = new ArrayList<>();

	public io.wispforest.owo.ui.core.Component getLayout() {
		return switch (type) {
			case GRID -> Containers.grid(horizontalSizing.getSizing(), verticalSizing.getSizing(), rows, columns);
			case VERTICAL_FLOW -> Containers.verticalFlow(horizontalSizing.getSizing(), verticalSizing.getSizing());
			case HORIZONTAL_FLOW -> Containers.horizontalFlow(horizontalSizing.getSizing(), verticalSizing.getSizing());
		};
	}

	public static class Sizing {
		public SizingType type;
		public int value;
		public int padding;
		public int percent;

		public io.wispforest.owo.ui.core.Sizing getSizing() {
			return switch (type) {
				case FIXED -> io.wispforest.owo.ui.core.Sizing.fixed(value);
				case CONTENT -> io.wispforest.owo.ui.core.Sizing.content();
				case CONTENT_WITH_PADDING -> io.wispforest.owo.ui.core.Sizing.content(padding);
				case FILL -> io.wispforest.owo.ui.core.Sizing.fill(percent);
			};
		}
	}

	public static class Component {
		@SerializedName("horizontal_sizing") public Sizing horizontalSizing;
		@SerializedName("vertical_sizing") public Sizing verticalSizing;
		public Sizing sizing;
		public ComponentType type;
		public Identifier texture;
		public Identifier textureAtlas;
		public Text text;
		public int width;
		public int height;
		public int u;
		public int v;
		public int textureWidth;
		public int textureHeight;
		public int regionWidth;
		public int regionHeight;
		public ButtonWidget.PressAction onPress;
		public String textBoxText;
		public NbtCompound nbt;
		public Identifier entityType;
		public Identifier item;
		public int count = 1;
		public double minSliderValue, maxSliderValue;

		public io.wispforest.owo.ui.core.Component getComponent() {
			return switch (type) {
				case TEXTURED_BUTTON -> Components.texturedButton(texture, text, width, height, u, v, onPress);
				case TEXTURED_BUTTON_CUSTOM_TEXTURE_SIZE -> Components.texturedButton(texture, text, width, height, u, v, textureWidth, textureHeight, onPress);
				case BUTTON -> Components.button(text, onPress);
				case BUTTON_CUSTOM_SIZE -> Components.button(text, width, height, onPress);
				case TEXT_BOX -> Components.textBox(horizontalSizing.getSizing());
				case TEXT_BOX_WITH_TEXT -> Components.textBox(horizontalSizing.getSizing(), textBoxText);
				case ENTITY -> Components.entity(sizing.getSizing(), Registry.ENTITY_TYPE.get(entityType), nbt);
				case ITEM -> Components.item(new ItemStack(Registry.ITEM.get(item), count));
				case LABEL -> Components.label(text);
				case CHECKBOX -> Components.checkbox(text);
				case SLIDER -> Components.slider(horizontalSizing.getSizing());
				case DISCRETE_SLIDER -> Components.discreteSlider(horizontalSizing.getSizing(), minSliderValue, maxSliderValue);
				case SPRITE -> Components.sprite(new SpriteIdentifier(textureAtlas, texture));
				case TEXTURE -> Components.texture(texture, u, v, regionWidth, regionHeight);
				case TEXTURE_CUSTOM_SIZE -> Components.texture(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
				case BOX -> Components.box(horizontalSizing.getSizing(), verticalSizing.getSizing());
				case DROPDOWN -> Components.dropdown(horizontalSizing.getSizing());
			};
		}
	}
}
