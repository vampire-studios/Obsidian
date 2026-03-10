package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;

public class AdditionalBlockInformation {

    public boolean overworldLike = true;
    public boolean netherLike = false;
    public boolean bambooLike = false;
    public boolean pillarModel = false;
    public boolean topBottomModel = false;

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
    @SerializedName("is_convertible")
    @com.google.gson.annotations.SerializedName("is_convertible")
    public boolean isConvertible = false;
    public Convertible convertible;

    public static class Convertible {
        public boolean drops_item = false;
        public boolean reversible = false;

        public Identifier parent_block;
        public Identifier transformed_block;
        public Identifier dropped_item;
        public Identifier sound;

        @SerializedName("conversion_item")
        @com.google.gson.annotations.SerializedName("conversion_item")
        public ConversionItem conversionItem;
        @SerializedName("reversal_item")
        @com.google.gson.annotations.SerializedName("reversal_item")
        public ConversionItem reversalItem;

        public static class ConversionItem {
            public Identifier item;
            public Identifier tag;
        }

    }

}