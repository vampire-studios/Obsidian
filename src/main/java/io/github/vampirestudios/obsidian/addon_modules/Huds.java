package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.ui.HUD;
import io.github.vampirestudios.obsidian.api.obsidian.ui.LayoutType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.JsonHelper;

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
        JsonObject jsonObject = JsonHelper.deserialize(new FileReader(file));
        try {
            if (hud == null) return;

            Component component = hud.getLayout(jsonObject);
            if (hud.getLayoutType() == LayoutType.FLOW_PANEL) {
                FlowLayout layout = (FlowLayout) component;
                hud.components.forEach(component1 -> layout.child(component1.getComponent(jsonObject)));
                layout.alignment(hud.horizontalAlignment, hud.verticalAlignment);
            } else if (hud.getLayoutType() == LayoutType.GRID) {
                GridLayout layout = (GridLayout) component;
                hud.components.forEach(component1 -> layout.child(
                        component1.getComponent(jsonObject),
                        component1.row, component1.column
                ));
                layout.alignment(hud.horizontalAlignment, hud.verticalAlignment);
                layout.padding(hud.padding.getInsets());
                layout.margins(hud.margin.getInsets());
                layout.surface(hud.surface(jsonObject).getSurface());
            }

            final Supplier<Component> hudComponent = () -> component;
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
