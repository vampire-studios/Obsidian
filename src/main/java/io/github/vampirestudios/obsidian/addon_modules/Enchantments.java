package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EnchantmentImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Enchantments implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Enchantment enchantment = Obsidian.GSON.fromJson(new FileReader(file), Enchantment.class);
        try {
            if (enchantment == null) return;
            Registry.register(Registry.ENCHANTMENT, enchantment.name.id, new EnchantmentImpl(enchantment));
            register(ContentRegistries.ENCHANTMENTS, "enchantment", enchantment.name.id, enchantment);
        } catch (Exception e) {
            failedRegistering("enchantment", enchantment.name.id.getPath(), e);
        }
    }

    @Override
    public String getType() {
        return "enchantments";
    }
}
