package io.github.vampirestudios.obsidian;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.util.Processor;
import io.github.vampirestudios.artifice.common.ArtificeRegistry;
import io.github.vampirestudios.artifice.impl.ArtificeImpl;
import io.github.vampirestudios.artifice.impl.DynamicResourcePackFactory;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import io.github.vampirestudios.obsidian.config.ObsidianConfig;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlockPair;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlocksRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePackSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.piece.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import squeek.appleskin.api.AppleSkinApi;

import java.io.FileNotFoundException;

public class Obsidian implements ModInitializer, AppleSkinApi {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
			.registerTypeAdapter(ModelIdentifier.class, (SimpleStringDeserializer<?>) ModelIdentifier::new)
			.setPrettyPrinting()
			.setLenient()
			.create();
	public static final Jankson JANKSON = Jankson.builder()
			.registerDeserializer(String.class, Identifier.class, (s, m) -> new Identifier(s))
			.registerSerializer(Identifier.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, ModelIdentifier.class, (s, m) -> {
				String[] strings = s.split("#");
				Identifier identifier = Identifier.tryParse(strings[0]);
				assert identifier != null;
				return new ModelIdentifier(identifier, strings[1]);
			})
			.registerSerializer(ModelIdentifier.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, Enchantment.class, (s, m) -> lookupDeserialize(s, Registry.ENCHANTMENT))
			.registerSerializer(Enchantment.class, (s, m) -> lookupSerialize(s, Registry.ENCHANTMENT))
			.registerDeserializer(String.class, EntityType.class, (s, m) -> lookupDeserialize(s, Registry.ENTITY_TYPE))
			.registerSerializer(EntityType.class, (s, m) -> lookupSerialize(s, Registry.ENTITY_TYPE))
			.registerDeserializer(String.class, Feature.class, (s, m) -> lookupDeserialize(s, Registry.FEATURE))
			.registerSerializer(Feature.class, (s, m) -> lookupSerialize(s, Registry.FEATURE))
			.registerDeserializer(String.class, Fluid.class, (s, m) -> lookupDeserialize(s, Registry.FLUID))
			.registerSerializer(Fluid.class, (s, m) -> lookupSerialize(s, Registry.FLUID))
			.registerDeserializer(String.class, Item.class, (s, m) -> lookupDeserialize(s, Registry.ITEM))
			.registerSerializer(Item.class, (s, m) -> lookupSerialize(s, Registry.ITEM))
			.registerDeserializer(String.class, ItemGroup.class, (s, m) -> lookupDeserialize(s, Registries.ITEM_GROUP_REGISTRY))
			.registerSerializer(ItemGroup.class, (s, m) -> lookupSerialize(s, Registries.ITEM_GROUP_REGISTRY))
			.registerDeserializer(String.class, MemoryModuleType.class, (s, m) -> lookupDeserialize(s, Registry.MEMORY_MODULE_TYPE))
			.registerSerializer(MemoryModuleType.class, (s, m) -> lookupSerialize(s, Registry.MEMORY_MODULE_TYPE))
			.registerDeserializer(String.class, PaintingVariant.class, (s, m) -> lookupDeserialize(s, Registry.PAINTING_VARIANT))
			.registerSerializer(PaintingVariant.class, (s, m) -> lookupSerialize(s, Registry.PAINTING_VARIANT))
			.registerDeserializer(String.class, ParticleType.class, (s, m) -> lookupDeserialize(s, Registry.PARTICLE_TYPE))
			.registerSerializer(ParticleType.class, (s, m) -> lookupSerialize(s, Registry.PARTICLE_TYPE))
			.registerDeserializer(String.class, PointOfInterestType.class, (s, m) -> lookupDeserialize(s, Registry.POINT_OF_INTEREST_TYPE))
			.registerSerializer(PointOfInterestType.class, (s, m) -> lookupSerialize(s, Registry.POINT_OF_INTEREST_TYPE))
			.registerDeserializer(String.class, Potion.class, (s, m) -> lookupDeserialize(s, Registry.POTION))
			.registerSerializer(Potion.class, (s, m) -> lookupSerialize(s, Registry.POTION))
			.registerDeserializer(String.class, RecipeSerializer.class, (s, m) -> lookupDeserialize(s, Registry.RECIPE_SERIALIZER))
			.registerSerializer(RecipeSerializer.class, (s, m) -> lookupSerialize(s, Registry.RECIPE_SERIALIZER))
			.registerDeserializer(String.class, RecipeType.class, (s, m) -> lookupDeserialize(s, Registry.RECIPE_TYPE))
			.registerSerializer(RecipeType.class, (s, m) -> lookupSerialize(s, Registry.RECIPE_TYPE))
			.registerDeserializer(String.class, Registry.class, (s, m) -> lookupDeserialize(s, Registry.REGISTRIES))
			.registerSerializer(Registry.class, (s, m) -> lookupSerialize(s, Registry.REGISTRIES))
			.registerDeserializer(String.class, Schedule.class, (s, m) -> lookupDeserialize(s, Registry.SCHEDULE))
			.registerSerializer(Schedule.class, (s, m) -> lookupSerialize(s, Registry.SCHEDULE))
			.registerDeserializer(String.class, SensorType.class, (s, m) -> lookupDeserialize(s, Registry.SENSOR_TYPE))
			.registerSerializer(SensorType.class, (s, m) -> lookupSerialize(s, Registry.SENSOR_TYPE))
			.registerDeserializer(String.class, SoundEvent.class, (s, m) -> lookupDeserialize(s, Registry.SOUND_EVENT))
			.registerSerializer(SoundEvent.class, (s, m) -> lookupSerialize(s, Registry.SOUND_EVENT))
			.registerDeserializer(String.class, StatType.class, (s, m) -> lookupDeserialize(s, Registry.STAT_TYPE))
			.registerSerializer(StatType.class, (s, m) -> lookupSerialize(s, Registry.STAT_TYPE))
			.registerDeserializer(String.class, StatusEffect.class, (s, m) -> lookupDeserialize(s, Registry.STATUS_EFFECT))
			.registerSerializer(StatusEffect.class, (s, m) -> lookupSerialize(s, Registry.STATUS_EFFECT))
			.registerDeserializer(String.class, StructureType.class, (s, m) -> lookupDeserialize(s, Registry.STRUCTURE_TYPE))
			.registerSerializer(StructureType.class, (s, m) -> lookupSerialize(s, Registry.STRUCTURE_TYPE))
			.registerDeserializer(String.class, StructurePieceType.class, (s, m) -> lookupDeserialize(s, Registry.STRUCTURE_PIECE))
			.registerSerializer(StructurePieceType.class, (s, m) -> lookupSerialize(s, Registry.STRUCTURE_PIECE))
			.registerDeserializer(String.class, StructurePoolElementType.class, (s, m) -> lookupDeserialize(s, Registry.STRUCTURE_POOL_ELEMENT))
			.registerSerializer(StructurePoolElementType.class, (s, m) -> lookupSerialize(s, Registry.STRUCTURE_POOL_ELEMENT))
			.registerDeserializer(String.class, StructureProcessorType.class, (s, m) -> lookupDeserialize(s, Registry.STRUCTURE_PROCESSOR))
			.registerSerializer(StructureProcessorType.class, (s, m) -> lookupSerialize(s, Registry.STRUCTURE_PROCESSOR))
			.registerDeserializer(String.class, VillagerProfession.class, (s, m) -> lookupDeserialize(s, Registry.VILLAGER_PROFESSION))
			.registerSerializer(VillagerProfession.class, (s, m) -> lookupSerialize(s, Registry.VILLAGER_PROFESSION))
			.registerDeserializer(String.class, VillagerType.class, (s, m) -> lookupDeserialize(s, Registry.VILLAGER_TYPE))
			.registerSerializer(VillagerType.class, (s, m) -> lookupSerialize(s, Registry.VILLAGER_TYPE))
			.build();
	public static final Logger LOGGER = LogManager.getLogger(Const.MOD_NAME);
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger(Const.MOD_NAME + " | Bedrock");
	public static final EntityType<SeatEntity> SEAT = Registry.register(Registry.ENTITY_TYPE, Const.id("seat"), FabricEntityTypeBuilder.
			<SeatEntity>create(SpawnGroup.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
			.build());
	public static ObsidianConfig CONFIG;

	public static ResourcePackSource RESOURCE_PACK_SOURCE = ResourcePackSource.nameAndSource("pack.source.obsidian");
	public static KeyBind binding = new KeyBind("key.uwu.hud_test", GLFW.GLFW_KEY_J, "misc");

	public static Identifier id(String path) {
		return Const.id(path);
	}

	public static <T> T registerInRegistryVanilla(Registry<T> registry, String name, T idk) {
		return Registry.register(registry, name, idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
		registerInRegistry(registry, Const.id(name), idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, Identifier name, T idk) {
		Registry.register(registry, name, idk);
	}

	public static void registerDataPack(Identifier id, Processor<ArtificeResourcePack.ServerResourcePackBuilder> register) {
		if (!ArtificeRegistry.DATA.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.DATA, id, new DynamicResourcePackFactory<>(ResourceType.SERVER_DATA, id, register));
	}

	@Environment(EnvType.CLIENT)
	public static void registerAssetPack(Identifier id, Processor<ArtificeResourcePack.ClientResourcePackBuilder> register) {
		if (!ArtificeRegistry.ASSETS.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.ASSETS, id, new DynamicResourcePackFactory<>(ResourceType.CLIENT_RESOURCES, id, register));
	}

	private static <T> T lookupDeserialize(String s, Registry<T> registry) {
		return registry.get(new Identifier(s));
	}

	private static <T, U extends T> JsonElement lookupSerialize(T t, Registry<U> registry) {
		@SuppressWarnings("unchecked") //Widening cast happening because of generic type parameters in the registry class
		Identifier id = registry.getId((U) t);
		if (id == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(id.toString());
	}

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("You're now running Obsidian v%s for %s", Const.MOD_VERSION, SharedConstants.getGameVersion().getName()));
		AutoConfig.register(ObsidianConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ObsidianConfig.class).getConfig();
		KeyBindingHelper.registerKeyBinding(binding);

		//Item Groups
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "building_blocks", ItemGroup.BUILDING_BLOCKS);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "decorations", ItemGroup.DECORATIONS);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "redstone", ItemGroup.REDSTONE);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "transportation", ItemGroup.TRANSPORTATION);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "misc", ItemGroup.MISC);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "food", ItemGroup.FOOD);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "tools", ItemGroup.TOOLS);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "combat", ItemGroup.COMBAT);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "brewing", ItemGroup.BREWING);
		registerInRegistryVanilla(Registries.ITEM_GROUP_REGISTRY, "search", ItemGroup.SEARCH);

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
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "emojis", new Emojis());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item", new Items());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tool", new Tools());
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
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
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "condensed_item_entries", new CondensedItemEntries());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "biome_layouts", new BiomeLayouts());
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "guis", new Guis());
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "huds", new Huds());

		for (Block block : ContentRegistries.BLOCKS) {
			if (block.additional_information.isConvertible) {
				AdditionalBlockInformation.Convertible convertible = block.additional_information.convertible;
				net.minecraft.block.Block parentBlock = Registry.BLOCK.get(convertible.parent_block);
				net.minecraft.block.Block transformedBlock = Registry.BLOCK.get(convertible.transformed_block);
				AdditionalBlockInformation.Convertible.ConversionItem conversionItem = convertible.conversionItem;
				Item conversionItemItem;
				if (conversionItem.item != null) conversionItemItem = Registry.ITEM.get(conversionItem.item);
				else conversionItemItem = null;

				TagKey<Item> conversionItemTag = null;
				if (conversionItem.tag != null) conversionItemTag = TagKey.of(Registry.ITEM_KEY, conversionItem.tag);

				Item reversalItemItem = null;
				TagKey<Item> reversalItemTag = null;
				AdditionalBlockInformation.Convertible.ConversionItem reversalItem = null;
				if (convertible.reversible) {
					if (convertible.reversalItem != null) reversalItem = convertible.reversalItem;

					if (reversalItem != null) {
						if (reversalItem.item != null) reversalItemItem = Registry.ITEM.get(conversionItem.item);
						if (reversalItem.tag != null)
							reversalItemTag = TagKey.of(Registry.ITEM_KEY, conversionItem.tag);
					}
				}

				SoundEvent sound;
				if (convertible.sound != null) sound = Registry.SOUND_EVENT.get(convertible.sound);
				else sound = null;

				Item droppedItem;
				if (convertible.dropped_item != null) droppedItem = Registry.ITEM.get(convertible.dropped_item);
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

		BedrockAddonLoader.loadDefaultBedrockAddons();
		BedrockAddonLoader.loadBedrockAddons();

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClient)
				return ActionResult.PASS;

			if (!world.canPlayerModifyAt(player, hit.getBlockPos()))
				return ActionResult.PASS;

			net.minecraft.block.Block b = world.getBlockState(hit.getBlockPos()).getBlock();

			if ((b instanceof SittableBlock || b instanceof HorizontalFacingSittableBlock || b instanceof SittableAndDyableBlock || b instanceof HorizontalFacingSittableAndDyableBlock) && !SeatEntity.OCCUPIED.containsKey(new Vec3d(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ())) && player.getStackInHand(hand).isEmpty()) {
				Vec3d comparePos = new Vec3d(player.getBlockPos().getX() + 0.5D, player.getBlockPos().getY() + 1.25D, player.getBlockPos().getZ() + 0.5D);

				//only allow sitting when right-clicking the top face of a block, and disallow sitting players from sitting again
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
