package io.github.vampirestudios.obsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Lifecycle;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.Processor;
import com.swordglowsblue.artifice.common.ArtificeRegistry;
import com.swordglowsblue.artifice.common.ClientResourcePackProfileLike;
import com.swordglowsblue.artifice.common.ServerResourcePackProfileLike;
import com.swordglowsblue.artifice.impl.ArtificeImpl;
import com.swordglowsblue.artifice.impl.DynamicResourcePackFactory;
import io.github.foundationgames.mealapi.api.v0.MealAPIInitializer;
import io.github.vampirestudios.obsidian.addonModules.*;
import io.github.vampirestudios.obsidian.api.bedrock.block.events.*;
import io.github.vampirestudios.obsidian.api.dataexchange.DataExchangeAPI;
import io.github.vampirestudios.obsidian.api.dataexchange.handler.HelloClient;
import io.github.vampirestudios.obsidian.api.dataexchange.handler.HelloServer;
import io.github.vampirestudios.obsidian.api.dataexchange.handler.RequestFiles;
import io.github.vampirestudios.obsidian.api.dataexchange.handler.SendFiles;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import io.github.vampirestudios.obsidian.commands.DumpRegistriesCommand;
import io.github.vampirestudios.obsidian.config.ObsidianConfig;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.appleskin.api.AppleSkinApi;

import java.io.FileNotFoundException;
import java.util.List;

public class Obsidian implements ModInitializer, MealAPIInitializer, AppleSkinApi {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
			.registerTypeAdapter(ModelIdentifier.class, (SimpleStringDeserializer<?>) ModelIdentifier::new)
			.setPrettyPrinting()
			.setLenient()
			.create();
	public static String MOD_ID = "obsidian";
	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY = FabricRegistryBuilder.createSimple(AddonModule.class, id("addon_modules")).buildAndRegister();
	public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = FabricRegistryBuilder.createSimple(ItemGroup.class, new Identifier(MOD_ID, "item_groups")).buildAndRegister();
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "entity_components")), Lifecycle.stable());
	public static final Registry<Class<? extends io.github.vampirestudios.obsidian.api.bedrock.block.Event>> BEDROCK_BLOCK_EVENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_event_registry")), Lifecycle.stable());
	public static final Registry<Class<? extends io.github.vampirestudios.obsidian.api.bedrock.Component>> BEDROCK_BLOCK_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "bedrock_block_components_registry")), Lifecycle.stable());
	public static final Registry<Class<? extends Event>> BLOCK_EVENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "block_event_registry")), Lifecycle.stable());
	public static String NAME = "Obsidian";
	public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger("[" + NAME + ": Bedrock]");
	public static Obsidian INSTANCE;
	public static String VERSION = "0.6.1-alpha";
	public static ObsidianConfig CONFIG;
	public static EntityType<SeatEntity> SEAT;

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static <T> void registerInRegistryVanilla(Registry<T> registry, String name, T idk) {
		Registry.register(registry, name, idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
		Registry.register(registry, new Identifier(MOD_ID, name), idk);
	}

	public static ServerResourcePackProfileLike registerDataPack(Identifier id, Processor<ArtificeResourcePack.ServerResourcePackBuilder> register) {
		if (ArtificeRegistry.DATA_PACKS.containsId(id)) return ArtificeRegistry.DATA_PACKS.get(id);
		else
			return ArtificeImpl.registerSafely(ArtificeRegistry.DATA_PACKS, id, new DynamicResourcePackFactory<>(ResourceType.SERVER_DATA, id, register));
	}

	@Environment(EnvType.CLIENT)
	public static ClientResourcePackProfileLike registerAssetPack(Identifier id, Processor<ArtificeResourcePack.ClientResourcePackBuilder> register) {
		if (ArtificeRegistry.RESOURCE_PACKS.containsId(id)) return ArtificeRegistry.RESOURCE_PACKS.get(id);
		else
			return ArtificeImpl.registerSafely(ArtificeRegistry.RESOURCE_PACKS, id, new DynamicResourcePackFactory<>(ResourceType.CLIENT_RESOURCES, id, register));
	}

	@Override
	public void onInitialize() {
		INSTANCE = this;
		LOGGER.info(String.format("You're now running Obsidian v%s for 1.17.1", VERSION));
		AutoConfig.register(ObsidianConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ObsidianConfig.class).getConfig();

		CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> DumpRegistriesCommand.register(commandDispatcher));

		SEAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "seat"), FabricEntityTypeBuilder.
				<SeatEntity>create(SpawnGroup.MISC, SeatEntity::new)
				.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
				.build());

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

		//Obsidian Block Events
		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "add_mob_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.AddMobEffect.class);
		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "damage", io.github.vampirestudios.obsidian.api.obsidian.block.events.Damage.class);
		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "decrement_stack", io.github.vampirestudios.obsidian.api.obsidian.block.events.DecrementStack.class);
		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "die", io.github.vampirestudios.obsidian.api.obsidian.block.events.Die.class);
		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "play_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.PlayEffect.class);
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
		registerInRegistry(ADDON_MODULE_REGISTRY, "paintings", new Paintings());
		registerInRegistry(ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		registerInRegistry(ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
		registerInRegistry(ADDON_MODULE_REGISTRY, "item", new Items());
		registerInRegistry(ADDON_MODULE_REGISTRY, "tool", new Tools());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
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
		registerInRegistry(ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());

		ObsidianAddonLoader.loadDefaultObsidianAddons();
		ObsidianAddonLoader.loadObsidianAddons();

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClient)
				return ActionResult.PASS;

			if (!world.canPlayerModifyAt(player, hit.getBlockPos()))
				return ActionResult.PASS;

			BlockState s = world.getBlockState(hit.getBlockPos());
			net.minecraft.block.Block b = world.getBlockState(hit.getBlockPos()).getBlock();

			if ((b instanceof SittableBlock || b instanceof HorizontalFacingSittableBlock || b instanceof SittableAndDyableBlock || b instanceof HorizontalFacingSittableAndDyableBlock) && !SeatEntity.OCCUPIED.containsKey(new Vec3d(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ())) && player.getStackInHand(hand).isEmpty()) {
				Vec3d comparePos = new Vec3d(player.getBlockPos().getX() + 0.5D, player.getBlockPos().getY() + 1.25D, player.getBlockPos().getZ() + 0.5D);

				//only allow sitting when rightclicking the top face of a block, and disallow sitting players from sitting again
				if (SeatEntity.OCCUPIED.containsKey(comparePos))
					return ActionResult.PASS;

				SeatEntity sit = Obsidian.SEAT.create(world);
				Vec3d vec3d = new Vec3d(hit.getBlockPos().getX() + 0.5D, hit.getBlockPos().getY() + 0.25D, hit.getBlockPos().getZ() + 0.5D);

				SeatEntity.OCCUPIED.put(vec3d, player.getBlockPos());
				assert sit != null;
				sit.updatePosition(vec3d.getX(), vec3d.getY(), vec3d.getZ());
				world.spawnEntity(sit);
				player.startRiding(sit);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});

		DataExchangeAPI.registerDescriptors(List.of(
				HelloClient.DESCRIPTOR,
				HelloServer.DESCRIPTOR,
				RequestFiles.DESCRIPTOR,
				SendFiles.DESCRIPTOR
		));
	}

	@Override
	public void onMealApiInit() {
//		Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> {
//			try {
//				addonModule.initMealApi();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		});
	}

	@Override
	public void registerEvents() {
		Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> {
			try {
				addonModule.initAppleSkin();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

}
