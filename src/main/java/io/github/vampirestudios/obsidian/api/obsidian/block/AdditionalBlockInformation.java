package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.util.Identifier;

public class AdditionalBlockInformation {

    public String extraBlocksName = "";

    public boolean slab = false;
    public boolean stairs = false;

    public boolean walls = false;
    public boolean fence = false;
    public boolean fenceGate = false;

    public boolean button = false;
    public boolean pressurePlate = false;

    public boolean door = false;
    public boolean trapdoor = false;

    public boolean path = false;
    public boolean lantern = false;
    public boolean barrel = false;
    public boolean leaves = false;
    public boolean plant = false;
    public boolean chains = false;
    public boolean cake_like = false;
    public boolean waterloggable = false;

    public boolean dyable = false;
    public int defaultColor = 16579836;

    public boolean sittable = false;
    public boolean isConvertible = false;
    public Convertible convertible;

    public static class Convertible {
        public boolean drops_item = false;
        public boolean reversible = false;

        public Identifier parent_block;
        public Identifier transformed_block;
        public Identifier dropped_item;
        public Identifier sound;

        public ConversionItem conversionItem;
        public ConversionItem reversalItem;

        public static class ConversionItem {
            public Identifier item;
            public Identifier tag;
        }

    }

}