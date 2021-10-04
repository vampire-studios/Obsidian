package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public record RegistryHelper(String modId) {
	public static RegistryHelper createRegistryHelper(String modId) {
		return new RegistryHelper(modId);
	}

	public Block registerBlock(Block block, String name, ItemGroup itemGroup) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registry.ITEM, new Identifier(this.modId, name), new BlockItem(block, new Settings().group(itemGroup)));
		return block;
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, new Settings().group(itemGroup)));
	}

	public void registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup, Settings settings) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings.group(itemGroup)));
	}

	public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
		if (Registry.BLOCK.containsId(new Identifier(this.modId, name)))
			block = Registry.BLOCK.get(new Identifier(this.modId, name));
		else block = Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		if (block2.information.has_item) {
			if (Registry.ITEM.containsId(new Identifier(this.modId, name))) Registry.ITEM.get(new Identifier(this.modId, name));
			else Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings));
		}
		return block;
	}

	public void registerTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings settings) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registry.ITEM, new Identifier(this.modId, name), new TallBlockItem(block2, block, settings));
	}

	public void registerHangingTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings settings) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		Registry.register(Registry.ITEM, new Identifier(this.modId, name), new HangingTallBlockItem(block2, block, settings));
	}

	public void registerLeavesBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Settings settings) {
		Block block = registerBlockWithoutItem(new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F)
				.ticksRandomly().sounds(block2.information.getBlockSoundGroup()).nonOpaque()
				.allowsSpawning((state, world, pos, type) -> type == EntityType.OCELOT || type == EntityType.PARROT)
				.suffocates((state, world, pos) -> false)
				.blockVision((state, world, pos) -> false)), name);
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

	public Block registerBlockWithoutItem(Block block, String name) {
		Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
		return block;
	}

	public Item registerItem(Item item, String name) {
		return Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
	}

	public <T extends BlockEntity> void registerBlockEntity(FabricBlockEntityTypeBuilder<T> builder, String name) {
		BlockEntityType<T> blockEntityType = builder.build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(this.modId, name), blockEntityType);
	}

}
