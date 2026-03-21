package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
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
		Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(this.modId, name), block);
		Item item = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name), new BlockItem(block, new Properties()
				.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name)))));
		CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
		return block;
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ResourceKey<CreativeModeTab> itemGroup) {
		registerBlockWithoutItem(name, block);
		Item item = register(BuiltInRegistries.ITEM, name, new CustomBlockItem(block2, block, new Properties()
				.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name)))));
		CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ResourceKey<CreativeModeTab> itemGroup, Properties settings) {
		Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(this.modId, name), block);
		Item item = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name), new CustomBlockItem(block2, block, settings));
		CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
	}

	public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Properties settings, ResourceKey<CreativeModeTab> itemGroup) {
		if (BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(this.modId, name)))
			block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(this.modId, name));
		else block = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(this.modId, name), block);

		if (block2.information.has_item) {
			Item item;
			if (BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(this.modId, name))) item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(this.modId, name));
			else item = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name), new CustomBlockItem(block2, block, settings));
			CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
		}
		return block;
	}

	public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Properties settings) {
		if (BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(this.modId, name)))
			block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(this.modId, name));
		else block = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(this.modId, name), block);

		if (block2.information.has_item) {
			Item item;
			if (BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(this.modId, name))) item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(this.modId, name));
			else item = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name), new CustomBlockItem(block2, block, settings));
			if (block2.information.getItemSettings() != null) CreativeModeTabEvents.modifyOutputEvent(block2.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(item));
		}
		return block;
	}

	public void registerHangingTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Properties settings) {
		Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(this.modId, name), block);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.modId, name), new HangingTallBlockItem(block2, block, settings));
	}

	public void registerLeavesBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Properties settings) {
		Block leavesBlock = new TintedParticleLeavesBlock(
				0.0F,
				BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT)
						.strength(0.2F)
						.randomTicks()
						.sound(block2.information.getBlockSettings().getBlockSoundGroup())
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

	public void registerLog(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, BlockBehaviour.Properties properties, String name, MapColor topMapColor, MapColor sideMapColor, Properties settings) {
		this.registerBlock(new PillarBlockImpl(block, properties.mapColor((state) ->
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
		CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
		return item;
	}

	public Item registerItem(Item item, String name) {
		register(BuiltInRegistries.ITEM, name, item);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> entries.accept(item));
		return item;
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name) {
		register(BuiltInRegistries.ITEM, name, item);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			stack.set(DataComponents.DYED_COLOR, new DyedItemColor(item.block.additional_information.defaultColor));
			entries.accept(stack);
		});
		return item;
	}

}
