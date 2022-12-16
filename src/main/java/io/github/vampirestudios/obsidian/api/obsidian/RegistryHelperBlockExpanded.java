package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class RegistryHelperBlockExpanded extends RegistryHelper.Blocks {
	private final String modId;

	public RegistryHelperBlockExpanded(String modId) {
		super(modId);
		this.modId = modId;
	}

	public Block registerBlock(Block block, String name, ItemGroup itemGroup) {
		Registry.register(Registries.BLOCK, new Identifier(this.modId, name), block);
		Item item = Registry.register(Registries.ITEM, new Identifier(this.modId, name), new BlockItem(block, new Settings()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
		return block;
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup) {
		registerBlockWithoutItem(name, block);
		Item item = register(Registries.ITEM, name, new CustomBlockItem(block2, block, new Settings()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup, Settings settings) {
		Registry.register(Registries.BLOCK, new Identifier(this.modId, name), block);
		Item item = Registry.register(Registries.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
	}

	public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
		if (Registries.BLOCK.containsId(new Identifier(this.modId, name)))
			block = Registries.BLOCK.get(new Identifier(this.modId, name));
		else block = Registry.register(Registries.BLOCK, new Identifier(this.modId, name), block);
		if (block2.information.has_item) {
			if (Registries.ITEM.containsId(new Identifier(this.modId, name))) Registries.ITEM.get(new Identifier(this.modId, name));
			else Registry.register(Registries.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings));
		}
		return block;
	}

	public void registerHangingTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings settings) {
		Registry.register(Registries.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registries.ITEM, new Identifier(this.modId, name), new HangingTallBlockItem(block2, block, settings));
	}

	public void registerLeavesBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings settings) {
		Block block = registerBlockWithoutItem(name, new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F)
				.ticksRandomly().sounds(block2.information.blockProperties.getBlockSoundGroup()).nonOpaque()
				.allowsSpawning((state, world, pos, type) -> type == EntityType.OCELOT || type == EntityType.PARROT)
				.suffocates((state, world, pos) -> false)
				.blockVision((state, world, pos) -> false)));
		if (block2.information.has_item) registerItem(new CustomBlockItem(block2, block, settings), name);
	}

	public void registerLog(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String name, MapColor topMapColor, MapColor sideMapColor, Settings settings) {
		this.registerBlock(new PillarBlockImpl(block, AbstractBlock.Settings.of(Material.WOOD, (state) ->
						state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
				.strength(2.0F).sounds(BlockSoundGroup.WOOD)), block, name, settings);
	}

	public void registerNetherStemBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String name, MapColor mapColor, Settings settings) {
		this.registerBlock(new PillarBlockImpl(block, AbstractBlock.Settings.of(Material.NETHER_WOOD, (state) -> mapColor).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)),
				block, name, settings);
	}

	public Block registerDoubleBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings itemGroup) {
		register(Registries.BLOCK, name, block);
		register(Registries.ITEM, name, new CustomTallBlockItem(block2, block, itemGroup));
		return block;
	}

	public Item registerItem(Item item, String name, ItemGroup itemGroup) {
		register(Registries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
		return item;
	}

	public Item registerItem(Item item, String name) {
		register(Registries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(item));
		return item;
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name) {
		register(Registries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.block.additional_information.defaultColor);
			entries.add(stack);
		});
		return item;
	}

	public Item registerDyeableItem(DyeableItemImpl item, String name) {
		register(Registries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.item.information.defaultColor);
			entries.add(stack);
		});
		return item;
	}

}
