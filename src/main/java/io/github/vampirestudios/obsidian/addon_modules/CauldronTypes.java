package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.getState;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CauldronTypes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		CauldronType cauldronType = AddonFormats.read(addon, file, CauldronType.class);
		try {
			if (cauldronType == null) return;

			Identifier identifier = Objects.requireNonNullElseGet(
					cauldronType.name,
					() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
			);
			if (cauldronType.name == null) cauldronType.name = identifier;

			CauldronInteraction interaction = buildInteraction(cauldronType, identifier);
			if (interaction != null) {
				Item item = BuiltInRegistries.ITEM.getValue(cauldronType.item);

				// Every cauldron state, so the item works on an empty cauldron and on a filled one alike.
				List.of(CauldronInteractions.EMPTY, CauldronInteractions.WATER,
								CauldronInteractions.LAVA, CauldronInteractions.POWDER_SNOW)
						.forEach(dispatcher -> dispatcher.put(item, interaction));
			}

			register(ContentRegistries.CAULDRON_TYPES, "cauldron_type", identifier, cauldronType);
		} catch (Exception e) {
			failedRegistering("cauldron_type", file.getName(), e);
		}
	}

	/**
	 * The interaction this definition describes, or null when it names something that does not exist —
	 * the entry is still registered so the pack can see it loaded, it just does nothing.
	 */
	private CauldronInteraction buildInteraction(CauldronType cauldronType, Identifier identifier) {
		if (cauldronType.item == null) {
			Obsidian.LOGGER.warn("Cauldron type {} names no item; it can never be triggered.", identifier);
			return null;
		}
		if (cauldronType.blockstate == null || cauldronType.blockstate.block == null) {
			Obsidian.LOGGER.warn("Cauldron type {} names no target block state.", identifier);
			return null;
		}

		Item item = BuiltInRegistries.ITEM.getValue(cauldronType.item);
		Block block = BuiltInRegistries.BLOCK.getValue(cauldronType.blockstate.block);
		if (item == null || block == null) {
			Obsidian.LOGGER.warn("Cauldron type {} names an unknown item or block ({} / {}).",
					identifier, cauldronType.item, cauldronType.blockstate.block);
			return null;
		}

		// Resolved once: getState consumes the property map as it reads it.
		Map<String, String> properties = cauldronType.blockstate.properties;
		BlockState target = getState(block, properties == null ? new java.util.HashMap<>() : properties);

		SoundEvent sound = cauldronType.sound_event == null
				? SoundEvents.BUCKET_EMPTY
				: BuiltInRegistries.SOUND_EVENT.getValue(cauldronType.sound_event);
		if (sound == null) sound = SoundEvents.BUCKET_EMPTY;

		SoundEvent playedSound = sound;
		return (state, level, pos, player, hand, stack) ->
				CauldronInteractions.emptyBucket(level, pos, player, hand, stack, target, playedSound);
	}

	@Override
	public String getType() {
		return "cauldron_type";
	}
}
