package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.WoodType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class WoodTypes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		WoodType woodTypes = Obsidian.GSON.fromJson(new FileReader(file), WoodType.class);
		try {
			if (woodTypes == null) return;

			ResourceLocation identifier = Objects.requireNonNullElseGet(
					woodTypes.id,
					() -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (woodTypes.id == null) woodTypes.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

			registerSoundIfNotFound(woodTypes.soundType);
			registerSoundIfNotFound(woodTypes.hangingSignSoundType);
			registerSoundIfNotFound(woodTypes.fenceGateClose);
			registerSoundIfNotFound(woodTypes.fenceGateOpen);

			register(ContentRegistries.WOOD_TYPES, "block_set_types", identifier, woodTypes);
			WoodTypeRegistry.register(woodTypes.id, getBlockSetType(woodTypes.setType),
					getSoundType(woodTypes.soundType),
					getSoundType(woodTypes.hangingSignSoundType),
					getSoundEvent(woodTypes.fenceGateClose),
					getSoundEvent(woodTypes.fenceGateOpen)
			);
		} catch (Exception e) {
			failedRegistering("wood_types", file.getName(), e);
		}
	}

	private void registerSoundIfNotFound(ResourceLocation sound) {
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(sound)) Obsidian.registerInRegistry(BuiltInRegistries.SOUND_EVENT, sound, SoundEvent.createVariableRangeEvent(sound));
	}

	private BlockSetType getBlockSetType(ResourceLocation id) {
		io.github.vampirestudios.obsidian.api.obsidian.block.BlockSetType blockSetType = ContentRegistries.BLOCK_SET_TYPES.get(id);
		return new BlockSetType(blockSetType.id.getPath(), blockSetType.canOpenByHand,
				getSoundType(blockSetType.soundType),
				getSoundEvent(blockSetType.doorClose),
				getSoundEvent(blockSetType.doorOpen),
				getSoundEvent(blockSetType.trapdoorClose),
				getSoundEvent(blockSetType.trapdoorOpen),
				getSoundEvent(blockSetType.pressurePlateClickOff),
				getSoundEvent(blockSetType.pressurePlateClickOn),
				getSoundEvent(blockSetType.buttonClickOff),
				getSoundEvent(blockSetType.buttonClickOn)
		);
	}

	private SoundType getSoundType(ResourceLocation id) {
		CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.get(id);
		return new SoundType(1.0f, 1.0f,
				getSoundEvent(customSoundGroup.break_sound),
				getSoundEvent(customSoundGroup.step_sound),
				getSoundEvent(customSoundGroup.place_sound),
				getSoundEvent(customSoundGroup.hit_sound),
				getSoundEvent(customSoundGroup.fall_sound)
		);
	}

	private SoundEvent getSoundEvent(ResourceLocation sound) {
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(sound)) return null;
		else return BuiltInRegistries.SOUND_EVENT.get(sound);
	}

	@Override
	public String getType() {
		return "block/wood_types";
	}

}
