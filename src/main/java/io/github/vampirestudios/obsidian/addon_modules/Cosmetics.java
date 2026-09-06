package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.Cosmetic;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
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
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Cosmetics implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Cosmetic cosmetic = AddonFormats.read(addon, file, Cosmetic.class);
		try {
			if (cosmetic == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
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

			CreativeModeTabEvents.modifyOutputEvent(creativeTab).register(entries -> entries.accept(registeredItem));
			register(ContentRegistries.COSMETICS, "cosmetic", identifier, cosmetic);
		} catch (Exception e) {
			failedRegistering("cosmetic", file.getName(), e);
		}
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		return ItemModuleHelper.baseProperties(item);
	}

	private ResourceKey<CreativeModeTab> getCreativeTab(Cosmetic cosmetic) {
		return ItemModuleHelper.getCreativeTab(cosmetic);
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