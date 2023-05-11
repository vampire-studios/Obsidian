package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class RegistryHelperBlockExpanded extends RegistryHelper.Blocks {
	private final String modId;

	public RegistryHelperBlockExpanded(String modId) {
		super(modId);
		this.modId = modId;
	}

	public Block registerBlock(Block block, String name, ResourceKey<CreativeModeTab> itemGroup) {
		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(this.modId, name), block);
		Item item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(this.modId, name), new BlockItem(block, new Properties()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
		return block;
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ResourceKey<CreativeModeTab> itemGroup) {
		registerBlockWithoutItem(name, block);
		Item item = register(BuiltInRegistries.ITEM, name, new CustomBlockItem(block2, block, new Properties()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ResourceKey<CreativeModeTab> itemGroup, Properties settings) {
		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(this.modId, name), block);
		Item item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(this.modId, name), new CustomBlockItem(block2, block, settings));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
	}

	public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Properties settings) {
		if (BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(this.modId, name)))
			block = BuiltInRegistries.BLOCK.get(new ResourceLocation(this.modId, name));
		else block = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(this.modId, name), block);
		if (block2.information.has_item) {
			Item item;
			if (BuiltInRegistries.ITEM.containsKey(new ResourceLocation(this.modId, name))) item = BuiltInRegistries.ITEM.get(new ResourceLocation(this.modId, name));
			else item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(this.modId, name), new CustomBlockItem(block2, block, settings));
			ItemGroupEvents.modifyEntriesEvent(block2.information.itemProperties.getItemGroup()).register(entries -> entries.accept(item));
		}
		return block;
	}

	public void registerHangingTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Properties settings) {
		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(this.modId, name), block);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(this.modId, name), new HangingTallBlockItem(block2, block, settings));
	}

	public void registerLeavesBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Properties settings) {
		Block leavesBlock = new LeavesBlock(
				BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT)
						.strength(0.2F)
						.randomTicks()
						.sound(block2.information.blockProperties.getBlockSoundGroup())
						.noOcclusion()
						.isValidSpawn((state, world, pos, type) -> type == EntityType.OCELOT || type == EntityType.PARROT)
						.isSuffocating((state, world, pos) -> false)
						.isViewBlocking((state, world, pos) -> false)
						.ignitedByLava()
						.pushReaction(PushReaction.DESTROY)
		);
		Block block = registerBlockWithoutItem(name, leavesBlock);
		if (block2.information.has_item) registerItem(new CustomBlockItem(block2, block, settings), name);
	}

	public void registerLog(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String name, MapColor topMapColor, MapColor sideMapColor, Properties settings) {
		this.registerBlock(new PillarBlockImpl(block, BlockBehaviour.Properties.of().mapColor((state) ->
						state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
				.strength(2.0F).sound(SoundType.WOOD)), block, name, settings);
	}

	public void registerNetherStemBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String name, MapColor mapColor, Properties settings) {
		this.registerBlock(new PillarBlockImpl(block, BlockBehaviour.Properties.of().mapColor(blockState -> mapColor).strength(2.0F).sound(SoundType.STEM)),
				block, name, settings);
	}

	public Block registerDoubleBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Properties itemGroup) {
		register(BuiltInRegistries.BLOCK, name, block);
		register(BuiltInRegistries.ITEM, name, new CustomTallBlockItem(block2, block, itemGroup));
		return block;
	}

	public Item registerItem(Item item, String name, ResourceKey<CreativeModeTab> itemGroup) {
		register(BuiltInRegistries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
		return item;
	}

	public Item registerItem(Item item, String name) {
		register(BuiltInRegistries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> entries.accept(item));
		return item;
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name) {
		register(BuiltInRegistries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.block.additional_information.defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerDyeableItem(DyeableItemImpl item, String name) {
		register(BuiltInRegistries.ITEM, name, item);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.item.information.defaultColor);
			entries.accept(stack);
		});
		return item;
	}

}
