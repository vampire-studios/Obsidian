package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class BlockSoundGroups implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		CustomSoundGroup customSoundGroup = Obsidian.GSON.fromJson(new FileReader(file), CustomSoundGroup.class);
		try {
			if (customSoundGroup == null) return;

			ResourceLocation identifier = Objects.requireNonNullElseGet(
					customSoundGroup.id,
					() -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (customSoundGroup.id == null) customSoundGroup.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

			registerSoundIfNotFound(customSoundGroup.break_sound);
			registerSoundIfNotFound(customSoundGroup.step_sound);
			registerSoundIfNotFound(customSoundGroup.place_sound);
			registerSoundIfNotFound(customSoundGroup.hit_sound);
			registerSoundIfNotFound(customSoundGroup.fall_sound);

			register(ContentRegistries.BLOCK_SOUND_GROUPS, "sound_groups", identifier, customSoundGroup);
		} catch (Exception e) {
			failedRegistering("sound_groups", file.getName(), e);
		}
	}

	private void registerSoundIfNotFound(ResourceLocation sound) {
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(sound)) Obsidian.registerInRegistry(BuiltInRegistries.SOUND_EVENT, sound, SoundEvent.createVariableRangeEvent(sound));
	}

	@Override
	public String getType() {
		return "block/sound_groups";
	}

}
