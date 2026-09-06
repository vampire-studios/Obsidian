package io.github.vampirestudios.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import io.github.vampirestudios.obsidian.addon_modules.*;
import io.github.vampirestudios.obsidian.addon_modules.crucible.*;
import io.github.vampirestudios.obsidian.addon_modules.nexo.NexoItems;
import io.github.vampirestudios.obsidian.api.EquipmentEvents;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleFabricHooks;
import io.github.vampirestudios.obsidian.api.crucible.SkillManager;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.AnimationManager;
import io.github.vampirestudios.obsidian.api.events.PlayerPickupItemCallback;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.configPack.AddonPacks;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.DynamicContainer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArrowEntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ProjectileEntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WorldEventManager;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ThrownKnifeEntity;
import io.github.vampirestudios.obsidian.registry.*;
import io.github.vampirestudios.obsidian.data.AddonBlockLoot;
import io.github.vampirestudios.obsidian.data.AddonBlockTags;
import io.github.vampirestudios.obsidian.villager.AddonJobSites;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Obsidian implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(Const.MOD_NAME);
	public static final Logger BEDROCK_LOGGER = LogManager.getLogger(Const.MOD_NAME + " | Bedrock");
	public static final EntityType<SeatEntity> SEAT = Registry.register(
			BuiltInRegistries.ENTITY_TYPE, Const.id("seat"),
			EntityType.Builder.of(SeatEntity::new, MobCategory.MISC)
					.noSummon()
					.noLootTable()
					.sized(0.001F, 0.001F)
					.passengerAttachments(0.0F)
					.clientTrackingRange(10)
					.build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Const.id("seat"))));
	public static final EntityType<ThrownKnifeEntity> THROWN_KNIFE = Registry.register(
			BuiltInRegistries.ENTITY_TYPE, Const.id("thrown_knife"),
			EntityType.Builder.of(ThrownKnifeEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(64)
					.build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Const.id("thrown_knife"))));
	/**
	 * One entity type for every {@code item/projectile} a pack declares. The definition is resolved from
	 * the item the projectile carries, so packs need no entity type of their own.
	 */
	public static final EntityType<ProjectileEntityImpl> PROJECTILE = Registry.register(
			BuiltInRegistries.ENTITY_TYPE, Const.id("projectile"),
			EntityType.Builder.<ProjectileEntityImpl>of(ProjectileEntityImpl::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(64)
					.updateInterval(10)
					.build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Const.id("projectile"))));
	/** The arrow-shaped counterpart of {@link #PROJECTILE}, fired from bows and crossbows. */
	public static final EntityType<ArrowEntityImpl> PROJECTILE_ARROW = Registry.register(
			BuiltInRegistries.ENTITY_TYPE, Const.id("projectile_arrow"),
			EntityType.Builder.<ArrowEntityImpl>of(ArrowEntityImpl::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Const.id("projectile_arrow"))));
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
		@SuppressWarnings("unchecked")
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
		ServerTickEvents.END_SERVER_TICK.register(server -> WorldEventManager.tick());
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> WorldEventManager.clear());
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) ->
				WorldEventManager.onDeath(entity, source.getEntity()));

		OI.init();
		OBE.init();
		OStructureTypes.init();
		OBlockTags.init();
		OEntityTags.init();
		OStructurePieceTypes.init();
		OItemComponents.init();
		io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteCommand.register();
		io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteApplication.register();
		new BaseGson();
		OMenus.init();

		SkillManager manager = SkillManager.getInstance();
		CrucibleEvents.bootstrap(manager);
		CrucibleFabricHooks.install();

		// Palettes load first: items, blocks and entities read them while registering.
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "palettes", new Palettes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_group", new LegacyItemGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "creative_tab", new CreativeTabs());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_sound_groups", new BlockSoundGroups());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_set_types", new BlockSetTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "wood_types", new WoodTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_properties", new BlockProperties());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_properties", new ItemProperties());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_template", new ItemTemplates());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "block_template", new BlockTemplates());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tiers", new Tiers());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "blocks", new Blocks());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "fluids", new Fluids());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "cauldron_types", new CauldronTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_materials", new ArmorMaterials());
//		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
//			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor_models", new ArmorModels());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "armor", new Armor());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "elytra", new Elytras());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "cosmetic", new Cosmetics());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "emojis", new Emojis());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item", new Items());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "oraxen_item", new NexoItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_skill", new CrucibleSkills());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_effect", new EffectsModule());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "augment_types", new AugmentTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "crucible_item", new CrucibleItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "item_sets", new ItemSets());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "tool", new Tools());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "zoomable_items", new ZoomableItems());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "particle", new Particles());
		// Status effects load first: a potion resolves the effects it names while registering.
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "status_effects", new StatusEffects());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "potion", new Potions());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sound_events", new SoundEvents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "ranged_weapon", new RangedWeapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "weapon", new Weapons());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "projectile", new Projectiles());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sound_playing_items", new SoundPlayingItems());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "commands", new Commands());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "entities", new Entities());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "biome_modifications", new BiomeModifications());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "portals", new Portals());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "patterns", new Patterns());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "world_events", new WorldEvents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "shields", new Shields());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food_components", new FoodComponents());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "food", new Food());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_professions", new VillagerProfessions());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "villager_biome_types", new VillagerBiomeTypes());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "fuel_sources", new FuelSources());
		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "brewing_fuel_sources", new BrewingFuelSources());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "expanded_item_group", new ExpandedItemGroups());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "sub_item_groups", new SubItemGroups());
