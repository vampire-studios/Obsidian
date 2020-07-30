package io.github.vampirestudios.obsidian.api_new.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.api.NameInformation;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlockInformation {

    public static final MapCodec<BlockInformation> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.STRING.fieldOf("material").forGetter((weather) -> {
            return weather.material;
        }), NameInformation.CODEC.fieldOf("name").forGetter((weather) -> {
            return weather.name;
        }), Codec.STRING.fieldOf("item_group").forGetter((weather) -> {
            return weather.item_group;
        }), Codec.BOOL.fieldOf("collidable").forGetter((weather) -> {
            return weather.collidable;
        }), Codec.STRING.fieldOf("sound_group").forGetter((weather) -> {
            return weather.sound_group;
        }), Codec.INT.fieldOf("luminance").forGetter((weather) -> {
            return weather.luminance;
        }), Codec.FLOAT.fieldOf("explosion_resistance").forGetter((weather) -> {
            return weather.explosion_resistance;
        }), Codec.FLOAT.fieldOf("destroy_time").forGetter((weather) -> {
            return weather.destroy_time;
        }), Codec.BOOL.fieldOf("random_ticks").forGetter((weather) -> {
            return weather.random_ticks;
        }), Codec.FLOAT.fieldOf("slipperiness").forGetter((weather) -> {
            return weather.slipperiness;
        }), Identifier.CODEC.fieldOf("drop").forGetter((weather) -> {
            return weather.drop;
        }), Codec.BOOL.fieldOf("dynamic_bounds").forGetter((weather) -> {
            return weather.dynamic_bounds;
        }), ListCodec.INT.fieldOf("bounding_boxes").forGetter((weather) -> {
            return weather.bounding_boxes;
        }), Codec.BOOL.fieldOf("collidable").forGetter((weather) -> {
            return weather.collidable;
        }), Codec.BOOL.fieldOf("collidable").forGetter((weather) -> {
            return weather.collidable;
        })).apply(instance, BlockInformation::new);
    });

    public String material;
    public NameInformation name;
    public String item_group;
    public boolean collidable = true;
    public String sound_group = "";
    public int luminance = 0;
    public float explosion_resistance = 0.0F;
    public float destroy_time = 0.0F;
    public boolean random_ticks = false;
    public float slipperiness = 0.0F;
    public Identifier drop = new Identifier("minecraft:stone");
    public boolean dynamic_bounds = false;
    public Integer[] bounding_boxes = new Integer[] {
            0, 0, 0, 16, 16, 16
    };
    public String action = "";
    public float jumpVelocityMultiplier;

    public BlockInformation(String material, NameInformation name, String item_group, boolean collidable, String sound_group, int luminance, float explosion_resistance,
                            float destroy_time, boolean random_ticks, float slipperiness, Identifier drop, boolean dynamic_bounds, Integer[] bounding_boxes, String action,
                            float jumpVelocityMultiplier) {
        this.material = material;
        this.name = name;
        this.item_group = item_group;
        this.collidable = collidable;
        this.sound_group = sound_group;
        this.luminance = luminance;
        this.explosion_resistance = explosion_resistance;
        this.destroy_time = destroy_time;
        this.random_ticks = random_ticks;
        this.slipperiness = slipperiness;
        this.drop = drop;
        this.dynamic_bounds = dynamic_bounds;
        this.bounding_boxes = bounding_boxes;
        this.action = action;
        this.jumpVelocityMultiplier = jumpVelocityMultiplier;
    }

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
            case "minecraft:supported":
                return Material.SUPPORTED;
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
            case "minecraft:cake":
                return Material.CAKE;
            case "minecraft:air":
            default:
                return Material.AIR;
        }
    }

    public ItemGroup getItem_group() {
        switch (item_group) {
            case "minecraft:building_blocks":
                return ItemGroup.BUILDING_BLOCKS;
            case "minecraft:decorations":
                return ItemGroup.DECORATIONS;
            case "minecraft:redstone":
                return ItemGroup.REDSTONE;
            case "minecraft:transportation":
                return ItemGroup.TRANSPORTATION;
            case "minecraft:misc":
                return ItemGroup.MISC;
            case "minecraft:food":
                return ItemGroup.FOOD;
            case "minecraft:tools":
                return ItemGroup.TOOLS;
            case "minecraft:combat":
                return ItemGroup.COMBAT;
            case "minecraft:brewing":
                return ItemGroup.BREWING;
            case "minecraft:search":
            default:
                return ItemGroup.SEARCH;
        }
    }

}