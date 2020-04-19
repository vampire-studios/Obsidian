package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.world.DimensionalBiomeSource;
import io.github.vampirestudios.obsidian.world.DimensionalBiomeSourceConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.biome.source.BiomeSourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.LongFunction;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static String VERSION = "0.1.0";

//    public static final DataPackModdingManager MODDING_MANAGER = new DataPackModdingManager();

    public static BiomeSourceType<DimensionalBiomeSourceConfig, DimensionalBiomeSource> DIMENSIONAL_BIOMES;

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "20w16a"));
        Constructor<BiomeSourceType> constructor;
        try {
            constructor = BiomeSourceType.class.getDeclaredConstructor(Function.class, LongFunction.class);
            constructor.setAccessible(true);
            DIMENSIONAL_BIOMES = constructor.newInstance((Function) DimensionalBiomeSource::new, (LongFunction) DimensionalBiomeSourceConfig::new);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

//        MODDING_MANAGER.registerReloadListener();
        ConfigHelper.loadDefault();
        CompletableFuture.runAsync(ConfigHelper::loadConfig, ConfigHelper.EXECUTOR_SERVICE);
    }

}