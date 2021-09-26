package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.dataexchange.DataExchangeAPI;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ObsidianServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DataExchangeAPI.prepareServerside();
    }
}
