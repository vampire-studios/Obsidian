package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.KeyBinding;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.KeybindingImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class KeyBindings implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        KeyBinding keyBinding = Obsidian.GSON.fromJson(new FileReader(file), KeyBinding.class);
        try {
            if (keyBinding == null) return;
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    keyBinding.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (keyBinding.id == null) keyBinding.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            KeyBindingRegistryImpl.registerKeyBinding(new KeybindingImpl(keyBinding));
            register(ContentRegistries.KEY_BINDINGS, "key_binding", identifier, keyBinding);
        } catch (Exception e) {
            failedRegistering("key_binding", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "key_binding";
    }
}
