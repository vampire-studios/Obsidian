package io.github.vampirestudios.obsidian.api.obsidian.ui;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.wispforest.owo.ui.component.Components;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Component {
	@SerializedName("horizontal_sizing") public Sizing horizontalSizing;
	@SerializedName("vertical_sizing") public Sizing verticalSizing;
	public Sizing sizing;
	public ComponentType type;
	public Identifier texture;
	public Identifier textureAtlas;
	public SpecialText text;
	public int width;
	public int height;
	public int u;
	public int v;
	public int textureWidth;
	public int textureHeight;
	public int regionWidth;
	public int regionHeight;
//	public ButtonWidget.PressAction onPress;
	public String textBoxText;
	public NbtCompound nbt;
	public Identifier entityType;
	public Identifier item;
	public int count = 1;
	public double minSliderValue, maxSliderValue;

	public int row;
	public int column;

	public io.wispforest.owo.ui.core.Component getComponent() {
		return switch (type) {
			case TEXTURED_BUTTON -> Components.texturedButton(texture, text.getName(), width, height, u, v, (buttonWidget) -> {});
			case TEXTURED_BUTTON_CUSTOM_TEXTURE_SIZE -> Components.texturedButton(texture, text.getName(), width, height, u, v, textureWidth, textureHeight, (buttonWidget) -> {});
			case BUTTON -> Components.button(text.getName(), (buttonWidget) -> {});
			case BUTTON_CUSTOM_SIZE -> Components.button(text.getName(), width, height, (buttonWidget) -> {});
			case TEXT_BOX -> Components.textBox(horizontalSizing.getSizing());
			case TEXT_BOX_WITH_TEXT -> Components.textBox(horizontalSizing.getSizing(), textBoxText);
			case ENTITY -> Components.entity(sizing.getSizing(), Registry.ENTITY_TYPE.get(entityType), nbt);
			case ITEM -> Components.item(new ItemStack(Registry.ITEM.get(item), count));
			case LABEL -> Components.label(text.getName());
			case CHECKBOX -> Components.checkbox(text.getName());
			case SLIDER -> Components.slider(horizontalSizing.getSizing());
			case DISCRETE_SLIDER -> Components.discreteSlider(horizontalSizing.getSizing(), minSliderValue, maxSliderValue);
			case SPRITE -> Components.sprite(new SpriteIdentifier(textureAtlas, texture));
			case TEXTURE -> Components.texture(texture, u, v, regionWidth, regionHeight);
			case TEXTURE_CUSTOM_SIZE -> Components.texture(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
			case BOX -> Components.box(horizontalSizing.getSizing(), verticalSizing.getSizing());
			case DROPDOWN -> Components.dropdown(horizontalSizing.getSizing());
		};
	}

	public enum ComponentType {
		TEXTURED_BUTTON_CUSTOM_TEXTURE_SIZE,
		TEXTURED_BUTTON,
		BUTTON_CUSTOM_SIZE,
		BUTTON,
		TEXT_BOX,
		TEXT_BOX_WITH_TEXT,
		ENTITY,
		ITEM,
		LABEL,
		CHECKBOX,
		SLIDER,
		DISCRETE_SLIDER,
		SPRITE,
		TEXTURE,
		TEXTURE_CUSTOM_SIZE,
		BOX,
		DROPDOWN
	}
}