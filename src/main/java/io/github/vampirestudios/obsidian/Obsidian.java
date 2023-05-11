package io.github.vampirestudios.obsidian;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.api.ThingResourceManager;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.parsers.BlockSetTypeParser;
import io.github.vampirestudios.obsidian.api.parsers.ShapeParser;
import io.github.vampirestudios.obsidian.api.scripting.ScriptParser;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.ModIdArgument;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.mixins.PackTypeAccessor;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlockPair;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlocksRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.renderer.debug.LightSectionDebugRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LightChunkGetter;
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

public class Obsidian implements ModInitializer {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, (SimpleStringDeserializer<?>) ResourceLocation::new)
			.registerTypeAdapter(ModelResourceLocation.class, (SimpleStringDeserializer<ModelResourceLocation>) s -> (ModelResourceLocation) ModelResourceLocation.tryParse(s))
			.setPrettyPrinting()
			.setLenient()
			.create();

	public static final Jankson JANKSON = JanksonFactory.builder()
			.registerDeserializer(String.class, ResourceLocation.class, (s, m) -> new ResourceLocation(s))
			.registerSerializer(ResourceLocation.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, ModelResourceLocation.class, (s, m) -> {
				String[] strings = s.split("#");
				ResourceLocation identifier = ResourceLocation.tryParse(strings[0]);
				assert identifier != null;
				return new ModelResourceLocation(identifier, strings[1]);
			})
			.registerSerializer(ModelResourceLocation.class, (i, m) -> new JsonPrimitive(i.toString()))
			.registerDeserializer(String.class, Enchantment.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.ENCHANTMENT))
			.registerSerializer(Enchantment.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.ENCHANTMENT))
			.registerDeserializer(String.class, EntityType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.ENTITY_TYPE))
			.registerSerializer(EntityType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.ENTITY_TYPE))
			.registerDeserializer(String.class, Feature.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.FEATURE))
			.registerSerializer(Feature.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.FEATURE))
			.registerDeserializer(String.class, Fluid.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.FLUID))
			.registerSerializer(Fluid.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.FLUID))
			.registerDeserializer(String.class, Item.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.ITEM))
			.registerSerializer(Item.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.ITEM))
			.registerDeserializer(String.class, CreativeModeTab.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.CREATIVE_MODE_TAB))
			.registerSerializer(CreativeModeTab.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.CREATIVE_MODE_TAB))
			.registerDeserializer(String.class, MemoryModuleType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.MEMORY_MODULE_TYPE))
			.registerSerializer(MemoryModuleType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.MEMORY_MODULE_TYPE))
			.registerDeserializer(String.class, PaintingVariant.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.PAINTING_VARIANT))
			.registerSerializer(PaintingVariant.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.PAINTING_VARIANT))
			.registerDeserializer(String.class, ParticleType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.PARTICLE_TYPE))
			.registerSerializer(ParticleType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.PARTICLE_TYPE))
			.registerDeserializer(String.class, PoiType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.POINT_OF_INTEREST_TYPE))
			.registerSerializer(PoiType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.POINT_OF_INTEREST_TYPE))
			.registerDeserializer(String.class, Potion.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.POTION))
			.registerSerializer(Potion.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.POTION))
			.registerDeserializer(String.class, RecipeSerializer.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.RECIPE_SERIALIZER))
			.registerSerializer(RecipeSerializer.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.RECIPE_SERIALIZER))
			.registerDeserializer(String.class, RecipeType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.RECIPE_TYPE))
			.registerSerializer(RecipeType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.RECIPE_TYPE))
			.registerDeserializer(String.class, Registry.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.REGISTRY))
			.registerSerializer(Registry.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.REGISTRY))
			.registerDeserializer(String.class, Schedule.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.SCHEDULE))
			.registerSerializer(Schedule.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.SCHEDULE))
			.registerDeserializer(String.class, SensorType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.SENSOR_TYPE))
			.registerSerializer(SensorType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.SENSOR_TYPE))
			.registerDeserializer(String.class, SoundEvent.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.SOUND_EVENT))
			.registerSerializer(SoundEvent.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.SOUND_EVENT))
			.registerDeserializer(String.class, StatType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.STAT_TYPE))
			.registerSerializer(StatType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.STAT_TYPE))
			.registerDeserializer(String.class, MobEffect.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.MOB_EFFECT))
			.registerSerializer(MobEffect.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.MOB_EFFECT))
			.registerDeserializer(String.class, StructureType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.STRUCTURE_TYPE))
			.registerSerializer(StructureType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.STRUCTURE_TYPE))
			.registerDeserializer(String.class, StructurePieceType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.STRUCTURE_PIECE))
			.registerSerializer(StructurePieceType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.STRUCTURE_PIECE))
			.registerDeserializer(String.class, StructurePoolElementType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.STRUCTURE_POOL_ELEMENT))
			.registerSerializer(StructurePoolElementType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.STRUCTURE_POOL_ELEMENT))
			.registerDeserializer(String.class, StructureProcessorType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.STRUCTURE_PROCESSOR))
			.registerSerializer(StructureProcessorType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.STRUCTURE_PROCESSOR))
			.registerDeserializer(String.class, VillagerProfession.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.VILLAGER_PROFESSION))
			.registerSerializer(VillagerProfession.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.VILLAGER_PROFESSION))
			.registerDeserializer(String.class, VillagerType.class, (s, m) -> lookupDeserialize(s, BuiltInRegistries.VILLAGER_TYPE))
			.registerSerializer(VillagerType.class, (s, m) -> lookupSerialize(s, BuiltInRegistries.VILLAGER_TYPE))
			.build();
	public static final Logger LOGGER = LogManager.getLogger(Const.MOD_NAME);
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger(Const.MOD_NAME + " | Bedrock");
	public static final EntityType<SeatEntity> SEAT = Registry.register(BuiltInRegistries.ENTITY_TYPE, Const.id("seat"), FabricEntityTypeBuilder.
			<SeatEntity>create(MobCategory.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
			.build());
//	public static ObsidianConfig CONFIG;

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

		var manager = ThingResourceManager.initialize();
		ScriptParser.enable(manager);
		manager.registerParser(new BlockSetTypeParser());
		manager.registerParser(new ShapeParser());

//		GlobalFixer.init();

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			if (destination.dimensionTypeId().equals(BuiltinDimensionTypes.END)) {
				MinecraftServer server = destination.getServer();
				Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation("I don't know, probably something like `the_end/kill_dragon`"));
				if(!server.getPlayerList().getPlayerAdvancements(player).getOrStartProgress(advancement).isDone()) {

				}
			}
		});

		ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Const.id("mod_id").toString(), ModIdArgument.class,
				SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));

		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_group", new LegacyItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "creative_tab", new CreativeTabs());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_sound_groups", new BlockSoundGroups());
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
				net.minecraft.world.level.block.Block parentBlock = BuiltInRegistries.BLOCK.get(convertible.parent_block);
				net.minecraft.world.level.block.Block transformedBlock = BuiltInRegistries.BLOCK.get(convertible.transformed_block);
				AdditionalBlockInformation.Convertible.ConversionItem conversionItem = convertible.conversionItem;
				Item conversionItemItem;
				if (conversionItem.item != null) conversionItemItem = BuiltInRegistries.ITEM.get(conversionItem.item);
				else conversionItemItem = null;

				TagKey<Item> conversionItemTag = null;
				if (conversionItem.tag != null) conversionItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);

				Item reversalItemItem = null;
				TagKey<Item> reversalItemTag = null;
				AdditionalBlockInformation.Convertible.ConversionItem reversalItem = null;
				if (convertible.reversible) {
					if (convertible.reversalItem != null) reversalItem = convertible.reversalItem;

					if (reversalItem != null) {
						if (reversalItem.item != null) reversalItemItem = BuiltInRegistries.ITEM.get(conversionItem.item);
						if (reversalItem.tag != null)
							reversalItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);
					}
				}

				SoundEvent sound;
				if (convertible.sound != null) sound = BuiltInRegistries.SOUND_EVENT.get(convertible.sound);
				else sound = null;

				Item droppedItem;
				if (convertible.dropped_item != null) droppedItem = BuiltInRegistries.ITEM.get(convertible.dropped_item);
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

}
