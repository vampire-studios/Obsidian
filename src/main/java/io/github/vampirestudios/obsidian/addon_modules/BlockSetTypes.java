package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockSetType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.addon_modules.ContentUtils.*;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class BlockSetTypes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		BlockSetType blockSetType = AddonFormats.read(addon, file, BlockSetType.class);
		try {
			if (blockSetType == null) return;

			Identifier identifier = Objects.requireNonNullElseGet(
					blockSetType.id,
					() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
			);
			if (blockSetType.id == null)
				blockSetType.id = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

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
					.soundType(getSoundType(blockSetType.soundType))
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
