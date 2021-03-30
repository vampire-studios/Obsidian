package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EnchantmentImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Enchantments implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Enchantment enchantment = Obsidian.GSON.fromJson(new FileReader(file), Enchantment.class);
        try {
            if (enchantment == null) return;
            Registry.register(Registry.ENCHANTMENT, enchantment.name.id, new EnchantmentImpl(enchantment));
            register(ENCHANTMENTS, "enchantment", enchantment.name.id.getPath(), enchantment);
        } catch (Exception e) {
            failedRegistering("enchantment", enchantment.name.id.getPath(), e);
        }
    }

    @Override
    public String getType() {
        return "enchantments";
    }
}
