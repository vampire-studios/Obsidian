package io.github.vampirestudios.obsidian.api.obsidian;

import com.glisco.owo.itemgroup.gui.ItemGroupTab;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
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
            public int textureWidth, textureHeight;
        }
        
        public com.glisco.owo.itemgroup.Icon getIcon() {
            if (type.equals("texture")) {
                return new com.glisco.owo.itemgroup.Icon.TextureIcon(properties.texture, properties.u, properties.v, properties.textureWidth, properties.textureHeight);
            } else {
                return new ItemIcon(new ItemStack(Registry.ITEM.get(properties.item)));
            }
        }
    }

    public record ItemIcon(ItemStack stack) implements com.glisco.owo.itemgroup.Icon {
        @Override
        public void render(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float delta) {
            MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(stack, x, y);
        }
    }
}