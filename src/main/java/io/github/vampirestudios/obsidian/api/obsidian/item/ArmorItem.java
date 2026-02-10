package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;

public class ArmorItem extends Item {
    @SerializedName("material_id")
    @com.google.gson.annotations.SerializedName("material_id")
    public Identifier materialId;
    public ArmorTemplate template = ArmorTemplate.LEATHER;
    public String slot;
    public Type armor_type = Type.HUMANOID;

    public ArmorMaterial getTemplateMaterial() {
        return switch (template) {
			case LEATHER, CUSTOM -> ArmorMaterials.LEATHER;
			case COPPER -> ArmorMaterials.COPPER;
			case CHAINMAIL -> ArmorMaterials.CHAINMAIL;
			case IRON -> ArmorMaterials.IRON;
			case GOLD -> ArmorMaterials.GOLD;
			case DIAMOND -> ArmorMaterials.DIAMOND;
			case TURTLE_SCUTE -> ArmorMaterials.TURTLE_SCUTE;
			case NETHERITE -> ArmorMaterials.NETHERITE;
			case ARMADILLO_SCUTE -> ArmorMaterials.ARMADILLO_SCUTE;
		};
    }

    public enum Type {
        HUMANOID,
        WOLF,
        HORSE,
        NAUTILUS,
        LLAMA
    }

    public enum ArmorTemplate {
        LEATHER,
        COPPER,
        CHAINMAIL,
        IRON,
        GOLD,
        DIAMOND,
        TURTLE_SCUTE,
        NETHERITE,
        ARMADILLO_SCUTE,
        CUSTOM
    }
}