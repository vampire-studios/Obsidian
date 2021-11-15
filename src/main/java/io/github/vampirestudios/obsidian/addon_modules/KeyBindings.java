package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.KeyBinding;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.KeybindingImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class KeyBindings implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        KeyBinding keyBinding = Obsidian.GSON.fromJson(new FileReader(file), KeyBinding.class);
        try {
            if (keyBinding == null) return;
            KeyBindingRegistryImpl.registerKeyBinding(new KeybindingImpl(keyBinding));
            register(KEY_BINDINGS, "key_binding", keyBinding.id, keyBinding);
        } catch (Exception e) {
            failedRegistering("key_binding", keyBinding.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "key_binding";
    }
}
