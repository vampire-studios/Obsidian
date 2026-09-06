package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.projectile.Projectile;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ProjectileArrowItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ProjectileItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.REGISTRY_HELPER;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Projectiles implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Projectile projectile = AddonFormats.read(addon, file, Projectile.class);
		try {
			if (projectile == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			projectile.information.id = identifier;

			Item.Properties settings = new Item.Properties();
			if (!projectile.damageable) settings.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
			ItemModuleHelper.applyPalette(settings, projectile);
			settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));

			// An arrow is ammunition, so it registers as an ArrowItem and is fired by a weapon that names
			// it; a thrown one is used straight from the hand.
			Item impl = projectile.isArrow()
					? new ProjectileArrowItem(projectile, settings)
					: new ProjectileItemImpl(projectile, settings);

			Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), impl);
			CreativeModeTabEvents.modifyOutputEvent(ItemModuleHelper.getCreativeTab(projectile))
					.register(entries -> entries.accept(registeredItem));

			// Registered under the item's id: the entity resolves its definition from the item it carries.
			register(ContentRegistries.PROJECTILES, "projectile", identifier, projectile);
		} catch (Exception e) {
			failedRegistering("projectile", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/projectile";
	}
}
