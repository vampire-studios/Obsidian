package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.SoundPlayingItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.GoatHornItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.InstrumentComponent;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class SoundPlayingItems implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		SoundPlayingItem soundItem = AddonFormats.read(addon, file, SoundPlayingItem.class);
		try {
			if (soundItem == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			soundItem.information.id = identifier;

			if (soundItem.sound_type == null || soundItem.sound_type.isBlank())
				throw new IllegalArgumentException("sound_type must be specified (\"music_disc\" or \"goat_horn\")");
			if (soundItem.sound == null || soundItem.sound.isBlank())
				throw new IllegalArgumentException("sound must be specified");
			Identifier soundId = Identifier.tryParse(soundItem.sound);
			if (soundId == null)
				throw new IllegalArgumentException("Invalid sound identifier: " + soundItem.sound);

			Item.Properties settings = ItemModuleHelper.baseProperties(soundItem)
					.setId(ResourceKey.create(Registries.ITEM, identifier));
			var creativeTab = ItemModuleHelper.getCreativeTab(soundItem, CreativeModeTabs.TOOLS_AND_UTILITIES);
			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

			switch (soundItem.sound_type) {
				case "music_disc" -> {
					settings.jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, soundId));
					expanded.registerItem(identifier.getPath(), new ItemImpl(soundItem, settings), creativeTab);
				}
				case "goat_horn" -> {
					settings.delayedComponent(DataComponents.INSTRUMENT,
							context -> new InstrumentComponent(context.getOrThrow(ResourceKey.create(Registries.INSTRUMENT, soundId))));
					expanded.registerItem(identifier.getPath(), new GoatHornItemImpl(soundItem, settings), creativeTab);
				}
				default -> throw new IllegalArgumentException(
						"Unknown sound_type: \"" + soundItem.sound_type + "\". Expected \"music_disc\" or \"goat_horn\"");
			}

			register(ContentRegistries.SOUND_PLAYING_ITEMS, "sound_playing_item", identifier, soundItem);
		} catch (Exception e) {
			failedRegistering("sound_playing_item", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/sound_playing_item";
	}
}
