package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.VanillaBlockSetTypes;
import io.github.vampirestudios.obsidian.api.VanillaWoodTypes;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.CompanionBlocks;
import io.github.vampirestudios.obsidian.api.obsidian.block.SaplingBaseBlock;
import io.github.vampirestudios.obsidian.block.PaintingTableBlock;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OBE;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Blocks implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo modInfo) throws IOException {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = getBlock(addon, file);
		if (block == null) return;

		// Before anything reads block.information — a block that declares none gets the template's.
		applyTemplate(block);

		Identifier blockId = Identifier.fromNamespaceAndPath(modInfo.modId(), AddonFormats.baseName(file));
		// Asked before the block is built: a built one that cannot be registered leaves a registry entry
		// behind that stops the game finishing startup at all.
		if (alreadyRegistered(BuiltInRegistries.BLOCK, blockId, "block", file.getName())) return;

		block.information.id = blockId;

		BlockBehaviour.Properties blockProps = createBlockProperties(block, blockId);
		Item.Properties itemProps = createItemProperties(block, blockId);
		ResourceKey<CreativeModeTab> tab = getCreativeTab(block);
		RegistryHelperBlockExpanded registry = new RegistryHelperBlockExpanded(modInfo.modId());

		try {
			registerSpecificBlockType(block, blockId, blockProps, itemProps, registry, tab);
			registerAdditionalFeatures(block, blockId, registry);

			if (!addon.getConfigPackInfo().hasData) {
				new BlockInitThread(block);
			}

			registerToContentRegistries(block, blockId);
		} catch (Exception e) {
			// Not getBlockType() — an unknown block_type is one of the things that lands us here,
			// and re-parsing it would throw straight back out of the handler.
			if (BlockType.OXIDIZING_BLOCK.name().equalsIgnoreCase(block.block_type)) {
				getOxidationStageIds(block).forEach(id -> failedRegistering("block", id.toString(), e));
			} else {
				failedRegistering("block", file.getName(), e);
			}
		}
	}

	private BlockBehaviour.Properties createBlockProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                                        Identifier blockId) {
		return createBlockProperties(block, blockId, block.information.getBlockSettings());
	}

	/**
	 * @param settings the settings to build from, which is the block's own unless a companion block
	 *                 declared settings of its own
	 */
	private BlockBehaviour.Properties createBlockProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                                        Identifier blockId,
	                                                        io.github.vampirestudios.obsidian.api.obsidian.BlockSettings settings) {
		BlockBehaviour.Properties props = block.information.parentBlock != null
				? BlockBehaviour.Properties.ofLegacyCopy(BuiltInRegistries.BLOCK.getValue(block.information.parentBlock))
				: BlockBehaviour.Properties.of();

		props.setId(ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, blockId));

		// A declared loot table points the block at someone else's table — vanilla's, another mod's, or one
		// the pack ships. Companion blocks declare theirs separately, so this is only the block itself.
		if (block.dropInformation != null && blockId.equals(block.information.id)) {
			overrideLootTable(props, block.dropInformation.lootTable);
		}

		if (settings != null) settings.applyTo(props);

		return props;
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                             Identifier itemId) {
		Item.Properties props = new Item.Properties()
				.useBlockDescriptionPrefix()
				.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, itemId));

		// --------------------------------------------------
		// 1. Legacy / JSON item settings (base defaults)
		// --------------------------------------------------
		if (block.information.getItemSettings() != null) {
			block.information.getItemSettings().applyTo(props);
		}

		// --------------------------------------------------
		// 2. Food definition
		// --------------------------------------------------
		if (block.food_information != null) {
			// Components are applied below as the override layer, so a declared consumable still wins.
			ItemModuleHelper.applyFood(props, block.food_information, null);
		}

		// --------------------------------------------------
		// 3. Apply DataComponents LAST (override layer)
		// --------------------------------------------------
		if (block.components != null) {
			applyAllComponents(props, block.components);
		}

		return props;
	}

	static void applyAllComponents(Item.Properties props, DataComponentMap map) {
		for (TypedDataComponent<?> entry : map) {
			applyTyped(props, entry);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void applyTyped(Item.Properties props, TypedDataComponent<T> entry) {
		props.component(entry.type(), entry.value());
	}

	private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block.components != null) {
			Identifier tabId = block.components.get(OItemComponents.CREATIVE_TAB);
			if (tabId != null) {
				return ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, tabId);
			}
		}

		// 2) Then item settings
		var settings = block.information.getItemSettings();
		if (settings != null) {
			var group = settings.getItemGroup();
			if (group != null) return group;

			var parent = settings.getParentSettings();
			if (parent != null && parent.getItemGroup() != null) return parent.getItemGroup();
		}

		// 3) Fallback
		return CreativeModeTabs.BUILDING_BLOCKS;
	}

	private void registerSpecificBlockType(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                       Identifier blockId,
	                                       BlockBehaviour.Properties blockProps,
	                                       Item.Properties itemProps,
	                                       RegistryHelperBlockExpanded registry,
	                                       ResourceKey<CreativeModeTab> itemGroup) {
		BlockType type = block.getBlockType();
		if (type == null) {
			handleAdditionalInformationFlags(block, blockId, blockProps, itemProps, registry, itemGroup);
			return;
		}

		switch (type) {
			case PAINTING_TABLE -> {
				Block paintingTable = registry.registerBlock(new PaintingTableBlock(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				OBE.PAINTING_TABLE.addValidBlock(paintingTable);
			}
			case BLOCK, WOOD -> {
				if (ContainerLogic.isContainer(block)) {
					registerContainerBlock(blockId, block, blockProps, itemProps, registry, itemGroup, false);
				} else if (isDyable(block)) {
					registerDyableBlock(blockId, block, blockProps, itemProps, registry);
				} else if (isPowerable(block) || isToggleable(block)) {
					registry.registerBlock(new PowerableBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				} else {
					registry.registerBlock(new BlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				}
			}
			case HORIZONTAL_DIRECTIONAL -> {
				boolean dyable = isDyable(block);
				boolean sittable = isSittable(block);
				boolean powerable = isPowerable(block) || isToggleable(block);

				if (ContainerLogic.isContainer(block)) {
					registerContainerBlock(blockId, block, blockProps, itemProps, registry, itemGroup, true);
				} else if (dyable && sittable) {
					registerDyableAndSittableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
				} else if (dyable) {
					registerDyableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
				} else if (powerable) {
					registry.registerBlock(new PowerableHorizontalFacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				} else if (sittable) {
					registerSittableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
				} else {
					registry.registerBlock(new HorizontalFacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				}
			}
			case DIRECTIONAL ->
					registry.registerBlock(new FacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case BED ->
					registry.registerBlock(new BedBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case CAMPFIRE ->
					registry.registerBlock(new CampfireBlockImpl(block.campfire_properties, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case STAIRS ->
					registry.registerBlock(new StairsImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case SLAB ->
					registry.registerBlock(new SlabImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case FENCE ->
					registry.registerBlock(new FenceImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case FENCE_GATE ->
					registry.registerBlock(new FenceGateImpl(block, blockProps, woodType(block)),
							block, blockId.getPath(), itemProps, itemGroup);
			case CAKE ->
					registry.registerBlock(new CakeBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case TRAPDOOR ->
					registry.registerBlock(new TrapDoorBlock(blockSetType(block), blockProps),
							block, blockId.getPath(), itemProps, itemGroup);
			case DOOR ->
					registry.registerBlock(new DoorBlock(blockSetType(block), blockProps),
							block, blockId.getPath(), itemProps, itemGroup);
			case WALL ->
					registry.registerBlock(new WallImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case PRESSURE_PLATE ->
					registry.registerBlock(new PressurePlateBlock(blockSetType(block), blockProps),
							block, blockId.getPath(), itemProps, itemGroup);
			case EIGHT_DIRECTIONAL_BLOCK -> OBE.ROTATION_BLOCK.addValidBlock(
					registry.registerBlock(new EightDirectionBlockImpl(block, blockProps),
							block, blockId.getPath(), itemProps, itemGroup));
			case SIXTEEN_DIRECTIONAL_BLOCK -> OBE.ROTATION_BLOCK.addValidBlock(
					registry.registerBlock(new SixteenDirectionBlockImpl(block, blockProps),
							block, blockId.getPath(), itemProps, itemGroup));
			case CROP ->
					registry.registerBlock(new CropBlockImpl(block, blockProps.noCollision().instabreak().randomTicks()),
							block, blockId.getPath(), itemProps, itemGroup);
			case CLIMBABLE ->
					registry.registerBlock(new ClimbableBlockImpl(block, blockProps.noCollision()),
							block, blockId.getPath(), itemProps, itemGroup);
			case ROD ->
					registry.registerBlock(new RodBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case CANDLE ->
					registry.registerBlock(new CandleBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case LEVER ->
					registry.registerBlock(new LeverBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case LOG ->
					registry.registerLog(block, blockProps, blockId.getPath(), MapColor.STONE, MapColor.STONE, itemProps);
			case STEM -> registry.registerNetherStemBlock(block, blockProps, blockId.getPath(), MapColor.STONE, itemProps);
			case OXIDIZING_BLOCK -> getOxidationStageIds(block).forEach(id ->
					registry.registerBlock(new BlockImpl(block, createBlockProperties(block, id)), block,
							id.getPath(), createItemProperties(block, id), itemGroup));
			case PLANT -> {
				if (isWaterloggable(block)) {
					registry.registerBlock(new WaterloggablePlantBlockImpl(block, blockProps.noCollision().instabreak()),
							block, blockId.getPath(), itemProps, itemGroup);
				} else {
					registry.registerBlock(new PlantBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
				}
			}
			case BUSH ->
					registry.registerBlock(new BushBlockImpl(block, blockProps.noCollision().instabreak().randomTicks()),
							block, blockId.getPath(), itemProps, itemGroup);
			case ROTATED_PILLAR ->
					registry.registerBlock(new PillarBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case HORIZONTAL_FACING_PLANT ->
					registry.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockProps.noCollision().instabreak()),
							block, blockId.getPath(), itemProps, itemGroup);
			case SAPLING -> registry.registerBlock(new SaplingBaseBlock(block,
						blockProps.noCollision().randomTicks().instabreak().sound(net.minecraft.world.level.block.SoundType.GRASS)),
					block, blockId.getPath(), itemProps, itemGroup);
			case TORCH -> registry.registerBlock(new TorchBaseBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case BEEHIVE -> {
				Block beehive = registry.registerBlock(new BeehiveBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
				REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beehive),
						blockId.getPath() + "_beehive_be");
			}
			case LEAVES -> registry.registerLeavesBlock(block, blockProps, blockId.getPath(), itemProps);
			case LADDER ->
					registry.registerBlock(new CustomLadderBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case PATH ->
					registry.registerBlock(new PathBlockImpl(blockProps, block), block, blockId.getPath(), itemProps, itemGroup);
			case BUTTON ->
					registry.registerBlock(new ButtonBlock(blockSetType(block),
							block.information.wooden_button ? 30 : 20, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case DOUBLE_PLANT -> {
				if (isWaterloggable(block)) {
					registry.registerDoubleBlock(new WaterloggableTallFlowerBlockImpl(block, blockProps.noCollision().instabreak()),
							block, blockId.getPath(), itemProps);
				} else {
					registry.registerDoubleBlock(new TallFlowerBlockImpl(block, blockProps), block, blockId.getPath(), itemProps);
				}
			}
			case HORIZONTAL_FACING_DOUBLE_PLANT ->
					registry.registerDoubleBlock(new TallFlowerBlock(blockProps.noCollision().instabreak()),
							block, blockId.getPath(), itemProps);
			case HANGING_DOUBLE_LEAVES ->
					registry.registerHangingTallBlock(new HangingDoubleLeaves(blockProps.noCollision().instabreak()),
							block, blockId.getPath(), itemProps);
			case LANTERN ->
					registry.registerBlock(new LanternBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case CHAIN ->
					registry.registerBlock(new ChainBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case PANE ->
					registry.registerBlock(new PaneBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case DYEABLE -> registerDyableBlock(blockId, block, blockProps, itemProps, registry);
			case LOOM ->
					registry.registerBlock(new LoomBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case CRAFTING_TABLE ->
					registry.registerBlock(new CraftingTableBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
			case FURNACE -> {
				Block furnace = registry.registerBlock(new FurnaceBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
				BlockEntityTypes.FURNACE.addValidBlock(furnace);
			}
			case BLAST_FURNACE -> {
				Block blast = registry.registerBlock(new BlastFurnaceBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
				BlockEntityTypes.BLAST_FURNACE.addValidBlock(blast);
			}
			case SMOKER -> {
				Block smoker = registry.registerBlock(new SmokerBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
				BlockEntityTypes.SMOKER.addValidBlock(smoker);
			}
			case BARREL -> {
				Block barrel = registry.registerBlock(new BarrelBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
				BlockEntityTypes.BARREL.addValidBlock(barrel);
			}
			case CARPET ->
					registry.registerBlock(new CarpetBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
		}
	}

	private void handleAdditionalInformationFlags(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                              Identifier blockId,
	                                              BlockBehaviour.Properties blockProps,
	                                              Item.Properties itemProps,
	                                              RegistryHelperBlockExpanded registry,
	                                              ResourceKey<CreativeModeTab> itemGroup) {
		if (block.additional_information == null) return;

		var info = block.additional_information;
		if (info.path)
			registry.registerBlock(new PathBlockImpl(net.minecraft.world.level.block.Blocks.DIRT, blockProps, block), block, blockId.getPath(), itemProps, itemGroup);
		else if (info.lantern)
			registry.registerBlock(new LanternBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
		else if (info.barrel)
			registry.registerBlock(new BarrelBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
		else if (info.leaves) registry.registerLeavesBlock(block, blockProps, blockId.getPath(), itemProps);
		else if (info.chains)
			registry.registerBlock(new ChainBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
		else if (info.cake_like)
			registry.registerBlock(new CakeBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
	}

	private void registerAdditionalFeatures(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                        Identifier baseId,
	                                        RegistryHelperBlockExpanded registry) {
		if (block.additional_information == null) return;
		registerExtraVariants(block, registry);
	}

	/** Points a block at an existing loot table, when the pack named one. */
	private void overrideLootTable(BlockBehaviour.Properties props, Identifier lootTable) {
		if (lootTable == null) return;
		props.overrideLootTable(java.util.Optional.of(ResourceKey.create(
				net.minecraft.core.registries.Registries.LOOT_TABLE, lootTable)));
	}

	/**
	 * Settings for one companion block, which are the base block's with the companion's own id and,
	 * when the pack named one for it, its own loot table.
	 */
	private BlockBehaviour.Properties createCompanionProperties(
			io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Identifier id,
			CompanionBlocks.Declared variant) {
		io.github.vampirestudios.obsidian.api.obsidian.BlockSettings settings = variant.options().getBlockSettings();
		BlockBehaviour.Properties props = createBlockProperties(block, id,
				settings != null ? settings : block.information.getBlockSettings());

		Identifier lootTable = variant.options().lootTable;
		// The older spelling, which names the same tables from drop_information.
		if (lootTable == null && block.dropInformation != null) {
			lootTable = block.dropInformation.lootTableFor(variant.type().key);
		}
		overrideLootTable(props, lootTable);
		return props;
	}

	private void registerExtraVariants(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                   RegistryHelperBlockExpanded registry) {
		var info = block.additional_information;
		net.minecraft.world.level.block.state.properties.WoodType woodType = getWoodTypeForSounds(info);

		for (CompanionBlocks.Declared variant : CompanionBlocks.declared(block)) {
			Identifier id = variant.id();
			BlockBehaviour.Properties props = createCompanionProperties(block, id, variant);
			net.minecraft.world.level.block.Block companion = switch (variant.type()) {
				case SLAB -> new SlabImpl(block, props);
				case STAIRS -> new StairsImpl(block, props);
				case WALL -> new WallImpl(block, props);
				case FENCE -> new FenceImpl(block, props);
				case FENCE_GATE -> new FenceGateImpl(block, props, woodType);
				case BUTTON -> new ButtonBlock(woodType.setType(), 30, props);
				case PRESSURE_PLATE -> new PressurePlateBlock(woodType.setType(), props);
				case DOOR -> new DoorBlock(woodType.setType(), props);
				case TRAPDOOR -> new TrapDoorBlock(woodType.setType(), props);
			};
			registry.registerBlock(companion, block, id.getPath(), variant.tab(), createItemProperties(block, id));
		}
	}

	private net.minecraft.world.level.block.state.properties.WoodType getWoodTypeForSounds(AdditionalBlockInformation info) {
		if (info.overworldLike) return WoodType.ACACIA;
		if (info.netherLike) return WoodType.CRIMSON;
		if (info.bambooLike) return WoodType.BAMBOO; // assuming you have a flag; adjust if needed
		return WoodType.CHERRY;
	}

	private BlockSetType blockSetType(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block.information.blockSetType == null) return BlockSetType.OAK;
		return VanillaBlockSetTypes.get(block.information.blockSetType);
	}

	private WoodType woodType(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block.information.woodType == null) return WoodType.OAK;
		return VanillaWoodTypes.get(block.information.woodType);
	}

	private boolean isDyable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return block.additional_information != null && block.additional_information.dyable;
	}

	private boolean isSittable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return SeatLogic.seatsOf(block) != null;
	}

	private boolean isWaterloggable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return block.additional_information != null && block.additional_information.waterloggable;
	}

	private boolean isPowerable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return block.information != null && block.information.powerable;
	}

	private boolean isToggleable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return block.information != null && block.information.toggleable;
	}

	/**
	 * Registers a block that declares {@code behaviour.container}, together with the block entity holding
	 * its slots. Container blocks get their own classes so that no other Obsidian block has to carry an
	 * {@code EntityBlock} implementation it would never use.
	 */
	private void registerContainerBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                    BlockBehaviour.Properties props, Item.Properties itemProps,
	                                    RegistryHelperBlockExpanded registry, ResourceKey<CreativeModeTab> itemGroup,
	                                    boolean facing) {
		Block impl = facing
				? new HorizontalFacingContainerBlockImpl(blockId, block, props)
				: new ContainerBlockImpl(blockId, block, props);

		Block registered = registry.registerBlock(impl, block, blockId.getPath(), itemProps, itemGroup);
		REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new ContainerBlockEntity(blockId, pos, state), registered),
				blockId.getPath() + "_be");
	}

	private void registerDyableBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                 BlockBehaviour.Properties props, Item.Properties itemProps,
	                                 RegistryHelperBlockExpanded registry) {
		Block registered = registry.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(blockId, block, props));
		registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
		REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
				blockId.getPath() + "_be");
	}

	private void registerDyableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                           BlockBehaviour.Properties props, Item.Properties itemProps,
	                                           RegistryHelperBlockExpanded registry) {
		Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
				new HorizontalFacingDyableBlockImpl(blockId, block, props));
		registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
		REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
				blockId.getPath() + "_be");
	}

	private void registerSittableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                             BlockBehaviour.Properties props, Item.Properties itemProps,
	                                             RegistryHelperBlockExpanded registry) {
		Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
				new HorizontalFacingSittableBlock(block, props));
		registry.registerItem(new CustomBlockItem(block, registered, itemProps), blockId.getPath()); // or use appropriate item registration
	}

	private void registerDyableAndSittableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                                      BlockBehaviour.Properties props, Item.Properties itemProps,
	                                                      RegistryHelperBlockExpanded registry) {
		Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
				new HorizontalFacingSittableAndDyableBlock(blockId, block, props));
		registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
		REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
				blockId.getPath() + "_be");
	}

	private List<Identifier> getOxidationStageIds(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		List<Identifier> ids = new ArrayList<>();
		block.oxidizable_properties.stages.forEach(stage ->
				stage.blocks.forEach(varBlock -> {
					if (!ids.contains(varBlock.id)) ids.add(varBlock.id);
				}));
		return ids;
	}

	private void registerToContentRegistries(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Identifier blockId) {
		if (block.getBlockType() == BlockType.OXIDIZING_BLOCK) {
			getOxidationStageIds(block).forEach(id -> {
				if (ContentRegistries.BLOCKS.get(id) != null) {
					register(ContentRegistries.BLOCKS, "block", id, block);
				}
			});
		} else {
			register(ContentRegistries.BLOCKS, "block", blockId, block);
		}
	}

	/**
	 * Fills anything this block left out from the {@code block/template} it names. The block's own values
	 * always win; fields it does not declare fall back to the template's.
	 *
	 * <p>Gson cannot tell a field left out from one written with its default value, so fields with a
	 * non-null default ({@code block_type}, {@code has_item}, {@code cake_slices}, …) are inherited only
	 * while the block still holds that default. A block cannot re-assert a default its template overrides.
	 */
	private void applyTemplate(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block.template == null || block.template.isBlank()) return;

		Identifier templateId = Identifier.tryParse(block.template);
		if (templateId == null) {
			Obsidian.LOGGER.warn("Block has invalid template reference: {}", block.template);
			return;
		}

		io.github.vampirestudios.obsidian.api.obsidian.block.Block template =
				ContentRegistries.BLOCK_TEMPLATES.getValue(templateId);
		if (template == null) {
			Obsidian.LOGGER.warn("Block references unknown template: {}", templateId);
			return;
		}

		if (block.description == null) block.description = template.description;
		if (isDefaultBlockType(block.block_type)) block.block_type = template.block_type;
		if (block.menuConfig == null) block.menuConfig = template.menuConfig;
		if (block.blocks == null) block.blocks = template.blocks;
		if (block.behaviour == null) block.behaviour = template.behaviour;
		if (block.dropInformation == null) block.dropInformation = template.dropInformation;
		if (block.additional_information == null) block.additional_information = template.additional_information;
		if (block.functions == null) block.functions = template.functions;
		if (block.components == null) block.components = template.components;
		if (block.food_information == null) block.food_information = template.food_information;
		if (block.campfire_properties == null) block.campfire_properties = template.campfire_properties;
		if (block.particle_type == null) block.particle_type = template.particle_type;
		if (block.growable == null) block.growable = template.growable;
		if (block.bushProperties == null) block.bushProperties = template.bushProperties;
		if (block.oxidizable_properties == null) block.oxidizable_properties = template.oxidizable_properties;
		if (block.multi_block_information == null) block.multi_block_information = template.multi_block_information;
		if (block.placeable_feature == null) block.placeable_feature = template.placeable_feature;
		if (block.paintingTableInformation == null) block.paintingTableInformation = template.paintingTableInformation;

		if ((block.lore == null || block.lore.isEmpty()) && template.lore != null) block.lore = template.lore;
		if ((block.can_plant_on == null || block.can_plant_on.isEmpty()) && template.can_plant_on != null) {
			block.can_plant_on = template.can_plant_on;
		}

		// Template provides defaults, the block's own entries take priority.
		if (template.events != null && block.events != null) template.events.forEach(block.events::putIfAbsent);
		else if (block.events == null) block.events = template.events;

		if (block.rendering == null) block.rendering = template.rendering;

		// Information is merged field by field so the block keeps its own name and id. A block with no
		// information of its own is only given one when the template has something to put in it.
		if (block.information == null && template.information != null) block.information = new BlockInformation();
		if (block.information != null) mergeInformation(block.information, template.information);
	}

	/**
	 * Whether {@code block_type} is still what a file that never mentioned it would hold. Only then is the
	 * template's type inherited.
	 */
	private boolean isDefaultBlockType(String blockType) {
		return blockType == null || blockType.isBlank()
				|| blockType.equalsIgnoreCase(BlockType.BLOCK.name());
	}

	private void mergeInformation(BlockInformation info, BlockInformation template) {
		if (template == null) return;

		// info.id is transient and per-block; info.name is the block's own identity.
		if (info.name == null) info.name = template.name;

		if (info.blockSetType == null) info.blockSetType = template.blockSetType;
		if (info.woodType == null) info.woodType = template.woodType;
		if (info.parentBlock == null) info.parentBlock = template.parentBlock;
		if (info.shape == null) info.shape = template.shape;
		if (info.shapes == null) info.shapes = template.shapes;
		if (info.collisionShape == null) info.collisionShape = template.collisionShape;
		if (info.outlineShape == null) info.outlineShape = template.outlineShape;
		if (info.placementShapes == null) info.placementShapes = template.placementShapes;
		if (info.partShapes == null) info.partShapes = template.partShapes;
		if (info.removedTooltipSections == null) info.removedTooltipSections = template.removedTooltipSections;
		if (info.blockSettings == null) info.blockSettings = template.blockSettings;
		if (info.itemSettings == null) info.itemSettings = template.itemSettings;
		if (info.properties == null) info.properties = template.properties;
		if (info.defaultValue == null) info.defaultValue = template.defaultValue;
		if (info.vanillaProperties == null) info.vanillaProperties = template.vanillaProperties;

		// Defaulted primitives: inherited only while the block still holds the format default.
		if (info.cake_slices == 1) info.cake_slices = template.cake_slices;
		if (info.has_item) info.has_item = template.has_item;
		if (info.wooden_button) info.wooden_button = template.wooden_button;
		if (!info.powerable) info.powerable = template.powerable;
		if (!info.toggleable) info.toggleable = template.toggleable;
	}

	private io.github.vampirestudios.obsidian.api.obsidian.block.Block getBlock(IAddonPack addon, File file) throws IOException {
		return AddonFormats.read(addon, file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
	}

	private SoundType determineSoundType(AdditionalBlockInformation info) {
		return info.overworldLike ? SoundType.OVERWORLD : (info.netherLike ? SoundType.NETHER : SoundType.BAMBOO);
	}

	@Override
	public String getType() {
		return "block";
	}

	private WoodType getWoodTypeSpecificSounds(SoundType soundType) {
		if (soundType == SoundType.OVERWORLD) {
			return WoodType.ACACIA;
		} else if (soundType == SoundType.NETHER) {
			return WoodType.CRIMSON;
		} else if (soundType == SoundType.BAMBOO) {
			return WoodType.BAMBOO;
		} else if (soundType == SoundType.CHERRY) {
			return WoodType.CHERRY;
		} else {
			return WoodType.ACACIA;
		}
	}

	private enum SoundType {
		OVERWORLD,
		NETHER,
		BAMBOO,
		CHERRY
	}

}
