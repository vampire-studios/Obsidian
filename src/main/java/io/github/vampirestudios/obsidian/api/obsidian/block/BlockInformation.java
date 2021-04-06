package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlockInformation {

    public String material = "minecraft:stone";
    public NameInformation name;
    public Identifier item_group;
    public boolean collidable = true;
    public String sound_group = "minecraft:stone";
    public float explosion_resistance = 0.0F;
    public float destroy_time = 0.0F;
    public boolean randomTicks = false;
    public float slipperiness = 0.6F;
    public Identifier drop = new Identifier("minecraft:stone");
    public boolean dynamicBounds = false;
    public String action = "";
    public boolean is_bouncy = false;
    public float jump_velocity_modifier = 1.0F;
    public boolean has_glint = false;
    public boolean is_enchantable = false;
    public int enchantability = 5;
    public boolean is_emissive = false;
    public boolean fireproof = false;
    public float light_absorption_value = 0.0F;
    public boolean translucent = false;
    public int cake_slices = 1;
    public boolean has_item = true;

    public BlockSoundGroup getBlockSoundGroup() {
        switch (sound_group) {
            case "minecraft:wood":
                return BlockSoundGroup.WOOD;
            case "minecraft:gravel":
                return BlockSoundGroup.GRAVEL;
            case "minecraft:grass":
                return BlockSoundGroup.GRASS;
            case "minecraft:metal":
                return BlockSoundGroup.METAL;
            case "minecraft:glass":
                return BlockSoundGroup.GLASS;
            case "minecraft:wool":
                return BlockSoundGroup.WOOL;
            case "minecraft:sand":
                return BlockSoundGroup.SAND;
            case "minecraft:snow":
                return BlockSoundGroup.SNOW;
            case "minecraft:ladder":
                return BlockSoundGroup.LADDER;
            case "minecraft:anvil":
                return BlockSoundGroup.ANVIL;
            case "minecraft:slime":
                return BlockSoundGroup.SLIME;
            case "minecraft:wet_grass":
                return BlockSoundGroup.WET_GRASS;
            case "minecraft:coral":
                return BlockSoundGroup.CORAL;
            case "minecraft:bamboo":
                return BlockSoundGroup.BAMBOO;
            case "minecraft:bamboo_sapling":
                return BlockSoundGroup.BAMBOO_SAPLING;
            case "minecraft:scaffolding":
                return BlockSoundGroup.SCAFFOLDING;
            case "minecraft:sweet_berry_bush":
                return BlockSoundGroup.SWEET_BERRY_BUSH;
            case "minecraft:crop":
                return BlockSoundGroup.CROP;
            case "minecraft:stem":
                return BlockSoundGroup.STEM;
            case "minecraft:nether_wart":
                return BlockSoundGroup.NETHER_WART;
            case "minecraft:lantern":
                return BlockSoundGroup.LANTERN;
            case "minecraft:sculk_sensor":
                return BlockSoundGroup.SCULK_SENSOR;
            case "minecraft:copper":
                return BlockSoundGroup.COPPER;
            case "minecraft:calcite":
                return BlockSoundGroup.CALCITE;
            case "minecraft:pointed_dripstone":
                return BlockSoundGroup.POINTED_DRIPSTONE;
            case "minecraft:dripstone_block":
                return BlockSoundGroup.DRIPSTONE_BLOCK;
            case "minecraft:tuff":
                return BlockSoundGroup.TUFF;
            case "minecraft:small_amethyst_bud":
                return BlockSoundGroup.SMALL_AMETHYST_BUD;
            case "minecraft:medium_amethyst_bud":
                return BlockSoundGroup.MEDIUM_AMETHYST_BUD;
            case "minecraft:large_amethyst_bud":
                return BlockSoundGroup.LARGE_AMETHYST_BUD;
            case "minecraft:amethyst_cluster":
                return BlockSoundGroup.AMETHYST_CLUSTER;
            case "minecraft:amethyst_block":
                return BlockSoundGroup.AMETHYST_BLOCK;
            case "minecraft:deepslate":
                return BlockSoundGroup.DEEPSLATE;
            case "minecraft:deepslate_bricks":
                return BlockSoundGroup.DEEPSLATE_BRICKS;
            case "minecraft:deepslate_tiles":
                return BlockSoundGroup.DEEPSLATE_TILES;
            case "minecraft:polished_deepslate":
                return BlockSoundGroup.POLISHED_DEEPSLATE;
            case "minecraft:azalea":
                return BlockSoundGroup.AZALEA;
            case "minecraft:azalea_leaves":
                return BlockSoundGroup.AZALEA_LEAVES;
            case "minecraft:flowering_azalea":
                return BlockSoundGroup.FLOWERING_AZALEA;
            case "minecraft:candle":
                return BlockSoundGroup.CANDLE;
            case "minecraft:stone":
            default:
                return BlockSoundGroup.STONE;
        }
    }

    public Material getMaterial() {
        switch (material) {
            case "minecraft:structure_void":
                return Material.STRUCTURE_VOID;
            case "minecraft:portal":
                return Material.PORTAL;
            case "minecraft:carpet":
                return Material.CARPET;
            case "minecraft:plant":
                return Material.PLANT;
            case "minecraft:underwater_plant":
                return Material.UNDERWATER_PLANT;
            case "minecraft:replaceable_plant":
                return Material.REPLACEABLE_PLANT;
            case "minecraft:replaceable_underwater_plant":
                return Material.REPLACEABLE_UNDERWATER_PLANT;
            case "minecraft:water":
                return Material.WATER;
            case "minecraft:bubble_column":
                return Material.BUBBLE_COLUMN;
            case "minecraft:lava":
                return Material.LAVA;
            case "minecraft:snow_layer":
                return Material.SNOW_LAYER;
            case "minecraft:fire":
                return Material.FIRE;
            case "minecraft:decoration":
                return Material.DECORATION;
            case "minecraft:cobweb":
                return Material.COBWEB;
            case "minecraft:redstone_lamp":
                return Material.REDSTONE_LAMP;
            case "minecraft:organic_product":
                return Material.ORGANIC_PRODUCT;
            case "minecraft:soil":
                return Material.SOIL;
            case "minecraft:solid_organic":
                return Material.SOLID_ORGANIC;
            case "minecraft:dense_ice":
                return Material.DENSE_ICE;
            case "minecraft:aggregate":
                return Material.AGGREGATE;
            case "minecraft:sponge":
                return Material.SPONGE;
            case "minecraft:shulker_box":
                return Material.SHULKER_BOX;
            case "minecraft:wood":
                return Material.WOOD;
            case "minecraft:bamboo_sapling":
                return Material.BAMBOO_SAPLING;
            case "minecraft:bamboo":
                return Material.BAMBOO;
            case "minecraft:wool":
                return Material.WOOL;
            case "minecraft:tnt":
                return Material.TNT;
            case "minecraft:leaves":
                return Material.LEAVES;
            case "minecraft:glass":
                return Material.GLASS;
            case "minecraft:ice":
                return Material.ICE;
            case "minecraft:cactus":
                return Material.CACTUS;
            case "minecraft:stone":
                return Material.STONE;
            case "minecraft:metal":
                return Material.METAL;
            case "minecraft:snow_block":
                return Material.SNOW_BLOCK;
            case "minecraft:repair_station":
                return Material.REPAIR_STATION;
            case "minecraft:barrier":
                return Material.BARRIER;
            case "minecraft:piston":
                return Material.PISTON;
            case "minecraft:unused_plant":
                return Material.UNUSED_PLANT;
            case "minecraft:gourd":
                return Material.GOURD;
            case "minecraft:egg":
                return Material.EGG;
            case "minecraft:amethyst":
                return Material.AMETHYST;
            case "minecraft:powdered_snow":
                return Material.POWDER_SNOW;
            case "minecraft:air":
            default:
                return Material.AIR;
        }
    }

    public ItemGroup getItemGroup() {
        return Obsidian.ITEM_GROUP_REGISTRY.get(item_group);
    }

}