package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;

public class DisplayInformation {

    public TextureAndModelInformation blockModel;
    @Deprecated public TextureAndModelInformation model;
    public TextureAndModelInformation itemModel;
//    public Map<String, Property> blockState;
    public BlockProperty blockState;
    public TooltipInformation[] lore = new TooltipInformation[0];

    public static class Property {
        public Identifier model;
        public int x;
        public int y;
        public int z;
    }

}
