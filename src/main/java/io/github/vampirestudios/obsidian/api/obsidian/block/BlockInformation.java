package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.BlockProperty;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockInformation {

    public String material = "minecraft:stone";
    public CustomMaterial custom_material;
    public NameInformation name;
    public Identifier item_group;
    public boolean collidable = true;
    public String sound_group = "minecraft:stone";
    public CustomSoundGroup custom_sound_group;
    public float hardness = 3.0F;
    public float resistance = 3.0F;
    public boolean randomTicks = false;
    public boolean instant_break = false;
    public float slipperiness = 0.6F;
    public Identifier drop = new Identifier("minecraft:stone");
    public float velocity_modifier = 1.0F;
    public float jump_velocity_modifier = 1.0F;
    public boolean has_glint = false;
    public boolean is_enchantable = false;
    public int enchantability = 5;
    public int luminance = 0;
    public boolean is_emissive = false;
    public boolean fireproof = false;
    public boolean translucent = false;
    public boolean dynamic_boundaries = false;
    public int cake_slices = 1;
    public boolean has_item = true;
    public Map<String, BlockProperty> properties;
    public String renderLayer = "SOLID";
    public List<String> removedTooltipSections;
    public boolean requires_mod_loaded = false;
    public String required_mod_id;

    public List<ItemStack.TooltipSection> getRemovedTooltipSections() {
        return new ArrayList<>();
    }

    public BlockSoundGroup getBlockSoundGroup() {
        if (custom_sound_group != null) {
            SoundEvent breakSound = Registry.SOUND_EVENT.get(custom_sound_group.break_sound);
            SoundEvent stepSound = Registry.SOUND_EVENT.get(custom_sound_group.step_sound);
            SoundEvent placeSound = Registry.SOUND_EVENT.get(custom_sound_group.place_sound);
            SoundEvent hitSound = Registry.SOUND_EVENT.get(custom_sound_group.hit_sound);
            SoundEvent fallSound = Registry.SOUND_EVENT.get(custom_sound_group.fall_sound);
            return new BlockSoundGroup(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
        } else {
            switch (sound_group) {
                case "minecraft:wood": return BlockSoundGroup.WOOD;
                case "minecraft:gravel": return BlockSoundGroup.GRAVEL;
                case "minecraft:grass": return BlockSoundGroup.GRASS;
                case "minecraft:lily_pad": return BlockSoundGroup.LILY_PAD;
                case "minecraft:metal": return BlockSoundGroup.METAL;
                case "minecraft:glass": return BlockSoundGroup.GLASS;
                case "minecraft:wool": return BlockSoundGroup.WOOL;
                case "minecraft:sand": return BlockSoundGroup.SAND;
                case "minecraft:snow": return BlockSoundGroup.SNOW;
                case "minecraft:ladder": return BlockSoundGroup.LADDER;
                case "minecraft:anvil": return BlockSoundGroup.ANVIL;
                case "minecraft:slime": return BlockSoundGroup.SLIME;
                case "minecraft:honey": return BlockSoundGroup.HONEY;
                case "minecraft:wet_grass": return BlockSoundGroup.WET_GRASS;
                case "minecraft:coral": return BlockSoundGroup.CORAL;
                case "minecraft:bamboo": return BlockSoundGroup.BAMBOO;
                case "minecraft:bamboo_sapling": return BlockSoundGroup.BAMBOO_SAPLING;
                case "minecraft:scaffolding": return BlockSoundGroup.SCAFFOLDING;
                case "minecraft:sweet_berry_bush": return BlockSoundGroup.SWEET_BERRY_BUSH;
                case "minecraft:crop": return BlockSoundGroup.CROP;
                case "minecraft:stem": return BlockSoundGroup.STEM;
                case "minecraft:vine": return BlockSoundGroup.VINE;
                case "minecraft:nether_wart": return BlockSoundGroup.NETHER_WART;
                case "minecraft:lantern": return BlockSoundGroup.LANTERN;
                case "minecraft:nether_stem": return BlockSoundGroup.NETHER_STEM;
                case "minecraft:nylium": return BlockSoundGroup.NYLIUM;
                case "minecraft:fungus": return BlockSoundGroup.FUNGUS;
                case "minecraft:roots": return BlockSoundGroup.ROOTS;
                case "minecraft:shroomlight": return BlockSoundGroup.SHROOMLIGHT;
                case "minecraft:weeping_vines": return BlockSoundGroup.WEEPING_VINES;
                case "minecraft:weeping_vines_low_pitch": return BlockSoundGroup.WEEPING_VINES_LOW_PITCH;
                case "minecraft:soul_sand": return BlockSoundGroup.SOUL_SAND;
                case "minecraft:soul_soil": return BlockSoundGroup.SOUL_SOIL;
                case "minecraft:basalt": return BlockSoundGroup.BASALT;
                case "minecraft:wart_block": return BlockSoundGroup.WART_BLOCK;
                case "minecraft:netherrack": return BlockSoundGroup.NETHERRACK;
                case "minecraft:nether_bricks": return BlockSoundGroup.NETHER_BRICKS;
                case "minecraft:nether_sprouts": return BlockSoundGroup.NETHER_SPROUTS;
                case "minecraft:nether_ore": return BlockSoundGroup.NETHER_ORE;
                case "minecraft:bone": return BlockSoundGroup.BONE;
                case "minecraft:netherite": return BlockSoundGroup.NETHERITE;
                case "minecraft:ancient_debris": return BlockSoundGroup.ANCIENT_DEBRIS;
                case "minecraft:lodestone": return BlockSoundGroup.LODESTONE;
                case "minecraft:chain": return BlockSoundGroup.CHAIN;
                case "minecraft:nether_gold_ore": return BlockSoundGroup.NETHER_GOLD_ORE;
                case "minecraft:gilded_blackstone": return BlockSoundGroup.GILDED_BLACKSTONE;
                default: return BlockSoundGroup.STONE;
            }
        }
    }

    public Material getMaterial() {
        if (custom_material != null) {
            return new Material(custom_material.getMapColor(), custom_material.liquid, custom_material.solid,
                    custom_material.allows_movement, custom_material.allows_light, custom_material.burnable,
                    custom_material.replaceable, custom_material.getPistonBehavior());
        } else {
            switch (material) {
                case "minecraft:structure_void": return Material.STRUCTURE_VOID;
                case "minecraft:portal": return Material.PORTAL;
                case "minecraft:carpet": return Material.CARPET;
                case "minecraft:plant": return Material.PLANT;
                case "minecraft:underwater_plant": return Material.UNDERWATER_PLANT;
                case "minecraft:replaceable_plant": return Material.REPLACEABLE_PLANT;
                case "minecraft:nether_shoots": return Material.NETHER_SHOOTS;
                case "minecraft:replaceable_underwater_plant": return Material.REPLACEABLE_UNDERWATER_PLANT;
                case "minecraft:water": return Material.WATER;
                case "minecraft:bubble_column": return Material.BUBBLE_COLUMN;
                case "minecraft:lava": return Material.LAVA;
                case "minecraft:snow_layer": return Material.SNOW_LAYER;
                case "minecraft:fire": return Material.FIRE;
                case "minecraft:decoration": return Material.DECORATION;
                case "minecraft:cobweb": return Material.COBWEB;
                case "minecraft:redstone_lamp": return Material.REDSTONE_LAMP;
                case "minecraft:organic_product": return Material.ORGANIC_PRODUCT;
                case "minecraft:soil": return Material.SOIL;
                case "minecraft:solid_organic": return Material.SOLID_ORGANIC;
                case "minecraft:dense_ice": return Material.DENSE_ICE;
                case "minecraft:aggregate": return Material.AGGREGATE;
                case "minecraft:sponge": return Material.SPONGE;
                case "minecraft:shulker_box": return Material.SHULKER_BOX;
                case "minecraft:wood": return Material.WOOD;
                case "minecraft:nether_wood": return Material.NETHER_WOOD;
                case "minecraft:bamboo_sapling": return Material.BAMBOO_SAPLING;
                case "minecraft:bamboo": return Material.BAMBOO;
                case "minecraft:wool": return Material.WOOL;
                case "minecraft:tnt": return Material.TNT;
                case "minecraft:leaves": return Material.LEAVES;
                case "minecraft:glass": return Material.GLASS;
                case "minecraft:ice": return Material.ICE;
                case "minecraft:cactus": return Material.CACTUS;
                case "minecraft:stone": return Material.STONE;
                case "minecraft:metal": return Material.METAL;
                case "minecraft:snow_block": return Material.SNOW_BLOCK;
                case "minecraft:repair_station": return Material.REPAIR_STATION;
                case "minecraft:barrier": return Material.BARRIER;
                case "minecraft:piston": return Material.PISTON;
                case "minecraft:moss_block": return Material.MOSS_BLOCK;
                case "minecraft:gourd": return Material.GOURD;
                case "minecraft:egg": return Material.EGG;
                case "minecraft:cake": return Material.CAKE;
                default: return Material.AIR;
            }
        }
    }

    public RenderLayer getRenderLayer() {
        switch(renderLayer) {
            case "SOLID": return RenderLayer.getSolid();
            case "CUTOUT": return RenderLayer.getCutout();
            case "CUTOUT_MIPPED": return RenderLayer.getCutoutMipped();
            case "TRANSLUCENT": return RenderLayer.getTranslucent();
            case "TRANSLUCENT_MOVING_BLOCK": return RenderLayer.getTranslucentMovingBlock();
            case "TRANSLUCENT_NO_CRUMBLING": return RenderLayer.getTranslucentNoCrumbling();
            case "WATER_MASK": return RenderLayer.getWaterMask();
            case "END_PORTAL": return RenderLayer.getEndPortal(2);
            case "GLINT": return RenderLayer.getGlint();
            case "DIRECT_GLINT": return RenderLayer.getDirectGlint();
            case "TRIPWIRE": return RenderLayer.getTripwire();
            default: throw new IllegalStateException("Unexpected value: " + renderLayer);
        }
    }

    public ItemGroup getItemGroup() {
        return Obsidian.ITEM_GROUP_REGISTRY.get(item_group);
    }

}