package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ContentUtils {
	public static void registerSoundIfNotFound(ResourceLocation sound) {
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(sound)) Obsidian.registerInRegistry(BuiltInRegistries.SOUND_EVENT, sound, SoundEvent.createVariableRangeEvent(sound));
	}

	public static BlockSetType getBlockSetType(ResourceLocation id) {
		io.github.vampirestudios.obsidian.api.obsidian.block.BlockSetType blockSetType = ContentRegistries.BLOCK_SET_TYPES.getValue(id);
		return new BlockSetType(blockSetType.id.getPath(), blockSetType.canOpenByHand,
				blockSetType.canOpenByWindCharge,
				blockSetType.canButtonBeActivatedByArrows,
				blockSetType.getPressurePlateSensitivity(),
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

	public static WoodType getWoodType(ResourceLocation id) {
		io.github.vampirestudios.obsidian.api.obsidian.block.WoodType woodType = ContentRegistries.WOOD_TYPES.getValue(id);
		return new WoodType(woodType.id.getPath(), getBlockSetType(woodType.setType),
				getSoundType(woodType.soundType),
				getSoundType(woodType.hangingSignSoundType),
				getSoundEvent(woodType.fenceGateClose),
				getSoundEvent(woodType.fenceGateOpen)
		);
	}

	public static SoundType getSoundType(ResourceLocation id) {
		CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.getValue(id);
		return new SoundType(1.0f, 1.0f,
				getSoundEvent(customSoundGroup.break_sound),
				getSoundEvent(customSoundGroup.step_sound),
				getSoundEvent(customSoundGroup.place_sound),
				getSoundEvent(customSoundGroup.hit_sound),
				getSoundEvent(customSoundGroup.fall_sound)
		);
	}

	public static SoundEvent getSoundEvent(ResourceLocation sound) {
		if (!BuiltInRegistries.SOUND_EVENT.containsKey(sound)) return null;
		else return BuiltInRegistries.SOUND_EVENT.getValue(sound);
	}
}
