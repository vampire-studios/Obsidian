package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CrossbowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.TridentItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class RangedWeapons implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		RangedWeaponItem rangedWeapon = AddonFormats.read(addon, file, RangedWeaponItem.class);
		try {
			if (rangedWeapon == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			rangedWeapon.information.id = identifier;
			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

			Item.Properties settings = new Item.Properties();
			if (!rangedWeapon.damageable) settings.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
			if (rangedWeapon.components != null) ItemModuleHelper.applyAllComponents(settings, rangedWeapon.components);
			ItemModuleHelper.applyPalette(settings, rangedWeapon);
			settings.setId(ResourceKey.create(Registries.ITEM, identifier));

			Item item = switch (rangedWeapon.weapon_type) {
				case BOW, SHORTBOW ->
						expanded.registerItem(identifier.getPath(), new BowItemImpl(rangedWeapon, settings));
				case CROSSBOW, HEAVY_CROSSBOW ->
						expanded.registerItem(identifier.getPath(), new CrossbowItemImpl(rangedWeapon, settings));
				case TRIDENT ->
						expanded.registerItem(identifier.getPath(), new TridentItemImpl(rangedWeapon, settings));
			};
			CreativeModeTabEvents.modifyOutputEvent(ItemModuleHelper.getCreativeTab(rangedWeapon)).register(entries -> entries.accept(item));
			register(ContentRegistries.RANGED_WEAPONS, "ranged_weapon", identifier, rangedWeapon);
		} catch (Exception e) {
			failedRegistering("ranged_weapon", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/weapon/ranged";
	}
}
