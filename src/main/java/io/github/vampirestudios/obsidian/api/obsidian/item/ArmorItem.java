package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Locale;

public class ArmorItem extends Item {
	@SerializedName(value = "material_id", alternate = "armor_material")
	public Identifier materialId;
	@SerializedName("armor_template")
	public ArmorTemplate armorTemplate = ArmorTemplate.LEATHER;
	@SerializedName(value = "slot", alternate = "armor_slot")
	public String slot;
	@SerializedName("armor_type")
	public Type armor_type = Type.HUMANOID;

	/** A material id is enough to select a custom material; old packs did not also name CUSTOM. */
	public boolean hasCustomMaterial() {
		return materialId != null || armorTemplate == ArmorTemplate.CUSTOM;
	}

	/** Accept both current ArmorType names and the old equipment-slot names used by addon packs. */
	public ArmorType getArmorType() {
		if (slot == null || slot.isBlank()) {
			throw new IllegalArgumentException("Humanoid armor is missing its slot");
		}

		return switch (slot.toUpperCase(Locale.ROOT)) {
			case "HEAD" -> ArmorType.HELMET;
			case "CHEST" -> ArmorType.CHESTPLATE;
			case "LEGS" -> ArmorType.LEGGINGS;
			case "FEET" -> ArmorType.BOOTS;
			default -> ArmorType.valueOf(slot.toUpperCase(Locale.ROOT));
		};
	}

	public ArmorMaterial getTemplateMaterial() {
		return switch (armorTemplate) {
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
