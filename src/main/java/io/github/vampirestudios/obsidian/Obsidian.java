package io.github.vampirestudios.obsidian;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import io.github.vampirestudios.obsidian.api.scripting.ScriptParser;
import io.github.vampirestudios.obsidian.config.ObsidianConfig;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.mixins.PackTypeAccessor;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlockPair;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlocksRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import squeek.appleskin.api.AppleSkinApi;

import java.io.FileNotFoundException;

public class Obsidian implements ModInitializer, AppleSkinApi {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, (SimpleStringDeserializer<?>) ResourceLocation::new)
			.registerTypeAdapter(ModelResourceLocation.class, (SimpleStringDeserializer<ModelResourceLocation>) s -> (ModelResourceLocation) ModelResourceLocation.tryParse(s))
			.setPrettyPrinting()
			.setLenient()
			.create();

	public static final Jankson JANKSON = Jankson.builder()
			.registerDeserializer(String.class, ResourceLocation.class, (s, m) -> new ResourceLocation(s))
			.registerSerializer(ResourceLocation.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, ModelResourceLocation.class, (s, m) -> {
				String[] strings = s.split("#");
				ResourceLocation identifier = ResourceLocation.tryParse(strings[0]);
				assert identifier != null;
				return new ModelResourceLocation(identifier, strings[1]);
			})
			.registerSerializer(ModelResourceLocation.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, Enchantment.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT))
			.registerSerializer(Enchantment.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT))
			.registerDeserializer(String.class, EntityType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE))
			.registerSerializer(EntityType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE))
			.registerDeserializer(String.class, Feature.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.FEATURE))
			.registerSerializer(Feature.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.FEATURE))
			.registerDeserializer(String.class, Fluid.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.FLUID))
			.registerSerializer(Fluid.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.FLUID))
			.registerDeserializer(String.class, Item.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.ITEM))
			.registerSerializer(Item.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.ITEM))
			.registerDeserializer(String.class, CreativeModeTab.class, (s, m) -> lookupDeserialize(s, Registries.ITEM_GROUP_REGISTRY))
			.registerSerializer(CreativeModeTab.class, (s, m) -> lookupSerialize(s, Registries.ITEM_GROUP_REGISTRY))
			.registerDeserializer(String.class, MemoryModuleType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.MEMORY_MODULE_TYPE))
			.registerSerializer(MemoryModuleType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.MEMORY_MODULE_TYPE))
			.registerDeserializer(String.class, PaintingVariant.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.PAINTING_VARIANT))
			.registerSerializer(PaintingVariant.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.PAINTING_VARIANT))
			.registerDeserializer(String.class, ParticleType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE))
			.registerSerializer(ParticleType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE))
			.registerDeserializer(String.class, PoiType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.POINT_OF_INTEREST_TYPE))
			.registerSerializer(PoiType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.POINT_OF_INTEREST_TYPE))
			.registerDeserializer(String.class, Potion.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.POTION))
			.registerSerializer(Potion.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.POTION))
			.registerDeserializer(String.class, RecipeSerializer.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER))
			.registerSerializer(RecipeSerializer.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER))
			.registerDeserializer(String.class, RecipeType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE))
			.registerSerializer(RecipeType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE))
			.registerDeserializer(String.class, Registry.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.REGISTRY))
			.registerSerializer(Registry.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.REGISTRY))
			.registerDeserializer(String.class, Schedule.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.SCHEDULE))
			.registerSerializer(Schedule.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.SCHEDULE))
			.registerDeserializer(String.class, SensorType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.SENSOR_TYPE))
			.registerSerializer(SensorType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.SENSOR_TYPE))
			.registerDeserializer(String.class, SoundEvent.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT))
			.registerSerializer(SoundEvent.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT))
			.registerDeserializer(String.class, StatType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.STAT_TYPE))
			.registerSerializer(StatType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.STAT_TYPE))
			.registerDeserializer(String.class, MobEffect.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT))
			.registerSerializer(MobEffect.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT))
			.registerDeserializer(String.class, StructureType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_TYPE))
			.registerSerializer(StructureType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_TYPE))
			.registerDeserializer(String.class, StructurePieceType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PIECE))
			.registerSerializer(StructurePieceType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PIECE))
			.registerDeserializer(String.class, StructurePoolElementType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_POOL_ELEMENT))
			.registerSerializer(StructurePoolElementType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_POOL_ELEMENT))
			.registerDeserializer(String.class, StructureProcessorType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PROCESSOR))
			.registerSerializer(StructureProcessorType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PROCESSOR))
			.registerDeserializer(String.class, VillagerProfession.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION))
			.registerSerializer(VillagerProfession.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION))
			.registerDeserializer(String.class, VillagerType.class, (s, m) -> lookupDeserialize(s, net.minecraft.core.registries.BuiltInRegistries.VILLAGER_TYPE))
			.registerSerializer(VillagerType.class, (s, m) -> lookupSerialize(s, net.minecraft.core.registries.BuiltInRegistries.VILLAGER_TYPE))
			.build();
	public static final Logger LOGGER = LogManager.getLogger(Const.MOD_NAME);
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger(Const.MOD_NAME + " | Bedrock");
	public static final EntityType<SeatEntity> SEAT = Registry.register(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE, Const.id("seat"), FabricEntityTypeBuilder.
			<SeatEntity>create(MobCategory.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
			.build());
	public static ObsidianConfig CONFIG;

	public static PackSource RESOURCE_PACK_SOURCE = PackSource.create(text -> Component.translatable("pack.source.obsidian"), false);
	public static KeyMapping binding = new KeyMapping("key.uwu.hud_test", GLFW.GLFW_KEY_J, "misc");

	public static ResourceLocation id(String path) {
		return Const.id(path);
	}

	public static <T> T registerInRegistryVanilla(Registry<T> registry, String name, T idk) {
		return Registry.register(registry, name, idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
		registerInRegistry(registry, Const.id(name), idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, ResourceLocation name, T idk) {
		Registry.register(registry, name, idk);
	}

	/*public static void registerDataPack(Identifier id, Processor<ArtificeResourcePack.ServerResourcePackBuilder> register) {
		if (!ArtificeRegistry.DATA.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.DATA, id, new DynamicResourcePackFactory<>(ResourceType.SERVER_DATA, id, register));
	}

	@Environment(EnvType.CLIENT)
	public static void registerAssetPack(Identifier id, Processor<ArtificeResourcePack.ClientResourcePackBuilder> register) {
		if (!ArtificeRegistry.ASSETS.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.ASSETS, id, new DynamicResourcePackFactory<>(ResourceType.CLIENT_RESOURCES, id, register));
	}*/

	private static <T> T lookupDeserialize(String s, Registry<T> registry) {
		return registry.get(new ResourceLocation(s));
	}

	private static <T, U extends T> JsonElement lookupSerialize(T t, Registry<U> registry) {
		@SuppressWarnings("unchecked") //Widening cast happening because of generic type parameters in the registry class
		ResourceLocation id = registry.getKey((U) t);
		if (id == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(id.toString());
	}

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("You're now running Obsidian v%s for %s", Const.MOD_VERSION, SharedConstants.getCurrentVersion().getName()));
		AutoConfig.register(ObsidianConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ObsidianConfig.class).getConfig();
		KeyBindingHelper.registerKeyBinding(binding);

		PackType type = PackTypeAccessor.createPackType("content");
		ResourceManagerHelper.get(type)
				.registerReloadListener(new ScriptParser());

//		GlobalFixer.init();

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			if (destination.dimensionTypeId().equals(BuiltinDimensionTypes.END)) {
				MinecraftServer server = destination.getServer();
				Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation("I don't know, probably something like `the_end/kill_dragon`"));
				if(!server.getPlayerList().getPlayerAdvancements(player).getOrStartProgress(advancement).isDone()) {

				}
			}
		});

		//Item Groups
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "building_blocks", CreativeModeTabs.BUILDING_BLOCKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "colored_blocks", CreativeModeTabs.COLORED_BLOCKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "natural", CreativeModeTabs.NATURAL_BLOCKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "functional", CreativeModeTabs.FUNCTIONAL_BLOCKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "redstone", CreativeModeTabs.REDSTONE_BLOCKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "search", CreativeModeTabs.SEARCH);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "tools", CreativeModeTabs.TOOLS_AND_UTILITIES);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "combat", CreativeModeTabs.COMBAT);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "food_and_drink", CreativeModeTabs.FOOD_AND_DRINKS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "ingredients", CreativeModeTabs.INGREDIENTS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "spawn_eggs", CreativeModeTabs.SPAWN_EGGS);
//		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "operator", CreativeModeTabs.OP_BLOCKS);

		//Entity Components
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "annotation.break_door", BreakDoorAnnotationComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "annotation.open_door", OpenDoorAnnotationComponent.class);

		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "admire_item", AdmireItemComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "ageable", AgeableComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "angry", AngryComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "area_attack", AreaAttackComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "attack_cooldown", AttackCooldownComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "barter", BarterComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "block_sensor", BlockSensorComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "boostable", BoostableComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "boss", BossComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "break_blocks", BreakBlocksComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "breathable", BreathableComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "celebrate", CelebrateBehaviourComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "collision_box", CollisionBoxComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "health", HealthComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "movement", MovementComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "nameable", NameableComponent.class);

		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "movement.basic", BasicMovementComponent.class);

		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "behavior.panic", PanicBehaviourComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "behavior.tempt", TemptBehaviourComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
		registerInRegistryVanilla(Registries.ENTITY_COMPONENT_REGISTRY, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);

		//Bedrock Block Events
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "add_mob_effect", AddMobEffect.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "damage", Damage.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "decrement_stack", DecrementStack.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "die", Die.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_effect", PlayEffect.class);
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
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "add_mob_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.AddMobEffect.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "damage", io.github.vampirestudios.obsidian.api.obsidian.block.events.Damage.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "decrement_stack", io.github.vampirestudios.obsidian.api.obsidian.block.events.DecrementStack.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "die", io.github.vampirestudios.obsidian.api.obsidian.block.events.Die.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "play_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.PlayEffect.class);

