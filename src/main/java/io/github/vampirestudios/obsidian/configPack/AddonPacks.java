package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.client.resource.ObsidianAddonResourcePack;
import net.minecraft.server.packs.PackResources;
import net.vampirestudios.packwright.api.SidedPackwrightCallback;

public final class AddonPacks {

	private AddonPacks() {
	}

	public static void register() {
		SidedPackwrightCallback.BETWEEN_MODS_AND_USER.register((type, resources) -> {
			for (IAddonPack addon : ObsidianAddonLoader.OBSIDIAN_ADDONS) {
				try {
					PackResources virtualPack = addon.getVirtualResourcePack();
					if (virtualPack == null) continue;

					resources.add(new ObsidianAddonResourcePack(addon));
				} catch (Exception e) {
					Obsidian.LOGGER.error("[Obsidian] Failed to serve {} for addon {}: {}",
							type, addon.getObsidianDisplayName(), e.getMessage());
				}
			}
		});
	}
}
