package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Locale;

public class ItemSettings {
	@com.google.gson.annotations.SerializedName("parent")
	public Object baseItemSettings;

	// Basic Info
	@com.google.gson.annotations.SerializedName("item_group")
	public Identifier creativeTab;

	@com.google.gson.annotations.SerializedName("max_stack_size")
	public Integer maxStackSize = 64;

	@com.google.gson.annotations.SerializedName("max_uses")
	public int durability = 0;

	public String rarity = "common";
	public boolean fireproof = false;

	/**
	 * Identifier for a custom tooltip background sprite (minecraft:tooltip_style component).
	 * The sprite must be registered in the minecraft:tooltip_background atlas.
	 * Example: "mynamespace:my_wrap_tooltip"
	 */
	@com.google.gson.annotations.SerializedName("tooltip_style")
	public Identifier tooltipStyle;

	/** Seconds the whole item type is on cooldown after being used. */
	@com.google.gson.annotations.SerializedName("use_cooldown")
	public Float useCooldown;

	/** What is left in the grid after crafting with this, the way a bucket is left behind. */
	@com.google.gson.annotations.SerializedName("craft_remainder")
	public Identifier craftRemainder;

	/** What repairs this in an anvil: an item id, or {@code #} and an item tag. */
	@com.google.gson.annotations.SerializedName("repairs_with")
	public String repairsWith;

	public Fuel fuel;

	public static class Fuel {
		public int duration = 10;
		@com.google.gson.annotations.SerializedName("return_item")
		public Identifier returnItem;
	}

	// Fuel
	@com.google.gson.annotations.SerializedName("is_fuel")
	public boolean isFuel;

	@com.google.gson.annotations.SerializedName("fuel_duration")
	public int fuelDuration;

	// Enchanting
	@com.google.gson.annotations.SerializedName("has_enchantment_glint")
	public TriState hasEnchantmentGlint;

	@com.google.gson.annotations.SerializedName("is_enchantable")
	public boolean isEnchantable;

	public int enchantability;

	// Block Placing
	@SerializedName("can_place_block")
	public boolean canPlaceBlock;
	@SerializedName("placable_block")
	public Identifier placableBlock;

	// Wearable
	public boolean wearable;
	@com.google.gson.annotations.SerializedName("wearable_slot")
	public String wearableSlot;

	// Dyeable
	public boolean dyeable;
	@com.google.gson.annotations.SerializedName("default_color")
	public int defaultColor;

	public int getDefaultColor() {
		int color;
		if (this.defaultColor != 0)
			color = this.defaultColor;
		else if (this.getParentSettings().defaultColor != 0)
			color = this.getParentSettings().defaultColor;
		else
			color = 10511680;

		return color;
	}

	// Rendering
	@com.google.gson.annotations.SerializedName("custom_render_mode")
	public boolean customRenderMode;

	@com.google.gson.annotations.SerializedName("render_mode_models")
	public List<RenderModeModel> renderModeModels;

	public ItemSettings() {
		this.creativeTab = Identifier.withDefaultNamespace("building_blocks");

		this.hasEnchantmentGlint = TriState.DEFAULT;
		this.enchantability = 5;
		this.defaultColor = 16579836;
	}

	/**
	 * The settings this one starts from, if it names a {@code parent}. Resolved settings already have
	 * their parent's fields folded in, so this is only for reading the parent on its own.
	 */
	public ItemSettings getParentSettings() {
		ItemSettings parent = SettingsResolver.parent(baseItemSettings, ContentRegistries.ITEM_SETTINGS, ItemSettings.class);
		// Resolved settings already carry their parent's fields, so they are their own best answer here —
		// and callers reading this as a fallback never have to check for null.
		return parent == null ? this : parent;
	}

	/** Reads a declaration — settings written out, or the id of registered ones — into finished settings. */
	public static ItemSettings resolve(Object declaration) {
		return SettingsResolver.resolve(declaration, ContentRegistries.ITEM_SETTINGS, ItemSettings.class,
				new ItemSettings(), LegacyProperties::item);
	}

	/**
	 * Puts these settings onto an item's properties — the block item of a block, or an item of its own.
	 *
	 * @param damageable whether the thing being built can take damage at all; durability is skipped
	 *                   otherwise, since an item that cannot break has no use for it
	 */
	public Item.Properties applyTo(Item.Properties props, boolean damageable) {
		props.stacksTo(maxStackSize).rarity(getRarity());

		if (damageable && durability != 0) props.durability(durability);
		if (fireproof) props.fireResistant();
		if (isEnchantable) props.enchantable(enchantability);
		if (useCooldown != null) props.useCooldown(useCooldown);

		if (tooltipStyle != null) props.component(DataComponents.TOOLTIP_STYLE, tooltipStyle);

		// ITEM is a defaulted registry, so an id that does not exist comes back as air rather than null.
		if (craftRemainder != null && BuiltInRegistries.ITEM.containsKey(craftRemainder)) {
			props.craftRemainder(BuiltInRegistries.ITEM.getValue(craftRemainder));
		}
		if (repairsWith != null) {
			// A "#tag" repairs with anything in that tag; a plain id, with that one item.
			if (repairsWith.startsWith("#")) {
				Identifier tag = Identifier.tryParse(repairsWith.substring(1));
				if (tag != null) props.repairable(TagKey.create(Registries.ITEM, tag));
			} else {
				Identifier id = Identifier.tryParse(repairsWith);
				if (id != null && BuiltInRegistries.ITEM.containsKey(id)) {
					props.repairable(BuiltInRegistries.ITEM.getValue(id));
				}
			}
		}

		return props;
	}

	public Item.Properties applyTo(Item.Properties props) {
		return applyTo(props, true);
	}

	public Rarity getRarity() {
		return Rarity.valueOf(rarity.toUpperCase(Locale.ROOT));
	}

	public ResourceKey<CreativeModeTab> getItemGroup() {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, creativeTab);
	}
}