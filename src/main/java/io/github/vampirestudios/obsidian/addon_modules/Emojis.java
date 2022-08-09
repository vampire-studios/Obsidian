package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.EmojiCode;
import io.github.vampirestudios.obsidian.api.EmojiType;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.emoji.Emoji;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Emojis implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Emoji emoji = Obsidian.GSON.fromJson(new FileReader(file), Emoji.class);
        try {
            if (emoji == null) return;
            EmojiType.emojiCodes.add(new EmojiCode(emoji.code, emoji.emoji));
            register(ContentRegistries.EMOJIS, "emoji", emoji.name, emoji);
        } catch (Exception e) {
            failedRegistering("emoji", emoji.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "emoji";
    }
}
