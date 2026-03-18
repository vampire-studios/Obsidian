package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.Cosmetic;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.Equippable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Cosmetics implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		Cosmetic cosmetic = BaseGson.GSON.fromJson(new FileReader(file), Cosmetic.class);
		try {
			if (cosmetic == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
			cosmetic.information.id = identifier;

			Item.Properties settings = createItemProperties(cosmetic)
					.setId(ResourceKey.create(Registries.ITEM, identifier));
			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());
			ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(cosmetic);

			Item registeredItem;
			if (isWearable(cosmetic)) {
				registeredItem = registerWearableItem(expanded, cosmetic, identifier, settings, creativeTab);
			} else {
				if (isDyeable(cosmetic)) {
					int defaultDyeableColor = 0xFFFFFF;
					if (cosmetic.information.getItemSettings().defaultColor != 0) {
						defaultDyeableColor = cosmetic.information.getItemSettings().getDefaultColor();
					} else if (cosmetic.information.getItemSettings().getParentSettings().defaultColor != 0) {
						defaultDyeableColor = cosmetic.information.getItemSettings().getParentSettings().getDefaultColor();
					}
					settings.component(DataComponents.DYED_COLOR, new DyedItemColor(defaultDyeableColor));
				}
				registeredItem = expanded.registerItem(identifier.getPath(), new ItemImpl(cosmetic, settings), creativeTab);
			}

			ItemGroupEvents.modifyEntriesEvent(creativeTab).register(entries -> entries.accept(registeredItem));
			register(ContentRegistries.COSMETICS, "cosmetic", identifier, cosmetic);
		} catch (Exception e) {
			failedRegistering("cosmetic", file.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
		for (var e : map.entrySet()) {
			var type = (DataComponentType<T>) e.getKey();
			var opt  = (Optional<T>) e.getValue();
			opt.ifPresent(v -> props.component(type, v));
		}
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		Item.Properties props = new Item.Properties();

		var comps = item.components;
		if (comps == null) {
			return props;
		}

		applyAllComponents(props, comps);
		return props;
	}

	private ResourceKey<CreativeModeTab> getCreativeTab(Cosmetic cosmetic) {
		ResourceKey<CreativeModeTab> creativeTab;
		if (cosmetic.components != null && cosmetic.components.get(OItemComponents.CREATIVE_TAB) != null &&
				cosmetic.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
			Identifier tabLocation = (Identifier) Objects.requireNonNull(cosmetic.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
			creativeTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabLocation);
		} else if (cosmetic.information.getItemSettings().getItemGroup() != null) {
			creativeTab = cosmetic.information.getItemSettings().getItemGroup();
		} else if (cosmetic.information.getItemSettings().getParentSettings().getItemGroup() != null) {
			creativeTab = cosmetic.information.getItemSettings().getParentSettings().getItemGroup();
		} else {
			creativeTab = net.minecraft.world.item.CreativeModeTabs.BUILDING_BLOCKS;
		}
		return creativeTab;
	}

	private boolean isWearable(Cosmetic cosmetic) {
		return (cosmetic.information.getItemSettings().wearable
				|| cosmetic.information.getItemSettings().getParentSettings().wearable
		) && (cosmetic.information.getItemSettings().maxStackSize <= 1
				|| cosmetic.information.getItemSettings().getParentSettings().maxStackSize <= 1);
	}

	private boolean isDyeable(Cosmetic cosmetic) {
		return cosmetic.information.getItemSettings().dyeable
				|| cosmetic.information.getItemSettings().getParentSettings().dyeable;
	}

	private Item registerWearableItem(RegistryHelperItemExpanded expanded, Cosmetic cosmetic, Identifier identifier,
									  Item.Properties settings, ResourceKey<CreativeModeTab> creativeTab) {
		if (cosmetic.information.getItemSettings().wearableSlot != null && !cosmetic.information.getItemSettings().wearableSlot.isEmpty()) {
			settings.component(DataComponents.EQUIPPABLE,
					Equippable.builder(EquipmentSlot.byName(cosmetic.information.getItemSettings().wearableSlot)).build());
		} else if (cosmetic.information.getItemSettings().getParentSettings().wearableSlot != null
				&& !cosmetic.information.getItemSettings().getParentSettings().wearableSlot.isEmpty()) {
			settings.component(DataComponents.EQUIPPABLE,
					Equippable.builder(EquipmentSlot.byName(cosmetic.information.getItemSettings().getParentSettings().wearableSlot)).build());
		}

		if (isDyeable(cosmetic)) {
			int defaultDyeableColor = 0xFFFFFF;
			if (cosmetic.information.getItemSettings().defaultColor != 0) {
				defaultDyeableColor = cosmetic.information.getItemSettings().getDefaultColor();
			} else if (cosmetic.information.getItemSettings().getParentSettings().defaultColor != 0) {
				defaultDyeableColor = cosmetic.information.getItemSettings().getParentSettings().getDefaultColor();
			}
			settings.component(DataComponents.DYED_COLOR, new DyedItemColor(defaultDyeableColor));
		}
		return expanded.registerItem(identifier.getPath(), new ItemImpl(cosmetic, settings), creativeTab);
	}

	@Override
	public String getType() {
		return "item/cosmetic";
	}
}