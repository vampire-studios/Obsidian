package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TabbedGroup {
    @SerializedName("target_group") public String targetGroup;
    public Tab[] tabs;
    public Button[] buttons;
    public int stackHeight = 4;
    public Identifier customTexture = null;
    public boolean staticTitle = true;

    public static class Button {
        public Icon icon;
        public String link;
        public String name;
    }

    public static class Tab {
        public Icon icon;
        public String name;
        public Identifier contentTag;
        public Identifier texture = ItemGroupTab.DEFAULT_TEXTURE;
    }

    public static class Icon {
        public String type;
        public IconProperties properties;
        
        public static class IconProperties {
            public Identifier item;
            public Identifier texture;
            public int u, v;
            public int textureWidth, textureHeight, textureSize, frameDelay;
            public boolean loop;
        }

        public io.wispforest.owo.itemgroup.Icon getIcon() {
            return switch (type) {
                case "texture" -> io.wispforest.owo.itemgroup.Icon.of(properties.texture, properties.u, properties.v, properties.textureWidth, properties.textureHeight);
                case "animated" -> io.wispforest.owo.itemgroup.Icon.of(properties.texture, properties.textureSize, properties.frameDelay, properties.loop);
                case "item_like" -> io.wispforest.owo.itemgroup.Icon.of(Registry.ITEM.get(properties.item));
                case "stack", default -> io.wispforest.owo.itemgroup.Icon.of(new ItemStack(Registry.ITEM.get(properties.item)));
            };
        }
    }
}