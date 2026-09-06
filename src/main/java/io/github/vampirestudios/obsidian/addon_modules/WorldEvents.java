package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.WorldEvent;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class WorldEvents implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		WorldEvent worldEvent = AddonFormats.read(addon, file, WorldEvent.class);
		if (worldEvent == null) return;

		Identifier eventId = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		try {
			worldEvent.id = eventId;
			register(ContentRegistries.WORLD_EVENTS, "world_event", eventId, worldEvent);
		} catch (Exception e) {
			failedRegistering("world_event", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "world/event";
	}
}
