package io.github.vampirestudios.obsidian;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonPrimitive;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.addon_modules.crucible.CrucibleItems;
import io.github.vampirestudios.obsidian.addon_modules.crucible.CrucibleSkills;
import io.github.vampirestudios.obsidian.addon_modules.crucible.EffectsModule;
import io.github.vampirestudios.obsidian.addon_modules.nexo.NexoItems;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleFabricHooks;
import io.github.vampirestudios.obsidian.api.crucible.SkillManager;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.AnimationManager;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.DynamicContainer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import io.github.vampirestudios.obsidian.registry.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

import java.util.ArrayList;
import java.util.List;

public class Obsidian implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(Const.MOD_NAME);
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger(Const.MOD_NAME + " | Bedrock");
	public static final EntityType<SeatEntity> SEAT = Registry.register(BuiltInRegistries.ENTITY_TYPE, Const.id("seat"), FabricEntityTypeBuilder.
			<SeatEntity>create(MobCategory.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0.001F, 0.001F))
			.build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Const.id("seat"))));
	public static final RegistryHelper OBSIDIAN_REGISTRY_HELPER = new RegistryHelper(Const.MOD_ID);

	public static MinecraftServer SERVER;

	public static final List<ConvertibleBlockPair> CONVERTIBLE_BLOCKS = new ArrayList<>();

	public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Obsidian.id("obsidian_equipment_asset"));

	public static final RegistryEntryAttachment<net.minecraft.world.level.block.Block, Boolean> BASED =
			RegistryEntryAttachment.boolBuilder(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath("quilt", "based"))
					.side(RegistryEntryAttachment.Side.CLIENT).build();

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

	private static <T> T lookupDeserialize(String s, Registry<T> registry) {
		return registry.getValue(Identifier.tryParse(s));
	}

	private static <T, U extends T> JsonElement lookupSerialize(T t, Registry<U> registry) {
		@SuppressWarnings("unchecked") //Widening cast happening because of generic type parameters in the registry class
		Identifier id = registry.getKey((U) t);
		if (id == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(id.toString());
	}


	public final static MenuType<DynamicContainer> DYNAMIC_CONTAINER = Registry.register(
			BuiltInRegistries.MENU,
			Obsidian.id("dynamic_container"),
			new MenuType<>(DynamicContainer::new, FeatureFlags.VANILLA_SET)
	);

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("You're now running Obsidian v%s for %s", Const.MOD_VERSION, SharedConstants.getCurrentVersion().name()));

		ServerLifecycleEvents.SERVER_STARTING.register(server -> SERVER = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER = null);
		ServerTickEvents.START_SERVER_TICK.register(server -> AnimationManager.getInstance().onTick());

		OI.init();
		OBE.init();
		OStructureTypes.init();
		OBlockTags.init();
		OEntityTags.init();
		OStructurePieceTypes.init();
		OItemComponents.init();
		new BaseGson();
		OMenus.init();

		SkillManager manager = SkillManager.getInstance();
		CrucibleEvents.bootstrap(manager);
		CrucibleFabricHooks.install();

		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_group", new LegacyItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "creative_tab", new CreativeTabs());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_sound_groups", new BlockSoundGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_set_types", new BlockSetTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "wood_types", new WoodTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_properties", new BlockProperties());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_properties", new ItemProperties());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tiers", new Tiers());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "blocks", new Blocks());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "ores", new Ores());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_materials", new ArmorMaterials());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "cosmetic", new Cosmetics());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "emojis", new Emojis());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item", new Items());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "oraxen_item", new NexoItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_skill", new CrucibleSkills());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_effect", new EffectsModule());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_item", new CrucibleItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tool", new Tools());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "particle", new Particles());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sound_events", new SoundEvents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "ranged_weapon", new RangedWeapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "weapon", new Weapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "commands", new Commands());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "entities", new Entities());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "shields", new Shields());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food_components", new FoodComponents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food", new Food());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "fuel_sources", new FuelSources());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sub_item_groups", new SubItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "condensed_item_entries", new CondensedItemEntries());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "biome_layouts", new BiomeLayouts());
