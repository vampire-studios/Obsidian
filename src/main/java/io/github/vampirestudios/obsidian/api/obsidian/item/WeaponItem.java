package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;

public class WeaponItem extends Item {

    public Object material;
	public float attackSpeed;
	public float attackDamage;
    public enum WeaponType {
        @com.google.gson.annotations.SerializedName("sword")  SWORD,
        @com.google.gson.annotations.SerializedName("spear")  SPEAR,
        @com.google.gson.annotations.SerializedName("mace")   MACE
    }
    public WeaponType weapon_type = WeaponType.SWORD;
    /** Spear-only: nested under "spear" in JSON. All values default to vanilla iron spear. */
    public SpearProperties spear = new SpearProperties();
    /** Mace-only: nested under "mace" in JSON. Null fields fall back to the tool material's values. */
    public MaceProperties mace = new MaceProperties();

    public static class SpearProperties {
        /** attackDuration: charge cycle length in seconds; attack speed = 1/charge_time - 4. */
        public float charge_time         = 0.95f;
        /** damageMultiplier: full-charge damage multiplier. */
        public float damage_multiplier   = 0.95f;
        /** delay: seconds before the lunge activates after release. */
        public float delay               = 0.6f;
        /** dismountTime: time window (seconds) in which a dismount bonus can trigger. */
        public float dismount_time       = 2.5f;
        /** dismountThreshold: minimum speed needed to trigger the dismount bonus. */
        public float dismount_threshold  = 8.0f;
        /** knockbackTime: time window (seconds) in which a knockback bonus can trigger. */
        public float knockback_time      = 6.75f;
        /** knockbackThreshold: minimum speed needed to trigger the knockback bonus. */
        public float knockback_threshold = 5.1f;
        /** damageTime: time window (seconds) in which a damage bonus can trigger. */
        public float damage_time         = 11.25f;
        /** damageThreshold: minimum speed needed to trigger the damage bonus. */
        public float damage_threshold    = 4.6f;
    }

    public static class MaceProperties {
        /** Override durability; null = use tool material's durability. */
        public Integer durability = null;
        /** Override enchantability; null = use tool material's enchantment value. */
        public Integer enchantability = null;
        /** Override repair tag (e.g. "minecraft:iron_ingots"); null = use tool material's repair items. */
        public String repairable = null;
    }

    public ToolMaterial getTier() {
		switch (material) {
			case Identifier id -> {
				if (id.getNamespace().contains("minecraft")) {
					String path = id.getPath().toUpperCase(Locale.ROOT);
					return switch (path) {
						case "WOOD" -> ToolMaterial.WOOD;
						case "STONE" -> ToolMaterial.STONE;
						case "COPPER" -> ToolMaterial.COPPER;
						case "IRON" -> ToolMaterial.IRON;
						case "DIAMOND" -> ToolMaterial.DIAMOND;
						case "GOLD" -> ToolMaterial.GOLD;
						case "NETHERITE" -> ToolMaterial.NETHERITE;
						default -> throw new IllegalStateException(STR."Unexpected value: \{path}");
					};
				}
				ToolMaterial mat = ContentRegistries.TOOL_MATERIALS.getValue(id);
				if (mat == null) throw new IllegalStateException("Tool material not found in registry: " + id);
				return mat;
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				if (location == null) throw new IllegalArgumentException("Invalid material identifier: " + s);
				if (location.getNamespace().contains("minecraft")) {
					String path = location.getPath().toUpperCase(Locale.ROOT);
					return switch (path) {
						case "WOOD" -> ToolMaterial.WOOD;
						case "STONE" -> ToolMaterial.STONE;
						case "COPPER" -> ToolMaterial.COPPER;
						case "IRON" -> ToolMaterial.IRON;
						case "DIAMOND" -> ToolMaterial.DIAMOND;
						case "GOLD" -> ToolMaterial.GOLD;
						case "NETHERITE" -> ToolMaterial.NETHERITE;
						default -> throw new IllegalStateException(STR."Unexpected value: \{path}");
					};
				}
				ToolMaterial mat = ContentRegistries.TOOL_MATERIALS.getValue(location);
				if (mat == null) throw new IllegalStateException("Tool material not found in registry: " + location);
				return mat;
			}
			case null, default -> throw new IllegalStateException("Material field is null or unrecognized type for: " + this.information.id);
		}
    }

}