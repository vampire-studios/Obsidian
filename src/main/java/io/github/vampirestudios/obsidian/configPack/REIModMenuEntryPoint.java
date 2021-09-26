/*
package io.github.vampirestudios.obsidian.configPack;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.client.ObsidianAddonInformationScreen;

import java.util.Collections;
import java.util.Map;

public class REIModMenuEntryPoint implements ModMenuApi {

    public ConfigScreenFactory<?> getAddonConfigScreenFactory(ObsidianAddon addon) {
        return parent -> new ObsidianAddonInformationScreen(parent, addon);
    }
    
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        for(IAddonPack pack : ConfigHelper.OBSIDIAN_ADDONS) {
            ObsidianAddon addon = (ObsidianAddon) pack;
            return ImmutableMap.of(
                    addon.getConfigPackInfo().namespace, getAddonConfigScreenFactory(addon)
            );
        }
        return Collections.emptyMap();
    }
}*/