//		registerInRegistry(Registries.ADDON_MODULE_REGISTRY, "condensed_item_entries", new CondensedItemEntries());
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

		AddonPacks.register();
		AddonJobSites.register();
		AddonBlockLoot.register();
		AddonBlockTags.register();

		registerBlockConversions();
		EquipmentEvents.register();

		PlayerPickupItemCallback.EVENT.register((player, itemEntity) -> {
			if (!player.level().isClientSide()
					&& itemEntity.getItem().getItem() instanceof ItemImpl itemImpl) {
				EventActionHandler.handleOnPickup(player, itemImpl.item);
			}
			return InteractionResult.PASS;
		});

		UseEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
			if (world.isClientSide()) return InteractionResult.PASS;
			if (target instanceof EntityImpl obsidianEntity) {
				return obsidianEntity.handleInteraction(player, hand);
			}
			return InteractionResult.PASS;
		});
	}

	/**
	 * Turns block-declared convertibles into BLOCK_TRANSFORMER components on their conversion items.
	 * The declaration lives on the block but the component lives on the item, so the mapping is inverted
	 * here and applied through Fabric's default-component hook — conversion items are often vanilla items
	 * that Obsidian never constructs, so the component cannot be baked in at registration time.
	 */
	private static void registerBlockConversions() {
		List<Map.Entry<Predicate<Item>, BlockTransformer.BlockTransformData>> entries = new ArrayList<>();

		for (Block block : ContentRegistries.BLOCKS) {
			if (block.additional_information == null) continue;

			// Declaring a "convertible" object is the opt-in; the separate is_convertible flag it used to
			// require was redundant and silently disabled otherwise-complete declarations.
			AdditionalBlockInformation.Convertible convertible = block.additional_information.convertible;
			if (convertible == null) continue;

			try {
				Identifier parentId = convertible.parent_block != null
						? convertible.parent_block
						: block.information.id;

				net.minecraft.world.level.block.Block parentBlock = BuiltInRegistries.BLOCK.getValue(parentId);
				net.minecraft.world.level.block.Block transformedBlock =
						BuiltInRegistries.BLOCK.getValue(convertible.transformed_block);
				if (parentBlock == null || transformedBlock == null) {
					LOGGER.warn("[Obsidian] Convertible block {} names an unknown block.", block.information.id);
					continue;
				}

				Holder<SoundEvent> sound = convertible.sound != null
						? BuiltInRegistries.SOUND_EVENT.get(convertible.sound).orElse(null)
						: null;

				Identifier context = block.information.id;

				Predicate<Item> conversionItem = toItemPredicate(convertible.conversionItem);
				if (conversionItem != null) {
					entries.add(Map.entry(conversionItem,
							conversionTransform(parentBlock, transformedBlock, sound, convertible, context)));
				}

				if (convertible.reversible) {
					Predicate<Item> reversalItem = toItemPredicate(convertible.reversalItem);
					if (reversalItem != null) {
						entries.add(Map.entry(reversalItem,
								conversionTransform(transformedBlock, parentBlock, sound, convertible, context)));
					}
				}

				if (convertible.dropped_item != null) {
					LOGGER.warn("[Obsidian] Convertible block {} sets dropped_item, which BLOCK_TRANSFORMER "
							+ "does not support; use \"loot\" with a loot table instead.", context);
				}
			} catch (Exception e) {
				LOGGER.warn("[Obsidian] Failed to build conversion for block {}: {}",
						block.information.id, e.getMessage());
			}
		}

		if (entries.isEmpty()) return;

		DefaultItemComponentEvents.MODIFY.register(context -> context.modify(item -> true, (builder, item) -> {
			List<BlockTransformer.BlockTransformData> matched = new ArrayList<>();
			for (Map.Entry<Predicate<Item>, BlockTransformer.BlockTransformData> entry : entries) {
				if (entry.getKey().test(item)) matched.add(entry.getValue());
			}
			if (matched.isEmpty()) return;

			// Merge rather than overwrite — the conversion item may be a vanilla axe or shovel that already
			// carries a transformer for stripping or path-making.
			Holder<BlockTransformer> existing = item.components().get(DataComponents.BLOCK_TRANSFORMER);
			List<BlockTransformer.BlockTransformData> combined = new ArrayList<>();
			if (existing != null) combined.addAll(existing.value().transforms());
			combined.addAll(matched);

			builder.set(DataComponents.BLOCK_TRANSFORMER, Holder.direct(new BlockTransformer(combined)));
		}));
	}

	private static BlockTransformer.BlockTransformData conversionTransform(
			net.minecraft.world.level.block.Block from,
			net.minecraft.world.level.block.Block to,
			Holder<SoundEvent> sound,
			AdditionalBlockInformation.Convertible options,
			Identifier context
	) {
		BlockTransformer.BlockTransformData.Builder builder = BlockTransformer.BlockTransformData.builder(
				RuleBasedStateProvider.ifTrueThenProvide(
						BlockPredicate.matchesBlocks(from),
						new CopyPropertiesProvider(to)));
		if (sound != null) builder.sound(sound);
		options.applyTo(builder, context);
		return builder.build();
	}

	private static Predicate<Item> toItemPredicate(AdditionalBlockInformation.Convertible.ConversionItem conversionItem) {
		if (conversionItem == null) return null;

		if (conversionItem.item != null) {
			Item item = BuiltInRegistries.ITEM.getValue(conversionItem.item);
			return item == null ? null : candidate -> candidate == item;
		}
		if (conversionItem.tag != null) {
			// Fully qualified: Obsidian's own Registries is wildcard-imported and would shadow this.
			TagKey<Item> tag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, conversionItem.tag);
			return candidate -> candidate.builtInRegistryHolder().is(tag);
		}
		return null;
	}

}
