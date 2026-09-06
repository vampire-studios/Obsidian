package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableArmorItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Armor implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		ArmorItem armor = AddonFormats.read(addon, file, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);

		try {
			if (armor == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			armor.information.id = identifier;

			net.minecraft.world.item.equipment.ArmorMaterial vanillaArmorMaterial;
			ArmorMaterial customMaterial = null;
			if (armor.hasCustomMaterial()) {
				if (armor.materialId == null) {
					throw new IllegalArgumentException("Custom armor is missing its material_id");
				}
				customMaterial = ContentRegistries.ARMOR_MATERIALS.getValue(armor.materialId);
				if (customMaterial == null) {
					throw new IllegalArgumentException("Unknown armor material " + armor.materialId);
				}

				// Like elytras, every custom armor item owns the equipment asset its rendering data
				// generates. The material supplies stats; the item supplies its worn appearance.
				ResourceKey<EquipmentAsset> equipmentAsset = ResourceKey.create(EquipmentAssets.ROOT_ID, identifier);
				vanillaArmorMaterial = new net.minecraft.world.item.equipment.ArmorMaterial(
						customMaterial.durability,
						customMaterial.defense,
						customMaterial.enchantability,
						customMaterial.getEquipSound(),
						customMaterial.toughness,
						customMaterial.knockback_resistance,
						TagKey.create(Registries.ITEM, customMaterial.getRepairTag(armor.materialId)),
						equipmentAsset
				);
			} else {
				vanillaArmorMaterial = armor.getTemplateMaterial();
			}

			Item item;
			Item.Properties settings = new Item.Properties()
					.stacksTo(armor.information.getItemSettings().maxStackSize)
					.rarity(Rarity.valueOf(armor.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));

			if (armor.armor_type == ArmorItem.Type.HUMANOID)
				settings.humanoidArmor(vanillaArmorMaterial, armor.getArmorType());
			if (armor.armor_type == ArmorItem.Type.WOLF)
				settings.wolfArmor(vanillaArmorMaterial);
			if (armor.armor_type == ArmorItem.Type.HORSE)
				settings.horseArmor(vanillaArmorMaterial);
			if (armor.armor_type == ArmorItem.Type.NAUTILUS)
				settings.nautilusArmor(vanillaArmorMaterial);
			if (customMaterial != null) {
				if (customMaterial.repairItem != null) {
					Item repairItem = BuiltInRegistries.ITEM.getValue(customMaterial.repairItem);
					if (repairItem != null) settings.repairable(repairItem);
				} else if (customMaterial.repairTag != null) {
					TagKey<Item> repairTag = TagKey.create(Registries.ITEM, customMaterial.repairTag);
					if (repairTag != null) settings.repairable(repairTag);
				}
			}

			settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
			ItemModuleHelper.applyPalette(settings, armor);

			if (armor.information.getItemSettings().dyeable)
				item = new DyeableArmorItemImpl(armor, settings);
			else
				item = new ArmorItemImpl(armor, settings);
			REGISTRY_HELPER.items().registerItem(identifier.getPath(), item);
			CreativeModeTabEvents.modifyOutputEvent(armor.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(item));

			register(ContentRegistries.ARMORS, "armor", identifier, armor);
		} catch (Exception e) {
			failedRegistering("armor", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/armor";
	}
}
