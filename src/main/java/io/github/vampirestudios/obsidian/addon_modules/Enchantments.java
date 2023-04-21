package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EnchantmentImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Enchantments implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Enchantment enchantment = Obsidian.GSON.fromJson(new FileReader(file), Enchantment.class);
        try {
            if (enchantment == null) return;
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    enchantment.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (enchantment.name.id == null) enchantment.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            Registry.register(BuiltInRegistries.ENCHANTMENT, identifier, new EnchantmentImpl(enchantment));
            register(ContentRegistries.ENCHANTMENTS, "enchantment", identifier, enchantment);
        } catch (Exception e) {
            failedRegistering("enchantment", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "enchantments";
    }
}
