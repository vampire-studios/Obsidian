package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static String VERSION = "0.2.0";

    public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "item_groups")), Lifecycle.stable());

//    public static final DataPackModdingManager MODDING_MANAGER = new DataPackModdingManager();

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "1.16.3"));
        GeckoLib.initialize();
        Registry.register(ITEM_GROUP_REGISTRY, "building_blocks", ItemGroup.BUILDING_BLOCKS);
        Registry.register(ITEM_GROUP_REGISTRY, "decorations", ItemGroup.DECORATIONS);
        Registry.register(ITEM_GROUP_REGISTRY, "redstone", ItemGroup.REDSTONE);
        Registry.register(ITEM_GROUP_REGISTRY, "transportation", ItemGroup.TRANSPORTATION);
        Registry.register(ITEM_GROUP_REGISTRY, "misc", ItemGroup.MISC);
        Registry.register(ITEM_GROUP_REGISTRY, "food", ItemGroup.FOOD);
        Registry.register(ITEM_GROUP_REGISTRY, "tools", ItemGroup.TOOLS);
        Registry.register(ITEM_GROUP_REGISTRY, "combat", ItemGroup.COMBAT);
        Registry.register(ITEM_GROUP_REGISTRY, "brewing", ItemGroup.BREWING);
        Registry.register(ITEM_GROUP_REGISTRY, "search", ItemGroup.SEARCH);

        ConfigHelper.loadDefault();
        CompletableFuture.runAsync(ConfigHelper::loadConfig, ConfigHelper.EXECUTOR_SERVICE);
    }

}