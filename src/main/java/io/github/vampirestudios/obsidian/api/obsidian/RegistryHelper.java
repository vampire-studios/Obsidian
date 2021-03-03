//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian.api.obsidian;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Settings;
import net.minecraft.potion.Potion;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.registry.Registry;

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

    public Block registerBlock(Block block, String name, ItemGroup itemGroup) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        BlockItem item = new BlockItem(block, (new Settings()).group(itemGroup));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
        return block;
    }

    public Block registerBlock(Block block, String name, ItemGroup itemGroup, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        BlockItem item = new BlockItem(block, settings.group(itemGroup));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
        return block;
    }

    public Block registerBlock(Block block, String name, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new BlockItem(block, settings));
        return block;
    }

    public Block registerBlockWithWallBlock(Block block, Block wallBlock, String name) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        Registry.register(Registry.ITEM, new Identifier(this.modId, name), new WallStandingBlockItem(block, wallBlock, (new Settings()).group(ItemGroup.DECORATIONS)));
        return block;
    }

    public Block registerNetherStem(String name, MapColor materialColor) {
        return this.registerBlock(new PillarBlock(net.minecraft.block.AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
            return materialColor;
        }).strength(1.0F).sounds(BlockSoundGroup.NETHER_STEM)), name, ItemGroup.BUILDING_BLOCKS);
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

    public Block registerBlockWithoutItem(Block block, String name) {
        Registry.register(Registry.BLOCK, new Identifier(this.modId, name), block);
        return block;
    }

    public Item registerItem(Item item, String name) {
        return Registry.register(Registry.ITEM, new Identifier(this.modId, name), item);
    }

    public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Builder<T> builder, String name) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(this.modId, name), blockEntityType);
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
