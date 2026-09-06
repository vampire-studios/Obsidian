package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Elytras implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Elytra item = AddonFormats.read(addon, file, Elytra.class);
		try {
			if (item == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			item.information.id = identifier;

			Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ItemImpl(item, new Item.Properties()
					.component(
							DataComponents.EQUIPPABLE,
							Equippable.builder(EquipmentSlot.CHEST)
									.setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
									.setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, identifier))
									.setDamageOnHurt(false)
									.build()
					)
					.component(DataComponents.GLIDER, Unit.INSTANCE)
					.component(DataComponents.ENCHANTABLE, new Enchantable(item.information.getItemSettings().enchantability))
					.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier))
					.stacksTo(1)
			));
			CreativeModeTabEvents.modifyOutputEvent(item.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(registeredItem));
			register(ContentRegistries.ELYTRAS, "elytra", identifier, item);
		} catch (Exception e) {
			failedRegistering("elytra", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/elytra";
	}
}
