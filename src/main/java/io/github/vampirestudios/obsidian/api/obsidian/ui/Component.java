package io.github.vampirestudios.obsidian.api.obsidian.ui;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Component {
	public String type;
	public ResourceLocation texture;
	public ResourceLocation textureAtlas;
	public Object text;
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
	public CompoundTag nbt;
	public ResourceLocation entityType;
	public ResourceLocation item;
	public int count = 1;
	public double minSliderValue, maxSliderValue;

	public int row;
	public int column;

	public net.minecraft.network.chat.Component getText(JsonObject jsonObject) {
		if(GsonHelper.isStringValue(jsonObject, "text")) {
			return net.minecraft.network.chat.Component.literal(GsonHelper.getAsString(jsonObject, "text"));
		} else if(GsonHelper.isObjectNode(jsonObject, "text")) {
			return SpecialText.of(GsonHelper.getAsJsonObject(jsonObject, "text"));
		} else {
			return net.minecraft.network.chat.Component.literal("");
		}
	}

	public Sizing sizing(JsonObject jsonObject, String type) {
		if(GsonHelper.isStringValue(jsonObject, type)) {
			return Sizing.of(GsonHelper.getAsString(jsonObject, type));
		} else if(GsonHelper.isObjectNode(jsonObject, type)) {
			return Sizing.of(GsonHelper.getAsJsonObject(jsonObject, type));
		} else {
			return new Sizing().setType(SizingType.CONTENT);
		}
	}

	public ComponentType getType() {
		return ComponentType.valueOf(type.toUpperCase(Locale.ROOT));
	}

	/*@Environment(EnvType.CLIENT)
	public io.wispforest.owo.ui.core.Component getComponent(JsonObject jsonObject) {
		Sizing horizontalSizing = sizing(jsonObject, "horizontal_sizing");
		Sizing verticalSizing = sizing(jsonObject, "vertical_sizing");
		Sizing sizing = sizing(jsonObject, "sizing");
		return switch (getType()) {
			case TEXTURED_BUTTON -> {
				ButtonComponent buttonComponent = Components.button(getText(jsonObject), buttonWidget -> {});
				buttonComponent.sizing(io.wispforest.owo.ui.core.Sizing.fixed(width), io.wispforest.owo.ui.core.Sizing.fixed(height));
				buttonComponent.renderer(ButtonComponent.Renderer.texture(texture, u, v, width, height));
				yield buttonComponent;
			}
			case TEXTURED_BUTTON_CUSTOM_TEXTURE_SIZE -> {
				ButtonComponent buttonComponent = Components.button(getText(jsonObject), buttonWidget -> {});
				buttonComponent.sizing(io.wispforest.owo.ui.core.Sizing.fixed(width), io.wispforest.owo.ui.core.Sizing.fixed(height));
				buttonComponent.renderer(ButtonComponent.Renderer.texture(texture, u, v, textureWidth, textureHeight));
				yield buttonComponent;
			}
			case BUTTON -> Components.button(getText(jsonObject), (ButtonComponent component) -> {});
			case BUTTON_CUSTOM_SIZE -> {
				ButtonComponent button = Components.button(getText(jsonObject), (buttonWidget) -> {});
				button.sizing(io.wispforest.owo.ui.core.Sizing.fixed(width), io.wispforest.owo.ui.core.Sizing.fixed(height));
				yield button;
			}
			case TEXT_BOX -> Components.textBox(sizing.get());
			case TEXT_BOX_WITH_TEXT -> Components.textBox(sizing.get(), textBoxText);
			case ENTITY -> Components.entity(sizing.get(), Registries.ENTITY_TYPE.get(entityType), nbt);
			case ITEM -> Components.item(new ItemStack(Registries.ITEM.get(item), count));
			case LABEL -> Components.label(getText(jsonObject));
			case CHECKBOX -> Components.checkbox(getText(jsonObject));
			case SLIDER -> Components.slider(sizing.get());
			case DISCRETE_SLIDER -> Components.discreteSlider(sizing.get(), minSliderValue, maxSliderValue);
			case SPRITE -> Components.sprite(new SpriteIdentifier(textureAtlas, texture));
			case TEXTURE -> Components.texture(texture, u, v, regionWidth, regionHeight);
			case TEXTURE_CUSTOM_SIZE -> Components.texture(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
			case BOX -> Components.box(horizontalSizing.get(), verticalSizing.get());
			case DROPDOWN -> Components.dropdown(sizing.get());
		};
	}*/

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