//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_sound", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "remove_mob_effect", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "run_command", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_at_pos", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_property", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "spawn_loot", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "swing", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "teleport", LookAtPlayerBehaviourComponent.class);
//      	registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "transform_item", LookAtPlayerBehaviourComponent.class);

		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_group", new LegacyItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "creative_tab", new CreativeTabs());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_sound_groups", new BlockSoundGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_materials", new BlockMaterials());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "blocks", new Blocks());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "ores", new Ores());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "paintings", new Paintings());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_materials", new ArmorMaterials());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "emojis", new Emojis());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item", new Items());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tool", new Tools());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "particle", new Particles());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sound_events", new SoundEvents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "music_discs", new MusicDiscs());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "ranged_weapon", new RangedWeapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "weapon", new Weapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "commands", new Commands());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "enchantments", new Enchantments());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "entities", new Entities());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "shields", new Shields());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food_components", new FoodComponents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food", new Food());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "fuel_sources", new FuelSources());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "condensed_item_entries", new CondensedItemEntries());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "biome_layouts", new BiomeLayouts());
//		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
//			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "guis", new Guis());
//		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
//			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "huds", new Huds());

		for (Block block : ContentRegistries.BLOCKS) {
			if (block.additional_information.isConvertible) {
				AdditionalBlockInformation.Convertible convertible = block.additional_information.convertible;
				net.minecraft.world.level.block.Block parentBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(convertible.parent_block);
				net.minecraft.world.level.block.Block transformedBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(convertible.transformed_block);
				AdditionalBlockInformation.Convertible.ConversionItem conversionItem = convertible.conversionItem;
				Item conversionItemItem;
				if (conversionItem.item != null) conversionItemItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(conversionItem.item);
				else conversionItemItem = null;

				TagKey<Item> conversionItemTag = null;
				if (conversionItem.tag != null) conversionItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);

				Item reversalItemItem = null;
				TagKey<Item> reversalItemTag = null;
				AdditionalBlockInformation.Convertible.ConversionItem reversalItem = null;
				if (convertible.reversible) {
					if (convertible.reversalItem != null) reversalItem = convertible.reversalItem;

					if (reversalItem != null) {
						if (reversalItem.item != null) reversalItemItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(conversionItem.item);
						if (reversalItem.tag != null)
							reversalItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);
					}
				}

				SoundEvent sound;
				if (convertible.sound != null) sound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(convertible.sound);
				else sound = null;

				Item droppedItem;
				if (convertible.dropped_item != null) droppedItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(convertible.dropped_item);
				else droppedItem = null;

				ConvertibleBlockPair.ConversionItem reversalItem1;
				if (reversalItem != null)
					reversalItem1 = new ConvertibleBlockPair.ConversionItem(reversalItemTag, reversalItemItem);
				else reversalItem1 = null;

				ConvertibleBlockPair.ConversionItem conversionItem1 = new ConvertibleBlockPair.ConversionItem(conversionItemTag, conversionItemItem);
				ConvertibleBlockPair convertibleBlockPair;
				if (reversalItem1 != null)
					convertibleBlockPair = new ConvertibleBlockPair(parentBlock, transformedBlock,
							conversionItem1, reversalItem1);
				else convertibleBlockPair = new ConvertibleBlockPair(parentBlock, transformedBlock, conversionItem1);
				if (sound != null) convertibleBlockPair.setSound(sound);
				if (droppedItem != null) convertibleBlockPair.setDroppedItem(droppedItem);
				ConvertibleBlocksRegistry.registerConvertibleBlockPair(convertibleBlockPair);
			}
		}

		//Add new spread behavior, mycelium spreads onto coal blocks by turning them into diamond blocks
