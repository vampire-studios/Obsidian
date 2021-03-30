package io.github.vampirestudios.obsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.addonModules.*;
import io.github.vampirestudios.obsidian.api.bedrock.block.Event;
import io.github.vampirestudios.obsidian.api.bedrock.block.events.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModuleVersionIndependent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import io.github.vampirestudios.obsidian.commands.DumpRegistriesCommand;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
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

import static net.minecraft.block.Blocks.*;

public class Obsidian implements ModInitializer {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting()
            .create();
    public static String MOD_ID = "obsidian";
    public static final Registry<AddonModule> ADDON_MODULE_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(id("addon_modules")), Lifecycle.stable());
    public static final Registry<AddonModuleVersionIndependent> ADDON_MODULE_VERSION_INDEPENDENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(id("addon_modules_version_independent")), Lifecycle.stable());
    public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "item_groups")), Lifecycle.stable());
    public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "entity_components")), Lifecycle.stable());
    public static final Registry<Class<? extends Event>> BEDROCK_BLOCK_EVENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_event_registry")), Lifecycle.stable());
    public static final Registry<Class<? extends io.github.vampirestudios.obsidian.api.bedrock.Component>> BEDROCK_BLOCK_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_components_registry")), Lifecycle.stable());
    public static final Registry<ConvertableOxidizableBlock> CONVERTABLE_OXIDIZABLE_BLOCKS = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "convertable_oxidizable_blocks")), Lifecycle.stable());
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static final Logger BEDROCK_LOGGER = LogManager.getLogger("[" + NAME + ": Bedrock]");
    public static Obsidian INSTANCE;
    public static String VERSION = "0.2.0";
    public static ModConfig config;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "21w11a"));

        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> DumpRegistriesCommand.register(commandDispatcher));

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        //Item Groups
        registerInRegistry(ITEM_GROUP_REGISTRY, "building_blocks", ItemGroup.BUILDING_BLOCKS);
        registerInRegistry(ITEM_GROUP_REGISTRY, "decorations", ItemGroup.DECORATIONS);
        registerInRegistry(ITEM_GROUP_REGISTRY, "redstone", ItemGroup.REDSTONE);
        registerInRegistry(ITEM_GROUP_REGISTRY, "transportation", ItemGroup.TRANSPORTATION);
        registerInRegistry(ITEM_GROUP_REGISTRY, "misc", ItemGroup.MISC);
        registerInRegistry(ITEM_GROUP_REGISTRY, "food", ItemGroup.FOOD);
        registerInRegistry(ITEM_GROUP_REGISTRY, "tools", ItemGroup.TOOLS);
        registerInRegistry(ITEM_GROUP_REGISTRY, "combat", ItemGroup.COMBAT);
        registerInRegistry(ITEM_GROUP_REGISTRY, "brewing", ItemGroup.BREWING);
        registerInRegistry(ITEM_GROUP_REGISTRY, "search", ItemGroup.SEARCH);

        //Entity Components
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "annotation.break_door", BreakDoorAnnotationComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "annotation.open_door", OpenDoorAnnotationComponent.class);

        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "admire_item", AdmireItemComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "agable", AgeableComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "angry", AngryComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "area_attack", AreaAttackComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "attack_cooldown", AttackCooldownComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "barter", BarterComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "block_sensor", BlockSensorComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "boostable", BoostableComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "boss", BossComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "break_blocks", BreakBlocksComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "breathable", BreathableComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "celebrate", CelebrateBehaviourComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "collision_box", CollisionBoxComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "health", HealthComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "movement", MovementComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "namable", NamableComponent.class);

        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "movement.basic", BasicMovementComponent.class);

        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "behavior.panic", PanicBehaviourComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "behavior.tempt", TemptBehaviourComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
        registerInRegistry(ENTITY_COMPONENT_REGISTRY, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);

        //Bedrock Block Events
        registerInRegistry(BEDROCK_BLOCK_EVENT_REGISTRY, "add_mob_effect", AddMobEffect.class);
        registerInRegistry(BEDROCK_BLOCK_EVENT_REGISTRY, "damage", Damage.class);
        registerInRegistry(BEDROCK_BLOCK_EVENT_REGISTRY, "decrement_stack", DecrementStack.class);
        registerInRegistry(BEDROCK_BLOCK_EVENT_REGISTRY, "die", Die.class);
        registerInRegistry(BEDROCK_BLOCK_EVENT_REGISTRY, "play_effect", PlayEffect.class);
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

        registerInRegistry(ADDON_MODULE_REGISTRY, "blocks", new Blocks());
        registerInRegistry(ADDON_MODULE_REGISTRY, "ores", new Ores());
        registerInRegistry(ADDON_MODULE_REGISTRY, "item_group", new ItemGroups());
        registerInRegistry(ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
        registerInRegistry(ADDON_MODULE_REGISTRY, "armor", new Armor());
        registerInRegistry(ADDON_MODULE_REGISTRY, "elytra", new Elytras());
        registerInRegistry(ADDON_MODULE_REGISTRY, "item", new Items());
        registerInRegistry(ADDON_MODULE_REGISTRY, "tool", new Tools());
        registerInRegistry(ADDON_MODULE_REGISTRY, "ranged_weapon", new RangedWeapons());
        registerInRegistry(ADDON_MODULE_REGISTRY, "weapon", new Weapons());
        registerInRegistry(ADDON_MODULE_REGISTRY, "commands", new Commands());
        registerInRegistry(ADDON_MODULE_REGISTRY, "enchantments", new Enchantments());
        registerInRegistry(ADDON_MODULE_REGISTRY, "entities", new Entities());
//        registerInRegistry(ADDON_MODULE_REGISTRY, "shields", new Shields());
        registerInRegistry(ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
        registerInRegistry(ADDON_MODULE_REGISTRY, "food", new Food());
        registerInRegistry(ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
        registerInRegistry(ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());

        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "normal_to_exposed", new ConvertableOxidizableBlock(COPPER_BLOCK, EXPOSED_COPPER));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "exposed_to_weathered", new ConvertableOxidizableBlock(EXPOSED_COPPER, WEATHERED_COPPER));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "weathered_to_oxidized", new ConvertableOxidizableBlock(WEATHERED_COPPER, OXIDIZED_COPPER));

        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_normal_to_cut_exposed", new ConvertableOxidizableBlock(CUT_COPPER, EXPOSED_CUT_COPPER));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_exposed_to_cut_weathered", new ConvertableOxidizableBlock(EXPOSED_CUT_COPPER, WEATHERED_CUT_COPPER));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_weathered_to_cut_oxidized", new ConvertableOxidizableBlock(WEATHERED_CUT_COPPER, OXIDIZED_CUT_COPPER));

        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_normal_slab_to_cut_exposed_slab", new ConvertableOxidizableBlock(CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_SLAB));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_exposed_slab_to_cut_weathered_slab", new ConvertableOxidizableBlock(EXPOSED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_weathered_slab_to_cut_oxidized_slab", new ConvertableOxidizableBlock(WEATHERED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_SLAB));

        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_normal_stairs_to_cut_exposed_stairs", new ConvertableOxidizableBlock(CUT_COPPER_STAIRS, EXPOSED_CUT_COPPER_STAIRS));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_exposed_stairs_to_cut_weathered_stairs", new ConvertableOxidizableBlock(EXPOSED_CUT_COPPER_STAIRS, WEATHERED_CUT_COPPER_STAIRS));
        registerInRegistry(CONVERTABLE_OXIDIZABLE_BLOCKS, "cut_weathered_stairs_to_cut_oxidized_stairs", new ConvertableOxidizableBlock(WEATHERED_CUT_COPPER_STAIRS, OXIDIZED_CUT_COPPER_STAIRS));

        ConfigHelper.loadDefaultObsidianAddons();
        CompletableFuture.runAsync(ConfigHelper::loadObsidianAddons, ConfigHelper.EXECUTOR_SERVICE);
//        BedrockAddonLoader.loadDefaultBedrockAddons();
//        CompletableFuture.runAsync(BedrockAddonLoader::loadBedrockAddons, BedrockAddonLoader.EXECUTOR_SERVICE);
    }

    public <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
        Registry.register(registry, name, idk);
    }

}
