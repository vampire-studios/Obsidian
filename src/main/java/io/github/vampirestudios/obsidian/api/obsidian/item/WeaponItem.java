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
        @com.google.gson.annotations.SerializedName("sword")      SWORD,
        @com.google.gson.annotations.SerializedName("spear")      SPEAR,
        @com.google.gson.annotations.SerializedName("mace")       MACE,
        @com.google.gson.annotations.SerializedName("longsword")  LONGSWORD,
        @com.google.gson.annotations.SerializedName("rapier")     RAPIER,
        @com.google.gson.annotations.SerializedName("dagger")     DAGGER,
        @com.google.gson.annotations.SerializedName("knife")      KNIFE,
        @com.google.gson.annotations.SerializedName("cleaver")    CLEAVER,
        @com.google.gson.annotations.SerializedName("scythe")     SCYTHE
    }
    public WeaponType weapon_type = WeaponType.SWORD;

    /** Longsword: extra entity interaction range in blocks (stacks with default reach). */
    @com.google.gson.annotations.SerializedName("reach_bonus")
    public float reach_bonus = 0.0f;

    /** Rapier: fraction 0–1 of target's armour value dealt as additional armour-bypassing damage. */
    @com.google.gson.annotations.SerializedName("armor_pierce")
    public float armor_pierce = 0.0f;

    /** Dagger / Knife: damage multiplier applied when striking the target from behind. */
    @com.google.gson.annotations.SerializedName("backstab_multiplier")
    public float backstab_multiplier = 1.0f;

    /** Knife: right-click throws the knife as a projectile and consumes one from the stack. */
    public boolean throwable = false;

    /** Cleaver / Scythe: radius in blocks for area-of-effect hits on secondary targets. */
    @com.google.gson.annotations.SerializedName("aoe_radius")
    public float aoe_radius = 0.0f;

    /** Cleaver / Scythe: fraction of main-hit damage applied to nearby AoE targets. */
    @com.google.gson.annotations.SerializedName("aoe_damage_multiplier")
    public float aoe_damage_multiplier = 0.5f;
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
						default -> throw new IllegalStateException("Unexpected value: " + path);
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
						default -> throw new IllegalStateException("Unexpected value: " + path);
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