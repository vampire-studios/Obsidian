package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;

public class ArmorItem extends Item {
//    @Deprecated public ArmorMaterial material;
    @SerializedName("material")
    @com.google.gson.annotations.SerializedName("material")
    public Identifier armorMaterial;
    @SerializedName("armor_type")
    @com.google.gson.annotations.SerializedName("armor_type")
    public String armorType;

}