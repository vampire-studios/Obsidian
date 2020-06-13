package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static String VERSION = "0.1.0";

//    public static final DataPackModdingManager MODDING_MANAGER = new DataPackModdingManager();

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "1.16-pre5"));

//        MODDING_MANAGER.registerReloadListener();
        ConfigHelper.loadDefault();
        CompletableFuture.runAsync(ConfigHelper::loadConfig, ConfigHelper.EXECUTOR_SERVICE);
    }

}