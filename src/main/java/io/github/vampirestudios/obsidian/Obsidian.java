package io.github.vampirestudios.obsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Lifecycle;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.Processor;
import com.swordglowsblue.artifice.common.ArtificeRegistry;
import com.swordglowsblue.artifice.impl.ArtificeImpl;
import com.swordglowsblue.artifice.impl.DynamicResourcePackFactory;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
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
import io.github.vampirestudios.vampirelib.api.ConvertibleBlockPair;
import io.github.vampirestudios.vampirelib.api.ConvertibleBlocksRegistry;
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
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePackSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.appleskin.api.AppleSkinApi;

import java.io.FileNotFoundException;

public class Obsidian implements ModInitializer/*, MealAPIInitializer*/, AppleSkinApi {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
			.registerTypeAdapter(ModelIdentifier.class, (SimpleStringDeserializer<?>) ModelIdentifier::new)
			.setPrettyPrinting()
			.setLenient()
			.create();
	public static final String MOD_ID = "obsidian";
	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY = FabricRegistryBuilder.createSimple(AddonModule.class, id("addon_modules")).buildAndRegister();
	public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY = FabricRegistryBuilder.createSimple(ItemGroup.class, new Identifier(MOD_ID, "item_groups")).buildAndRegister();
	public static final Registry<FoodComponent> FOOD_COMPONENTS = FabricRegistryBuilder.createSimple(FoodComponent.class, new Identifier(MOD_ID, "food_components")).buildAndRegister();
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(MOD_ID, "entity_components")), Lifecycle.stable(), null);
	public static Registry<AnimationDefinition> ANIMATION_DEFINITIONS = FabricRegistryBuilder.createSimple(AnimationDefinition.class, id("animation_definitions")).buildAndRegister();
	public static Registry<AnimationChannel.Interpolation> ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.createSimple(AnimationChannel.Interpolation.class, id("animation_channel_interpolations")).buildAndRegister();
	public static Registry<AnimationChannel.Target> ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.createSimple(AnimationChannel.Target.class, id("animation_channel_targets")).buildAndRegister();
	public static final String NAME = "Obsidian";
	public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger("[" + NAME + ": Bedrock]");
	public static final Obsidian INSTANCE = new Obsidian();
	public static final String VERSION = "0.7.0-alpha";
	public static final EntityType<SeatEntity> SEAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "seat"), FabricEntityTypeBuilder.
			<SeatEntity>create(SpawnGroup.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
			.build());
	public static ObsidianConfig CONFIG;

	public static ResourcePackSource RESOURCE_PACK_SOURCE = ResourcePackSource.nameAndSource("pack.source.obsidian");

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static <T> T registerInRegistryVanilla(Registry<T> registry, String name, T idk) {
		return Registry.register(registry, name, idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, String name, T idk) {
		registerInRegistry(registry, new Identifier(MOD_ID, name), idk);
	}

	public static <T> void registerInRegistry(Registry<T> registry, Identifier name, T idk) {
		Registry.register(registry, name, idk);
	}

	public static void registerDataPack(Identifier id, Processor<ArtificeResourcePack.ServerResourcePackBuilder> register) {
		if (!ArtificeRegistry.DATA_PACKS.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.DATA_PACKS, id, new DynamicResourcePackFactory<>(ResourceType.SERVER_DATA, id, register));
	}

	@Environment(EnvType.CLIENT)
	public static void registerAssetPack(Identifier id, Processor<ArtificeResourcePack.ClientResourcePackBuilder> register) {
		if (!ArtificeRegistry.RESOURCE_PACKS.containsId(id))
			ArtificeImpl.registerSafely(ArtificeRegistry.RESOURCE_PACKS, id, new DynamicResourcePackFactory<>(ResourceType.CLIENT_RESOURCES, id, register));
	}

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("You're now running Obsidian v%s for 1.17.1", VERSION));
		AutoConfig.register(ObsidianConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ObsidianConfig.class).getConfig();

		CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> DumpRegistriesCommand.register(commandDispatcher));

		AnimationChannel.Targets.init();
		AnimationChannel.Interpolations.init();

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
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "ageable", AgeableComponent.class);
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
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "nameable", NameableComponent.class);

		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "movement.basic", BasicMovementComponent.class);

		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.panic", PanicBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.tempt", TemptBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENT_REGISTRY, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);

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
		registerInRegistry(ADDON_MODULE_REGISTRY, "block_sound_groups", new BlockSoundGroups());
		registerInRegistry(ADDON_MODULE_REGISTRY, "block_materials", new BlockMaterials());
		registerInRegistry(ADDON_MODULE_REGISTRY, "blocks", new Blocks());
		registerInRegistry(ADDON_MODULE_REGISTRY, "ores", new Ores());
		registerInRegistry(ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
		registerInRegistry(ADDON_MODULE_REGISTRY, "paintings", new Paintings());
		registerInRegistry(ADDON_MODULE_REGISTRY, "armor_materials", new ArmorMaterials());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
		registerInRegistry(ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(ADDON_MODULE_REGISTRY, "emojis", new Emojis());
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
		registerInRegistry(ADDON_MODULE_REGISTRY, "entities", new Entities());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(ADDON_MODULE_REGISTRY, "entity_models", new EntityModels());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(ADDON_MODULE_REGISTRY, "entity_animations", new EntityAnimations());
		registerInRegistry(ADDON_MODULE_REGISTRY, "shields", new Shields());
		registerInRegistry(ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
		registerInRegistry(ADDON_MODULE_REGISTRY, "food_components", new FoodComponents());
		registerInRegistry(ADDON_MODULE_REGISTRY, "food", new Food());
		registerInRegistry(ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
		registerInRegistry(ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());
		registerInRegistry(ADDON_MODULE_REGISTRY, "fuel_sources", new FuelSources());
		registerInRegistry(ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());
//		registerInRegistry(ADDON_MODULE_REGISTRY, "biome_layouts", new BiomeLayouts());

		for (Block block : ObsidianAddonLoader.BLOCKS) {
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
						if (reversalItem.tag != null) reversalItemTag = TagKey.of(Registry.ITEM_KEY, conversionItem.tag);
					}
				}

				SoundEvent sound;
				if (convertible.sound != null) sound = Registry.SOUND_EVENT.get(convertible.sound);
				else sound = null;

				Item droppedItem;
				if (convertible.dropped_item != null) droppedItem = Registry.ITEM.get(convertible.dropped_item);
				else droppedItem = null;

				ConvertibleBlockPair.ConversionItem reversalItem1;
				if (reversalItem != null) reversalItem1 = new ConvertibleBlockPair.ConversionItem(reversalItemTag, reversalItemItem);
				else reversalItem1 = null;

				ConvertibleBlockPair.ConversionItem conversionItem1 = new ConvertibleBlockPair.ConversionItem(conversionItemTag, conversionItemItem);
				ConvertibleBlockPair convertibleBlockPair;
				if (reversalItem1 != null) convertibleBlockPair = new ConvertibleBlockPair(parentBlock, transformedBlock,
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
		DimensionType

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
		Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> {
			try {
				addonModule.initAppleSkin();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

}
