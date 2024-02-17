package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockSetType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.addon_modules.ContentUtils.*;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class BlockSetTypes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		BlockSetType blockSetType = Obsidian.GSON.fromJson(new FileReader(file), BlockSetType.class);
		try {
			if (blockSetType == null) return;

			ResourceLocation identifier = Objects.requireNonNullElseGet(
					blockSetType.id,
					() -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (blockSetType.id == null) blockSetType.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

			registerSoundIfNotFound(blockSetType.soundType);
			registerSoundIfNotFound(blockSetType.doorClose);
			registerSoundIfNotFound(blockSetType.doorOpen);
			registerSoundIfNotFound(blockSetType.trapdoorClose);
			registerSoundIfNotFound(blockSetType.trapdoorOpen);
			registerSoundIfNotFound(blockSetType.pressurePlateClickOff);
			registerSoundIfNotFound(blockSetType.pressurePlateClickOn);
			registerSoundIfNotFound(blockSetType.buttonClickOff);
			registerSoundIfNotFound(blockSetType.buttonClickOn);

			register(ContentRegistries.BLOCK_SET_TYPES, "block_set_types", identifier, blockSetType);
			new BlockSetTypeBuilder()
					.openableByHand(blockSetType.canOpenByHand)
					.openableByWindCharge(blockSetType.canOpenByWindCharge)
					.buttonActivatedByArrows(blockSetType.canButtonBeActivatedByArrows)
					.pressurePlateActivationRule(blockSetType.getPressurePlateSensitivity())
					.soundGroup(getSoundType(blockSetType.soundType))
					.doorCloseSound(getSoundEvent(blockSetType.doorClose))
					.doorOpenSound(getSoundEvent(blockSetType.doorOpen))
					.trapdoorCloseSound(getSoundEvent(blockSetType.trapdoorClose))
					.trapdoorOpenSound(getSoundEvent(blockSetType.trapdoorOpen))
					.pressurePlateClickOffSound(getSoundEvent(blockSetType.pressurePlateClickOff))
					.pressurePlateClickOnSound(getSoundEvent(blockSetType.pressurePlateClickOn))
					.buttonClickOffSound(getSoundEvent(blockSetType.buttonClickOff))
					.buttonClickOnSound(getSoundEvent(blockSetType.buttonClickOn))
					.register(blockSetType.id);
		} catch (Exception e) {
			failedRegistering("block_set_types", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "block/block_set_type";
	}

}
