package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.ui.HUD;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.hud.Hud;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Supplier;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Huds implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        HUD hud = Obsidian.GSON.fromJson(new FileReader(file), HUD.class);
        try {
            if (hud == null) return;

            Component component = hud.getLayout();
            hud.components.forEach(component1 -> {

            });

            final Supplier<Component> hudComponent = () -> component;
            ClientTickEvents.END.register(client -> {
                while (Obsidian.binding.wasPressed()) {
                    if (Hud.hasComponent(hud.id)) {
                        Hud.remove(hud.id);
                    } else {
                        Hud.add(hud.id, hudComponent);
                    }
                }
            });
            register(ContentRegistries.HUDS, "hud", hud.id, hud);
        } catch (Exception e) {
            failedRegistering("hud", hud.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "client/hud";
    }
}
