package io.github.vampirestudios.obsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.api.bedrock.block.Event;
import io.github.vampirestudios.obsidian.api.bedrock.block.events.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.network.NetworkHandler;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static Obsidian INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static final Logger BEDROCK_LOGGER = LogManager.getLogger("[" + NAME + ": Bedrock]");
    public static String VERSION = "0.2.0";
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting()
            .create();

    public NetworkHandler networkHandler;

    public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "item_groups")), Lifecycle.stable());
    public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "entity_components")), Lifecycle.stable());
    public static final Registry<Class<? extends Event>> BEDROCK_BLOCK_EVENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_event_registry")), Lifecycle.stable());
    public static final Registry<Class<? extends io.github.vampirestudios.obsidian.api.bedrock.Component>> BEDROCK_BLOCK_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_components_registry")), Lifecycle.stable());

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "1.16.4"));

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

        Registry.register(ENTITY_COMPONENT_REGISTRY, "movement", MovementComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "health", HealthComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "collision_box", CollisionBoxComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "namable", NamableComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "movement.basic", BasicMovementComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "behavior.panic", PanicBehaviourComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "behavior.tempt", TemptBehaviourComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
        Registry.register(ENTITY_COMPONENT_REGISTRY, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);

        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "add_mob_effect", AddMobEffect.class);
        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "damage", Damage.class);
        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "decrement_stack", DecrementStack.class);
        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "die", Die.class);
        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "play_effect", PlayEffect.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "play_sound", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "remove_mob_effect", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "run_command", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_at_pos", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_property", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "spawn_loot", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "swing", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "teleport", LookAtPlayerBehaviourComponent.class);
//        Registry.register(BEDROCK_BLOCK_EVENT_REGISTRY, "transform_item", LookAtPlayerBehaviourComponent.class);

        ConfigHelper.loadDefaultObsidianAddons();
        BedrockAddonLoader.loadDefaultBedrockAddons();
        CompletableFuture.runAsync(ConfigHelper::loadObsidianAddons, ConfigHelper.EXECUTOR_SERVICE);
        CompletableFuture.runAsync(BedrockAddonLoader::loadBedrockAddons, BedrockAddonLoader.EXECUTOR_SERVICE);
        networkHandler = new NetworkHandler();
    }

}
