package io.github.vampirestudios.obsidian.api;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TabbedGroup {

	@SerializedName("target_group") public String targetGroup;
	public Tab[] tabs;
	public Button[] buttons;
	public int stackHeight = 4;
	public ResourceLocation customTexture = null;
	public boolean displayTabNamesAsTitle = true;

	public static class Button {
		public Icon icon;
		public String link;
		public String name;
		public int u;
		public int v;
	}

	public static class Tab {
		public Icon icon;
		public String name;
		public ResourceLocation contentTag;
		public ResourceLocation texture;
	}

	public static class Icon {
		public String type;
		public IconProperties properties;

		public static class IconProperties {}

		public static class Item extends IconProperties {
			public ResourceLocation item;
		}

		public static class Texture extends IconProperties {
			public ResourceLocation texture;
			public int u, v;
			public int textureWidth, textureHeight;
		}

		public io.wispforest.owo.itemgroup.Icon getIcon() {
			if (type.equals("texture")) {
				Texture texture = (Texture) properties;
				return io.wispforest.owo.itemgroup.Icon.of(texture.texture, texture.u, texture.v, texture.textureWidth, texture.textureHeight);
			} else {
				Item item = (Item) properties;
				return io.wispforest.owo.itemgroup.Icon.of(new ItemStack(BuiltInRegistries.ITEM.get(item.item)));
			}
		}
	}

}