//		SpreadBehaviors.addComplexSpreaderBehavior(net.minecraft.block.Blocks.COAL_BLOCK, SpreaderType.MYCELIUM, ((state, level, pos) -> net.minecraft.block.Blocks.DIAMOND_BLOCK.getDefaultState()));
//		SpreadBehaviors.addComplexSpreaderBehavior(net.minecraft.block.Blocks.DIAMOND_BLOCK, SpreaderType.MYCELIUM, ((state, level, pos) -> net.minecraft.block.Blocks.DIAMOND_BLOCK.getDefaultState()));

		//Replace vanilla behavior, grass will spread to dirt by turning it into moss blocks
//		SpreadBehaviors.addComplexSpreaderBehavior(net.minecraft.block.Blocks.DIRT, SpreaderType.GRASS, ((state, level, pos) -> net.minecraft.block.Blocks.MOSS_BLOCK.getDefaultState()));
//		SpreadBehaviors.addComplexSpreaderBehavior(net.minecraft.block.Blocks.MOSS_BLOCK, SpreaderType.GRASS, ((state, level, pos) -> net.minecraft.block.Blocks.MOSS_BLOCK.getDefaultState()));

		ObsidianAddonLoader.loadDefaultObsidianAddons();
		ObsidianAddonLoader.loadObsidianAddons();

		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "bedrock_blocks", new io.github.vampirestudios.obsidian.addon_modules.bedrock.Blocks());

		BedrockAddonLoader.loadDefaultBedrockAddons();
		BedrockAddonLoader.loadBedrockAddons();

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClientSide)
				return InteractionResult.PASS;

			if (!world.mayInteract(player, hit.getBlockPos()))
				return InteractionResult.PASS;

			net.minecraft.world.level.block.Block b = world.getBlockState(hit.getBlockPos()).getBlock();

			if ((b instanceof SittableBlock || b instanceof HorizontalFacingSittableBlock || b instanceof SittableAndDyableBlock || b instanceof HorizontalFacingSittableAndDyableBlock) && !SeatEntity.OCCUPIED.containsKey(new Vec3(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ())) && player.getItemInHand(hand).isEmpty()) {
				Vec3 comparePos = new Vec3(player.blockPosition().getX() + 0.5D, player.blockPosition().getY() + 1.25D, player.blockPosition().getZ() + 0.5D);

				//only allow sitting when right-clicking the top face of a block, and disallow sitting players from sitting again
				if (SeatEntity.OCCUPIED.containsKey(comparePos))
					return InteractionResult.PASS;

				SeatEntity sit = Obsidian.SEAT.create(world);
				Vec3 vec3d = new Vec3(hit.getBlockPos().getX() + 0.5D, hit.getBlockPos().getY() + 0.25D, hit.getBlockPos().getZ() + 0.5D);

				SeatEntity.OCCUPIED.put(vec3d, player.blockPosition());
				assert sit != null;
				sit.absMoveTo(vec3d.x(), vec3d.y(), vec3d.z());
				world.addFreshEntity(sit);
				player.startRiding(sit);
				return InteractionResult.SUCCESS;
			}

			return InteractionResult.PASS;
		});
	}

	/*@Override
	public void onMealApiInit() {
//		Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> {
//			try {
//				addonModule.initMealApi();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		});
	}*/

	@Override
	public void registerEvents() {
		Registries.ADDON_MODULE_REGISTRY.forEach(addonModule -> {
			try {
				addonModule.initAppleSkin();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

}
