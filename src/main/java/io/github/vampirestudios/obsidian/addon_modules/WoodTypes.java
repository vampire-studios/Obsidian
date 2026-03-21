package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.WoodType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.addon_modules.ContentUtils.*;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class WoodTypes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		WoodType woodTypes = BaseGson.GSON.fromJson(new FileReader(file), WoodType.class);
		try {
			if (woodTypes == null) return;

			Identifier identifier = Objects.requireNonNullElseGet(
					woodTypes.id,
					() -> Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (woodTypes.id == null) woodTypes.id = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));

			registerSoundIfNotFound(woodTypes.soundType);
			registerSoundIfNotFound(woodTypes.hangingSignSoundType);
			registerSoundIfNotFound(woodTypes.fenceGateClose);
			registerSoundIfNotFound(woodTypes.fenceGateOpen);

			register(ContentRegistries.WOOD_TYPES, "block_set_types", identifier, woodTypes);
			new WoodTypeBuilder()
					.soundType(getSoundType(woodTypes.setType))
					.hangingSignSoundType(getSoundType(woodTypes.hangingSignSoundType))
					.fenceGateCloseSound(getSoundEvent(woodTypes.fenceGateClose))
					.fenceGateOpenSound(getSoundEvent(woodTypes.fenceGateOpen))
					.register(woodTypes.id, getBlockSetType(woodTypes.setType));
		} catch (Exception e) {
			failedRegistering("wood_types", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "block/wood_type";
	}

}
