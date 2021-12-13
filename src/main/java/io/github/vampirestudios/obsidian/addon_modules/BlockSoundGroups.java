package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class BlockSoundGroups implements AddonModule {

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		CustomSoundGroup customSoundGroup = Obsidian.GSON.fromJson(new FileReader(file), CustomSoundGroup.class);
		try {
			if (customSoundGroup == null) return;
			registerSoundIfNotFound(customSoundGroup.break_sound);
			registerSoundIfNotFound(customSoundGroup.step_sound);
			registerSoundIfNotFound(customSoundGroup.place_sound);
			registerSoundIfNotFound(customSoundGroup.hit_sound);
			registerSoundIfNotFound(customSoundGroup.fall_sound);
			register(BLOCK_SOUND_GROUPS, "food_component", customSoundGroup.id, customSoundGroup);
		} catch (Exception e) {
			failedRegistering("food_component", customSoundGroup.id.toString(), e);
		}
	}

	private void registerSoundIfNotFound(Identifier sound) {
		if (!Registry.SOUND_EVENT.containsId(sound)) Obsidian.registerInRegistry(Registry.SOUND_EVENT, sound, new SoundEvent(sound));
	}

	@Override
	public String getType() {
		return "blocks/sound_groups";
	}

}
