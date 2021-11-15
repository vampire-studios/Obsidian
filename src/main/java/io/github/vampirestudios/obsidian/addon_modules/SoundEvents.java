package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.MusicDisc;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MusicDiscItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class SoundEvents implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        MusicDisc musicDisc = Obsidian.GSON.fromJson(new FileReader(file), MusicDisc.class);
        try {
            if (musicDisc == null) return;
            Item.Settings settings = new Item.Settings().group(musicDisc.information.getItemGroup())
                    .maxCount(musicDisc.information.max_count).rarity(musicDisc.information.getRarity());
            Registry.register(Registry.ITEM, musicDisc.information.name.id, new MusicDiscItemImpl(musicDisc, settings
                    .maxDamage(musicDisc.information.use_duration)));
            register(MUSIC_DISCS, "music_disc", musicDisc.information.name.id, musicDisc);
        } catch (Exception e) {
            failedRegistering("music_disc", musicDisc.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "sound_events";
    }
}
