package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.item.FoodComponent;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import io.github.vampirestudios.obsidian.api.obsidian.palette.Palette;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.registry.components.PaletteApplicator;
import io.github.vampirestudios.obsidian.registry.components.PaletteComponent;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

/** Shared helpers to reduce boilerplate across item-related addon modules. */
public final class ItemModuleHelper {

	private ItemModuleHelper() {
	}

	// -------------------------------------------------------------------------
	// Component application
	// -------------------------------------------------------------------------

	/** Applies every entry from {@code map} onto {@code props}. */
	public static void applyAllComponents(Item.Properties props, DataComponentMap map) {
		for (TypedDataComponent<?> entry : map) {
			applyTyped(props, entry);
		}
	}

	private static <T> void applyTyped(Item.Properties props, TypedDataComponent<T> entry) {
		props.component(entry.type(), entry.value());
	}

	/**
	 * Creates a fresh {@link Item.Properties} and applies all user-defined
	 * components from {@code item.components} (if any).
	 */
	public static Item.Properties baseProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		Item.Properties props = new Item.Properties();
		if (item.components != null) applyAllComponents(props, item.components);
		applyPalette(props, item);
		return props;
	}

	// -------------------------------------------------------------------------
	// Food
	// -------------------------------------------------------------------------

	/**
	 * Applies a food declaration: its nutrition as {@code FOOD}, and how eating it behaves as
	 * {@code CONSUMABLE}.
	 *
	 * <p>Only nutrition still lives on food properties — effects, eating speed, animation and sound
	 * all moved to the consumable component, so both have to be set for a food to work fully. A
	 * {@code consumable} the pack wrote by hand always wins; pass the declared component map so it can
	 * be seen, or {@code null} where components are applied after this call and would overwrite it
	 * anyway.
	 */
	public static void applyFood(Item.Properties props, @Nullable FoodInformation info,
	                             @Nullable DataComponentMap declaredComponents) {
		if (info == null || info.foodComponent == null) return;

		FoodProperties properties =
				io.github.vampirestudios.obsidian.registry.Registries.FOODS.getValue(info.foodComponent);
		if (properties != null) props.food(properties);

		if (declaredComponents != null && declaredComponents.get(DataComponents.CONSUMABLE) != null) return;

		FoodComponent declaration = ContentRegistries.FOOD_COMPONENTS.getValue(info.foodComponent);
		if (declaration == null) return;

		props.component(DataComponents.CONSUMABLE, declaration.getConsumable(info));
	}

	// -------------------------------------------------------------------------
	// Palettes
	// -------------------------------------------------------------------------

	/**
	 * Gives a colourable item the palette it starts out painted with, taken from the item's own
	 * {@code rendering.palette} or, failing that, from its channels' {@code default_palette}. Items
	 * whose JSON already sets {@code obsidian:palette} directly are left alone.
	 */
	public static void applyPalette(Item.Properties props,
	                                io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		applyApplicatorUses(props, item);

		if (item.rendering == null || item.information == null) return;

		// Also registers an inline palette, so the asset pass later finds it by id.
		Identifier channelsId = item.rendering.resolveChannels(item.information.id);
		if (channelsId == null) return;

		props.component(OItemComponents.CHANNELS, channelsId);
		if (item.components != null && item.components.get(OItemComponents.PALETTE) != null) return;

		Identifier palette = item.rendering.palette;
		if (palette == null) {
			Palette channels = ContentRegistries.PALETTES.getValue(channelsId);
			if (channels != null) palette = channels.defaultPalette;
		}

		if (palette != null) props.component(OItemComponents.PALETTE, PaletteComponent.of(palette));
	}

	/**
	 * An applicator that declares {@code uses} gets durability to match, so a chroma set can wear
	 * out without the pack also having to set {@code durability} in the item settings. Applicators
	 * are unlimited unless they ask not to be.
	 */
	private static void applyApplicatorUses(Item.Properties props,
	                                        io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		if (item.components == null || !item.damageable) return;

		PaletteApplicator applicator = item.components.get(OItemComponents.PALETTE_APPLICATOR);
		if (applicator == null) return;

		applicator.uses().filter(uses -> uses > 0).ifPresent(props::durability);
	}

	// -------------------------------------------------------------------------
	// Creative tab resolution
	// -------------------------------------------------------------------------

	/**
	 * Resolves the creative tab for an item, checking (in order):
	 * <ol>
	 *   <li>The {@code OItemComponents.CREATIVE_TAB} component</li>
	 *   <li>The item's own settings {@code itemGroup}</li>
	 *   <li>The parent settings {@code itemGroup}</li>
	 *   <li>{@code defaultTab}</li>
	 * </ol>
	 */
	public static ResourceKey<CreativeModeTab> getCreativeTab(
			io.github.vampirestudios.obsidian.api.obsidian.item.Item item,
			ResourceKey<CreativeModeTab> defaultTab) {
		if (item.components != null) {
			Identifier tabId = item.components.get(OItemComponents.CREATIVE_TAB);
			if (tabId != null) {
				return ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabId);
			}
		}
		if (item.information.getItemSettings().getItemGroup() != null)
			return item.information.getItemSettings().getItemGroup();
		if (item.information.getItemSettings().getParentSettings().getItemGroup() != null)
			return item.information.getItemSettings().getParentSettings().getItemGroup();
		return defaultTab;
	}

	/**
	 * {@link #getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item, ResourceKey)}
	 * defaulting to {@link CreativeModeTabs#BUILDING_BLOCKS}.
	 */
	public static ResourceKey<CreativeModeTab> getCreativeTab(
			io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		return getCreativeTab(item, CreativeModeTabs.BUILDING_BLOCKS);
	}
}
