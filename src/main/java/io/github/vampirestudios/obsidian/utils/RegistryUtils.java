package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.api.ExtendedRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryUtils {

	private RegistryUtils() {
	}

	// ---- REGISTRY MANIPULATION ----

	public static void unfreeze(Registry<?> registry) {
		((ExtendedRegistry<?>) registry).obsidian$unfreeze();
	}

	@SuppressWarnings("unchecked")
	public static <T> void remove(Registry<T> registry, RegistryKey<T> key) {
		((ExtendedRegistry<T>) registry).obsidian$remove(key);
	}

	@SuppressWarnings("unchecked")
	public static void remove(Registry<?> registry, Identifier id) {
		remove((Registry<Object>) registry, (RegistryKey<Object>) RegistryKey.of(registry.getKey(), id));
	}

	// ---- REGISTER STUFF ----

	public static Block register(Block block, Identifier name) {
		register(block, name, ItemGroup.DECORATIONS);
		return block;
	}

	public static Block register(String name, Block block) {
		register(block, Const.id(name), ItemGroup.DECORATIONS);
		return block;
	}

	public static Block register(Block block, Identifier name, ItemGroup itemGroup) {
		Registry.register(Registry.BLOCK, name, block);
		BlockItem item = new BlockItem(block, (new Settings()).group(itemGroup));
		item.appendBlocks(Item.BLOCK_ITEMS, item);
		Registry.register(Registry.ITEM, name, item);
		return block;
	}

	public static Block registerBlockWithoutItem(Block block, Identifier identifier) {
		Registry.register(Registry.BLOCK, identifier, block);
		return block;
	}

	public static Block registerBlockWithoutItem(String name, Block block) {
		Registry.register(Registry.BLOCK, Const.id(name), block);
		return block;
	}

	public static Item registerItem(Item item, Identifier name) {
		if (Registry.ITEM.containsId(name)) {
			return Registry.ITEM.get(name);
		} else {
			return Registry.register(Registry.ITEM, name, item);
		}
	}

	public static Item registerItem(String name, Item item) {
		return Registry.register(Registry.ITEM, Const.id(name), item);
	}

	public static Block registerBlockWithWallBlock(Block block, Block wallBlock, Identifier name) {
		Registry.register(Registry.BLOCK, name, block);
		Registry.register(
				Registry.ITEM,
				name,
				new WallStandingBlockItem(
						block,
						wallBlock,
						new Settings().group(ItemGroup.DECORATIONS)
				)
		);
		return block;
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Builder<T> builder, Identifier name) {
		BlockEntityType<T> blockEntityType = builder.build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, name, blockEntityType);
		return blockEntityType;
	}

	public static Block registerNetherStem(Identifier name, MapColor MapColor) {
		return register(
				new PillarBlock(
						AbstractBlock.Settings.of(
								Material.WOOD,
								(blockState) -> MapColor
						).strength(1.0F).sounds(BlockSoundGroup.NETHER_STEM)
				),
				name
		);
	}

	public static Block registerLog(Identifier name, MapColor MapColor, MapColor MapColor2) {
		return register(
				new PillarBlock(
						AbstractBlock.Settings.of(
								Material.WOOD,
								(blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor : MapColor2
						).strength(2.0F).sounds(BlockSoundGroup.WOOD)
				),
				name
		);
	}

	public static Block registerNetherStem(String name, MapColor MapColor) {
		return register(
				new PillarBlock(
						AbstractBlock.Settings.of(
								Material.WOOD,
								(blockState) -> MapColor
						).strength(1.0F).sounds(BlockSoundGroup.NETHER_STEM)
				),
				Const.id(name)
		);
	}

	public static Block registerLog(String name, MapColor MapColor, MapColor MapColor2) {
		return register(
				new PillarBlock(
						AbstractBlock.Settings.of(
								Material.WOOD,
								(blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor : MapColor2
						).strength(2.0F).sounds(BlockSoundGroup.WOOD)
				),
				Const.id(name)
		);
	}

}
