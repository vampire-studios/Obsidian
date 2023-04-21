package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.ResourceLocation;

public class ExpandedItemGroup {

    public ResourceLocation target_group;
    public Tab[] tabs;
    public TabButton[] buttons;

    static class Tab {
//        public String iconType;
//        public IconTextureInformation iconInformation;
        public ResourceLocation icon;
        public String name;
        public ResourceLocation tag;

//        public Icon icon() {
//            if (iconType.equals("item")) {
//                return Icon.of(Registry.ITEM.get(iconItem));
//            } else {
//                return Icon.of(iconInformation.texture, iconInformation.u, iconInformation.v,
//                        iconInformation.textureWidth, iconInformation.textureHeight);
//            }
//        }
    }

    static class TabButton {
//        public String iconType;
//        public IconTextureInformation iconInformation;
        public ResourceLocation icon;
        public String name;

//        public Icon icon() {
//            if (iconType.equals("item")) {
//                return Icon.of(Registry.ITEM.get(iconItem));
//            } else {
//                return Icon.of(iconInformation.texture, iconInformation.u, iconInformation.v,
//                        iconInformation.textureWidth, iconInformation.textureHeight);
//            }
//        }
    }

    static class IconTextureInformation {
        public ResourceLocation texture;
        public int u;
        public int v;
        public int textureHeight;
        public int textureWidth;
    }

}
