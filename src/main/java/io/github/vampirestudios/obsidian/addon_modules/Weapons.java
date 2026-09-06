package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Weapons implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		WeaponItem weapon = AddonFormats.read(addon, file, WeaponItem.class);
		try {
			if (weapon == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			weapon.information.id = identifier;

			Item.Properties settings = new Item.Properties();
			if (!weapon.damageable) settings.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
			ItemModuleHelper.applyPalette(settings, weapon);
			settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));

			Item weaponItem = getWeaponItem(weapon, settings);
			Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), weaponItem);
			CreativeModeTabEvents.modifyOutputEvent(ItemModuleHelper.getCreativeTab(weapon)).register(entries -> entries.accept(registeredItem));
			register(ContentRegistries.WEAPONS, "weapon", identifier, weapon);
		} catch (Exception e) {
			failedRegistering("weapon", file.getName(), e);
		}
	}

	private static @NonNull Item getWeaponItem(WeaponItem weapon, Item.Properties settings) {
		ToolMaterial material = weapon.getTier();
		float dmg = weapon.attackDamage;
		float spd = weapon.attackSpeed;
		return switch (weapon.weapon_type) {
			case SWORD -> new MeleeWeaponImpl(weapon, material, dmg, spd, settings);
			case SPEAR -> new SpearItemImpl(weapon, material, dmg, spd, settings);
			case MACE -> new MaceWeaponImpl(weapon, material, dmg, spd, settings);
			case LONGSWORD -> new LongswordItemImpl(weapon, material, dmg, spd, settings);
			case RAPIER -> new RapierItemImpl(weapon, material, dmg, spd, settings);
			case DAGGER -> new DaggerItemImpl(weapon, material, dmg, spd, settings);
			case KNIFE -> new KnifeItemImpl(weapon, material, dmg, spd, settings);
			case CLEAVER -> new CleaverItemImpl(weapon, material, dmg, spd, settings);
			case SCYTHE -> new ScytheItemImpl(weapon, material, dmg, spd, settings);
		};
	}

	@Override
	public String getType() {
		return "item/weapon";
	}
}