//		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "guis", new Guis());
//		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
//			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "huds", new Huds());

		ObsidianAddonLoader.loadDefaultObsidianAddons();
		ObsidianAddonLoader.loadObsidianAddons();
		ObsidianAddonLoader.loadServerObsidianAddons();

		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "bedrock_blocks", new io.github.vampirestudios.obsidian.addon_modules.bedrock.Blocks());

		BedrockAddonLoader.loadDefaultBedrockAddons();
		BedrockAddonLoader.loadBedrockAddons();

		for (Block block : ContentRegistries.BLOCKS) {
			if (block.additional_information != null && block.additional_information.isConvertible) {
				AdditionalBlockInformation.Convertible convertible = block.additional_information.convertible;
				net.minecraft.world.level.block.Block parentBlock = BuiltInRegistries.BLOCK.getValue(convertible.parent_block);
				net.minecraft.world.level.block.Block transformedBlock = BuiltInRegistries.BLOCK.getValue(convertible.transformed_block);
				AdditionalBlockInformation.Convertible.ConversionItem conversionItem = convertible.conversionItem;
				Item conversionItemItem;
				if (conversionItem.item != null) conversionItemItem = BuiltInRegistries.ITEM.getValue(conversionItem.item);
				else conversionItemItem = null;

				TagKey<Item> conversionItemTag = null;
				if (conversionItem.tag != null) conversionItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);

				Item reversalItemItem = null;
				TagKey<Item> reversalItemTag = null;
				AdditionalBlockInformation.Convertible.ConversionItem reversalItem = null;
				if (convertible.reversible) {
					if (convertible.reversalItem != null) reversalItem = convertible.reversalItem;

					if (reversalItem != null) {
						if (reversalItem.item != null) reversalItemItem = BuiltInRegistries.ITEM.getValue(conversionItem.item);
						if (reversalItem.tag != null)
							reversalItemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);
					}
				}

				SoundEvent sound;
				if (convertible.sound != null) sound = BuiltInRegistries.SOUND_EVENT.getValue(convertible.sound);
				else sound = null;

				Item droppedItem;
				if (convertible.dropped_item != null) droppedItem = BuiltInRegistries.ITEM.getValue(convertible.dropped_item);
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

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClientSide()) {
				for (ConvertibleBlockPair convertibleBlock : CONVERTIBLE_BLOCKS) {
					ItemStack itemStack = player.getItemInHand(hand);
					BlockState blockState = world.getBlockState(hitResult.getBlockPos());
					if (convertibleBlock.getConversionItem().matches(itemStack)) {
						if (blockState.getBlock() == convertibleBlock.getOriginal()) {
							if (convertibleBlock.getSound() != null)
								world.playSound(null, hitResult.getBlockPos(), convertibleBlock.getSound(),
										SoundSource.BLOCKS, 1.0F, 1.0F);

							if (convertibleBlock.getDroppedItem() != null) {
								ItemStack newStack = new ItemStack(convertibleBlock.getDroppedItem());
								if (!newStack.isEmpty() && world instanceof ServerLevel serverLevel &&
										serverLevel.getGameRules().get(GameRules.BLOCK_DROPS)) {
									ItemEntity itemEntity = new ItemEntity(world, hitResult.getBlockPos().getX() + 0.5,
											hitResult.getBlockPos().getY() + 0.5,
											hitResult.getBlockPos().getZ() + 0.5,
											newStack);
									itemEntity.setDefaultPickUpDelay();
									world.addFreshEntity(itemEntity);
								}
							}

							world.setBlock(hitResult.getBlockPos(), convertibleBlock.getConverted()
									.withPropertiesOf(blockState), 11);
							if (!player.getAbilities().instabuild) itemStack.hurtAndBreak(1, player, hand);
							world.gameEvent(GameEvent.BLOCK_CHANGE, hitResult.getBlockPos(),
									GameEvent.Context.of(player, blockState));
							return InteractionResult.SUCCESS;
						}
					} else if (convertibleBlock.getReversingItem() != null &&
							convertibleBlock.getReversingItem().matches(itemStack) &&
							blockState.is(convertibleBlock.getConverted())) {
						if (convertibleBlock.getSound() != null)
							world.playSound(null, hitResult.getBlockPos(), convertibleBlock.getSound(),
									SoundSource.BLOCKS, 1.0F, 1.0F);

						if (convertibleBlock.getDroppedItem() != null) {
							ItemStack newStack = new ItemStack(convertibleBlock.getDroppedItem());
							if (!newStack.isEmpty() && world instanceof ServerLevel serverLevel &&
									serverLevel.getGameRules().get(GameRules.BLOCK_DROPS)) {
								ItemEntity itemEntity = new ItemEntity(world, hitResult.getBlockPos().getX() + 0.5,
										hitResult.getBlockPos().getY() + 0.5,
										hitResult.getBlockPos().getZ() + 0.5, newStack);
								itemEntity.setDefaultPickUpDelay();
								world.addFreshEntity(itemEntity);
							}
						}

						world.setBlock(hitResult.getBlockPos(), convertibleBlock.getOriginal()
								.withPropertiesOf(blockState), 11);
						itemStack.hurtAndBreak(1, player, hand);
						world.gameEvent(GameEvent.BLOCK_CHANGE, hitResult.getBlockPos(),
								GameEvent.Context.of(player, blockState));
						return InteractionResult.SUCCESS;
					}
				}
			}
			return InteractionResult.PASS;
		});
	}

}
