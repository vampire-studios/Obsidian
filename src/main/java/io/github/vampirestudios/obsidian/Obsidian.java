package io.github.vampirestudios.obsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.addonModules.*;
import io.github.vampirestudios.obsidian.api.bedrock.block.Event;
import io.github.vampirestudios.obsidian.api.bedrock.block.events.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import io.github.vampirestudios.obsidian.commands.DumpRegistriesCommand;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting()
            .setLenient()
            .create();
    public static String MOD_ID = "obsidian";
    public static final Registry<AddonModule> ADDON_MODULE_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(id("addon_modules")), Lifecycle.stable());
    public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "item_groups")), Lifecycle.stable());
    public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "entity_components")), Lifecycle.stable());
    public static final Registry<Class<? extends Event>> BEDROCK_BLOCK_EVENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_event_registry")), Lifecycle.stable());
    public static final Registry<Class<? extends io.github.vampirestudios.obsidian.api.bedrock.Component>> BEDROCK_BLOCK_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_components_registry")), Lifecycle.stable());
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static final Logger BEDROCK_LOGGER = LogManager.getLogger("[" + NAME + ": Bedrock]");
    public static Obsidian INSTANCE;
    public static String VERSION = "0.4.0";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info(String.format("You're now running Obsidian v%s for 1.17.1", VERSION));

        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> DumpRegistriesCommand.register(commandDispatcher));

        //Item Groups
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "building_blocks", ItemGroup.BUILDING_BLOCKS);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "decorations", ItemGroup.DECORATIONS);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "redstone", ItemGroup.REDSTONE);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "transportation", ItemGroup.TRANSPORTATION);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "misc", ItemGroup.MISC);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "food", ItemGroup.FOOD);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "tools", ItemGroup.TOOLS);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "combat", ItemGroup.COMBAT);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "brewing", ItemGroup.BREWING);
        registerInRegistryVanilla(ITEM_GROUP_REGISTRY, "search", ItemGroup.SEARCH);

        //Entity Components
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "annotation.break_door", BreakDoorAnnotationComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "annotation.open_door", OpenDoorAnnotationComponent.class);

        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "admire_item", AdmireItemComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "agable", AgeableComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "angry", AngryComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "area_attack", AreaAttackComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "attack_cooldown", AttackCooldownComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "barter", BarterComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "block_sensor", BlockSensorComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "boostable", BoostableComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "boss", BossComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "break_blocks", BreakBlocksComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "breathable", BreathableComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "celebrate", CelebrateBehaviourComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "collision_box", CollisionBoxComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "health", HealthComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "movement", MovementComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "namable", NamableComponent.class);

        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "movement.basic", BasicMovementComponent.class);

        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.panic", PanicBehaviourComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.tempt", TemptBehaviourComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
        registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);

        //Bedrock Block Events
        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "add_mob_effect", AddMobEffect.class);
        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "damage", Damage.class);
        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "decrement_stack", DecrementStack.class);
        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "die", Die.class);
        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_effect", PlayEffect.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_sound", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "remove_mob_effect", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "run_command", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_at_pos", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_property", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "spawn_loot", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "swing", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "teleport", LookAtPlayerBehaviourComponent.class);
//        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "transform_item", LookAtPlayerBehaviourComponent.class);

        registerInRegistry(ADDON_MODULE_REGISTRY, "item_group", new ItemGroups());
        registerInRegistry(ADDON_MODULE_REGISTRY, "blocks", new Blocks());
        registerInRegistry(ADDON_MODULE_REGISTRY, "ores", new Ores());
        registerInRegistry(ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
        registerInRegistry(ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
        registerInRegistry(ADDON_MODULE_REGISTRY, "armor", new Armor());
        registerInRegistry(ADDON_MODULE_REGISTRY, "elytra", new Elytras());
        registerInRegistry(ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
        registerInRegistry(ADDON_MODULE_REGISTRY, "item", new Items());
        registerInRegistry(ADDON_MODULE_REGISTRY, "tool", new Tools());
        registerInRegistry(ADDON_MODULE_REGISTRY, "particle", new Particles());
        registerInRegistry(ADDON_MODULE_REGISTRY, "sound_events", new SoundEvents());
        registerInRegistry(ADDON_MODULE_REGISTRY, "music_discs", new MusicDiscs());
        registerInRegistry(ADDON_MODULE_REGISTRY, "ranged_weapon", new RangedWeapons());
        registerInRegistry(ADDON_MODULE_REGISTRY, "weapon", new Weapons());
        registerInRegistry(ADDON_MODULE_REGISTRY, "commands", new Commands());
        registerInRegistry(ADDON_MODULE_REGISTRY, "enchantments", new Enchantments());
        registerInRegistry(ADDON_MODULE_REGISTRY, "entity_models", new EntityModels());
        registerInRegistry(ADDON_MODULE_REGISTRY, "entities", new Entities());
//        registerInRegistry(ADDON_MODULE_REGISTRY, "shields", new Shields());
        registerInRegistry(ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
        registerInRegistry(ADDON_MODULE_REGISTRY, "food", new Food());
        registerInRegistry(ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
        registerInRegistry(ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());
        registerInRegistry(ADDON_MODULE_REGISTRY, "fuel_sources", new FuelSources());

        ObsidianAddonLoader.loadDefaultObsidianAddons();
        CompletableFuture.runAsync(ObsidianAddonLoader::loadObsidianAddons, ObsidianAddonLoader.EXECUTOR_SERVICE);
//        BedrockAddonLoader.loadDefaultBedrockAddons();
//        CompletableFuture.runAsync(BedrockAddonLoader::loadBedrockAddons, BedrockAddonLoader.EXECUTOR_SERVICE);
    }

    public <T> void registerInRegistryVanilla(Registry<T> registry, String name, T idk) {
        Registry.register(registry, name, idk);
    }

    public <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
        Registry.register(registry, new Identifier(MOD_ID, name), idk);
    }

}
