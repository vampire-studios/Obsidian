//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomBlockItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.HangingTallBlockItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.TallBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Settings;
import net.minecraft.potion.Potion;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.registry.Registry;

import java.util.function.BiFunction;

public class RegistryHelper {
    private final String modId;

    RegistryHelper(String modId) {
        this.modId = modId;
    }

    public static RegistryHelper createRegistryHelper(String modId) {
        return new RegistryHelper(modId);
    }

    public Block registerBlock(Block block, String name) {
        this.registerBlock(block, name, ItemGroup.DECORATIONS);
        return block;
    }

    public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name) {
        this.registerBlock(block, block2, name, ItemGroup.DECORATIONS);
        return block;
    }

    public Block registerBlock(Block block, String name, ItemGroup itemGroup) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new BlockItem(block, new Settings().group(itemGroup)));
        return block;
    }


    public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, new Settings().group(itemGroup)));
        return block;
    }

    public Block registerBlock(Block block, String name, ItemGroup itemGroup, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new BlockItem(block, settings.group(itemGroup)));
        return block;
    }

    public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, ItemGroup itemGroup, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings.group(itemGroup)));
        return block;
    }

    public Block registerBlock(Block block, String name, Item item) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
        return block;
    }

    public Block registerBlock(Block block, String name, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new BlockItem(block, settings));
        return block;
    }

    public Block register(Block block, String id, Item.Settings settings, BiFunction<Block, Settings, Item> item) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, id), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, id), item.apply(block, settings));
        return block;
    }

    public Block registerBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
        if (Registry.BLOCK.containsId(new Identifier(this.modId, name))) block = Registry.BLOCK.get(new Identifier(this.modId, name));
        else block = Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        if (block2.information.has_item) if (Registry.ITEM.containsId(new Identifier(this.modId, name))) Registry.ITEM.get(new Identifier(this.modId, name));
        else Registry.register(Registry.ITEM, new Identifier(this.modId, name), new CustomBlockItem(block2, block, settings));
        return block;
    }

    public Block registerBlockWithWallBlock(Block block, Block wallBlock, String name) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new WallStandingBlockItem(block, wallBlock, (new Settings()).group(ItemGroup.DECORATIONS)));
        return block;
    }

    public Block registerTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new TallBlockItem(block2, block, new Settings().group(ItemGroup.DECORATIONS)));
        return block;
    }

    public Block registerTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new TallBlockItem(block2, block, settings));
        return block;
    }

    public Block registerHangingTallBlock(Block block, io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new HangingTallBlockItem(block2, block, settings));
        return block;
    }

    public Block registerNetherStem(String name, MapColor materialColor) {
        return this.registerBlock(new PillarBlock(net.minecraft.block.AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
            return materialColor;
        }).strength(1.0F).sounds(BlockSoundGroup.NETHER_STEM)), name, ItemGroup.BUILDING_BLOCKS);
    }

    public Block registerLeavesBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block2, String name, Item.Settings settings) {
        Block block = registerBlockWithoutItem(new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F)
                .ticksRandomly().sounds(block2.information.getBlockSoundGroup()).nonOpaque()
                .allowsSpawning((state, world, pos, type) -> type == EntityType.OCELOT || type == EntityType.PARROT)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false)), name);
        if (block2.information.has_item) registerItem(new CustomBlockItem(block2, block, settings), name);
        return block;
    }

    public Block registerLog(String name, MapColor materialColor, MapColor materialColor2) {
        return this.registerBlock(new PillarBlock(net.minecraft.block.AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
            return blockState.get(PillarBlock.AXIS) == Axis.Y ? materialColor : materialColor2;
        }).strength(2.0F).sounds(BlockSoundGroup.WOOD)), name);
    }

    public Block registerLog(String name, MapColor materialColor, MapColor materialColor2, Item.Settings settings) {
        return this.registerBlock(new PillarBlock(net.minecraft.block.AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
            return blockState.get(PillarBlock.AXIS) == Axis.Y ? materialColor : materialColor2;
        }).strength(2.0F).sounds(BlockSoundGroup.WOOD)), name, settings);
    }

    public void registerLog(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String name, MapColor topMapColor, MapColor sideMapColor, Settings settings) {
        this.registerBlock(new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, (state) ->
            state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
                .strength(2.0F).sounds(BlockSoundGroup.WOOD)), block, name, settings);
    }

    public Block registerBlockWithoutItem(Block block, String name) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        return block;
    }

    public Item registerItem(Item item, String name) {
        return Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
    }

    public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(FabricBlockEntityTypeBuilder<T> builder, String name) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(this.modId, name), blockEntityType);
        return blockEntityType;
    }

    public Block registerCompatBlock(String modName, String blockName, Block block, ItemGroup itemGroup) {
        return !FabricLoader.getInstance().isModLoaded(modName) ? this.registerBlock(block, blockName, itemGroup) : null;
    }

    public Item registerCompatItem(String modName, String itemName, Settings settings, ItemGroup itemGroup) {
        return !FabricLoader.getInstance().isModLoaded(modName) ? this.registerItem(new Item(settings.group(itemGroup)), itemName) : null;
    }

    public SoundEvent createSoundEvent(String name) {
        return Registry.register(Registry.SOUND_EVENT, name, new SoundEvent(new Identifier(this.modId, name)));
    }

    public SoundEvent registerSoundEvent(SoundEvent soundEvent, String name) {
        return Registry.register(Registry.SOUND_EVENT, new Identifier(this.modId, name), soundEvent);
    }

    public Item registerSpawnEgg(String name, EntityType<? extends MobEntity> entity, int primaryColor, int secondaryColor) {
        return this.registerItem(new SpawnEggItem(entity, primaryColor, secondaryColor, (new Settings()).group(ItemGroup.MISC)), name + "_spawn_egg");
    }

    public Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registry.POTION, new Identifier(this.modId, name), potion);
    }
}
