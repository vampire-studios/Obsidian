package io.github.vampirestudios.obsidian.api.obsidian.ui;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.wispforest.owo.ui.component.Components;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class Component {
	public String type;
	public Identifier texture;
	public Identifier textureAtlas;
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
	public NbtCompound nbt;
	public Identifier entityType;
	public Identifier item;
	public int count = 1;
	public double minSliderValue, maxSliderValue;

	public int row;
	public int column;

	public Text getText(JsonObject jsonObject) {
		if(JsonHelper.hasString(jsonObject, "text")) {
			return Text.literal(JsonHelper.getString(jsonObject, "text"));
		} else if(JsonHelper.hasJsonObject(jsonObject, "text")) {
			return SpecialText.of(JsonHelper.getObject(jsonObject, "text"));
		} else {
			return Text.literal("");
		}
	}

	public Sizing sizing(JsonObject jsonObject, String type) {
		if(JsonHelper.hasString(jsonObject, type)) {
			return Sizing.of(JsonHelper.getString(jsonObject, type));
		} else if(JsonHelper.hasJsonObject(jsonObject, type)) {
			return Sizing.of(JsonHelper.getObject(jsonObject, type));
		} else {
			return new Sizing().setType(SizingType.CONTENT);
		}
	}

	public ComponentType getType() {
		return ComponentType.valueOf(type.toUpperCase(Locale.ROOT));
	}

	@Environment(EnvType.CLIENT)
	public io.wispforest.owo.ui.core.Component getComponent(JsonObject jsonObject) {
		Sizing horizontalSizing = sizing(jsonObject, "horizontal_sizing");
		Sizing verticalSizing = sizing(jsonObject, "vertical_sizing");
		Sizing sizing = sizing(jsonObject, "sizing");
		return switch (getType()) {
			case TEXTURED_BUTTON -> Components.texturedButton(texture, getText(jsonObject), width, height, u, v, (buttonWidget) -> {});
			case TEXTURED_BUTTON_CUSTOM_TEXTURE_SIZE -> Components.texturedButton(texture, getText(jsonObject), width, height, u, v, textureWidth, textureHeight,
					(buttonWidget) -> {});
			case BUTTON -> Components.button(getText(jsonObject), (buttonWidget) -> {});
			case BUTTON_CUSTOM_SIZE -> Components.button(getText(jsonObject), width, height, (buttonWidget) -> {});
			case TEXT_BOX -> Components.textBox(sizing.get());
			case TEXT_BOX_WITH_TEXT -> Components.textBox(sizing.get(), textBoxText);
			case ENTITY -> Components.entity(sizing.get(), Registry.ENTITY_TYPE.get(entityType), nbt);
			case ITEM -> Components.item(new ItemStack(Registry.ITEM.get(item), count));
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