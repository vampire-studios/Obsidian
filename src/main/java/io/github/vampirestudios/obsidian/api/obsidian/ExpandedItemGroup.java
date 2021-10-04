package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;

public class ExpandedItemGroup {

    public Identifier target_group;
    public Tab[] tabs;
    public TabButton[] buttons;

    static class Tab {
//        public String iconType;
//        public IconTextureInformation iconInformation;
        public Identifier icon;
        public String name;
        public Identifier tag;

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
        public Identifier icon;
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
        public Identifier texture;
        public int u;
        public int v;
        public int textureHeight;
        public int textureWidth;
    }

}
