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

import java.util.List;
import java.util.Map;

public class BlockInformation {

    public String material = "minecraft:stone";
    public boolean has_advanced_bounding_box = false;
    public float[] bounding_box = new float[] {0, 0, 0, 16, 16, 16};
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
    public boolean wearable = false;
    public Identifier wearableModel;

    public List<ItemStack.TooltipSection> getRemovedTooltipSections() {
        return List.of();
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
            return switch (sound_group) {
                case "minecraft:wood" -> BlockSoundGroup.WOOD;
                case "minecraft:gravel" -> BlockSoundGroup.GRAVEL;
                case "minecraft:grass" -> BlockSoundGroup.GRASS;
                case "minecraft:lily_pad" -> BlockSoundGroup.LILY_PAD;
                case "minecraft:metal" -> BlockSoundGroup.METAL;
                case "minecraft:glass" -> BlockSoundGroup.GLASS;
                case "minecraft:wool" -> BlockSoundGroup.WOOL;
                case "minecraft:sand" -> BlockSoundGroup.SAND;
                case "minecraft:snow" -> BlockSoundGroup.SNOW;
                case "minecraft:powder_snow" -> BlockSoundGroup.POWDER_SNOW;
                case "minecraft:ladder" -> BlockSoundGroup.LADDER;
                case "minecraft:anvil" -> BlockSoundGroup.ANVIL;
                case "minecraft:slime" -> BlockSoundGroup.SLIME;
                case "minecraft:honey" -> BlockSoundGroup.HONEY;
                case "minecraft:wet_grass" -> BlockSoundGroup.WET_GRASS;
                case "minecraft:coral" -> BlockSoundGroup.CORAL;
                case "minecraft:bamboo" -> BlockSoundGroup.BAMBOO;
                case "minecraft:bamboo_sapling" -> BlockSoundGroup.BAMBOO_SAPLING;
                case "minecraft:scaffolding" -> BlockSoundGroup.SCAFFOLDING;
                case "minecraft:sweet_berry_bush" -> BlockSoundGroup.SWEET_BERRY_BUSH;
                case "minecraft:crop" -> BlockSoundGroup.CROP;
                case "minecraft:stem" -> BlockSoundGroup.STEM;
                case "minecraft:vine" -> BlockSoundGroup.VINE;
                case "minecraft:nether_wart" -> BlockSoundGroup.NETHER_WART;
                case "minecraft:lantern" -> BlockSoundGroup.LANTERN;
                case "minecraft:nether_stem" -> BlockSoundGroup.NETHER_STEM;
                case "minecraft:nylium" -> BlockSoundGroup.NYLIUM;
                case "minecraft:fungus" -> BlockSoundGroup.FUNGUS;
                case "minecraft:roots" -> BlockSoundGroup.ROOTS;
                case "minecraft:shroomlight" -> BlockSoundGroup.SHROOMLIGHT;
                case "minecraft:weeping_vines" -> BlockSoundGroup.WEEPING_VINES;
                case "minecraft:weeping_vines_low_pitch" -> BlockSoundGroup.WEEPING_VINES_LOW_PITCH;
                case "minecraft:soul_sand" -> BlockSoundGroup.SOUL_SAND;
                case "minecraft:soul_soil" -> BlockSoundGroup.SOUL_SOIL;
                case "minecraft:basalt" -> BlockSoundGroup.BASALT;
                case "minecraft:wart_block" -> BlockSoundGroup.WART_BLOCK;
                case "minecraft:netherrack" -> BlockSoundGroup.NETHERRACK;
                case "minecraft:nether_bricks" -> BlockSoundGroup.NETHER_BRICKS;
                case "minecraft:nether_sprouts" -> BlockSoundGroup.NETHER_SPROUTS;
                case "minecraft:nether_ore" -> BlockSoundGroup.NETHER_ORE;
                case "minecraft:bone" -> BlockSoundGroup.BONE;
                case "minecraft:netherite" -> BlockSoundGroup.NETHERITE;
                case "minecraft:ancient_debris" -> BlockSoundGroup.ANCIENT_DEBRIS;
                case "minecraft:lodestone" -> BlockSoundGroup.LODESTONE;
                case "minecraft:chain" -> BlockSoundGroup.CHAIN;
                case "minecraft:nether_gold_ore" -> BlockSoundGroup.NETHER_GOLD_ORE;
                case "minecraft:gilded_blackstone" -> BlockSoundGroup.GILDED_BLACKSTONE;
                case "minecraft:candle" -> BlockSoundGroup.CANDLE;
                case "minecraft:amethyst_block" -> BlockSoundGroup.AMETHYST_BLOCK;
                case "minecraft:amethyst_cluster" -> BlockSoundGroup.AMETHYST_CLUSTER;
                case "minecraft:small_amethyst_bud" -> BlockSoundGroup.SMALL_AMETHYST_BUD;
                case "minecraft:medium_amethyst_bud" -> BlockSoundGroup.MEDIUM_AMETHYST_BUD;
                case "minecraft:large_amethyst_bud" -> BlockSoundGroup.LARGE_AMETHYST_BUD;
                case "minecraft:tuff" -> BlockSoundGroup.TUFF;
                case "minecraft:calcite" -> BlockSoundGroup.CALCITE;
                case "minecraft:dripstone_block" -> BlockSoundGroup.DRIPSTONE_BLOCK;
                case "minecraft:pointed_dripstone" -> BlockSoundGroup.POINTED_DRIPSTONE;
                case "minecraft:copper" -> BlockSoundGroup.COPPER;
                case "minecraft:cave_vines" -> BlockSoundGroup.CAVE_VINES;
                case "minecraft:spore_blossom" -> BlockSoundGroup.SPORE_BLOSSOM;
                case "minecraft:azalea" -> BlockSoundGroup.AZALEA;
                case "minecraft:flowering_azalea" -> BlockSoundGroup.FLOWERING_AZALEA;
                case "minecraft:moss_carpet" -> BlockSoundGroup.MOSS_CARPET;
                case "minecraft:moss_block" -> BlockSoundGroup.MOSS_BLOCK;
                case "minecraft:big_dripleaf" -> BlockSoundGroup.BIG_DRIPLEAF;
                case "minecraft:small_dripleaf" -> BlockSoundGroup.SMALL_DRIPLEAF;
                case "minecraft:rooted_dirt" -> BlockSoundGroup.ROOTED_DIRT;
                case "minecraft:hanging_roots" -> BlockSoundGroup.HANGING_ROOTS;
                case "minecraft:azalea_leaves" -> BlockSoundGroup.AZALEA_LEAVES;
                case "minecraft:sculk_sensor" -> BlockSoundGroup.SCULK_SENSOR;
                case "minecraft:glow_lichen" -> BlockSoundGroup.GLOW_LICHEN;
                case "minecraft:deepslate" -> BlockSoundGroup.DEEPSLATE;
                case "minecraft:deepslate_bricks" -> BlockSoundGroup.DEEPSLATE_BRICKS;
                case "minecraft:deepslate_tiles" -> BlockSoundGroup.DEEPSLATE_TILES;
                case "minecraft:polished_deepslate" -> BlockSoundGroup.POLISHED_DEEPSLATE;
                default -> BlockSoundGroup.STONE;
            };
        }
    }

    public Material getMaterial() {
        if (custom_material != null) {
            return new Material(custom_material.getMapColor(), custom_material.liquid, custom_material.solid,
                    custom_material.allows_movement, custom_material.allows_light, custom_material.burnable,
                    custom_material.replaceable, custom_material.getPistonBehavior());
        } else {
            return switch (material) {
                case "minecraft:structure_void" -> Material.STRUCTURE_VOID;
                case "minecraft:portal" -> Material.PORTAL;
                case "minecraft:carpet" -> Material.CARPET;
                case "minecraft:plant" -> Material.PLANT;
                case "minecraft:underwater_plant" -> Material.UNDERWATER_PLANT;
                case "minecraft:replaceable_plant" -> Material.REPLACEABLE_PLANT;
                case "minecraft:nether_shoots" -> Material.NETHER_SHOOTS;
                case "minecraft:replaceable_underwater_plant" -> Material.REPLACEABLE_UNDERWATER_PLANT;
                case "minecraft:water" -> Material.WATER;
                case "minecraft:bubble_column" -> Material.BUBBLE_COLUMN;
                case "minecraft:lava" -> Material.LAVA;
                case "minecraft:snow_layer" -> Material.SNOW_LAYER;
                case "minecraft:fire" -> Material.FIRE;
                case "minecraft:decoration" -> Material.DECORATION;
                case "minecraft:cobweb" -> Material.COBWEB;
                case "minecraft:redstone_lamp" -> Material.REDSTONE_LAMP;
                case "minecraft:organic_product" -> Material.ORGANIC_PRODUCT;
                case "minecraft:soil" -> Material.SOIL;
                case "minecraft:solid_organic" -> Material.SOLID_ORGANIC;
                case "minecraft:dense_ice" -> Material.DENSE_ICE;
                case "minecraft:aggregate" -> Material.AGGREGATE;
                case "minecraft:sponge" -> Material.SPONGE;
                case "minecraft:shulker_box" -> Material.SHULKER_BOX;
                case "minecraft:wood" -> Material.WOOD;
                case "minecraft:nether_wood" -> Material.NETHER_WOOD;
                case "minecraft:bamboo_sapling" -> Material.BAMBOO_SAPLING;
                case "minecraft:bamboo" -> Material.BAMBOO;
                case "minecraft:wool" -> Material.WOOL;
                case "minecraft:tnt" -> Material.TNT;
                case "minecraft:leaves" -> Material.LEAVES;
                case "minecraft:glass" -> Material.GLASS;
                case "minecraft:ice" -> Material.ICE;
                case "minecraft:cactus" -> Material.CACTUS;
                case "minecraft:stone" -> Material.STONE;
                case "minecraft:metal" -> Material.METAL;
                case "minecraft:snow_block" -> Material.SNOW_BLOCK;
                case "minecraft:repair_station" -> Material.REPAIR_STATION;
                case "minecraft:barrier" -> Material.BARRIER;
                case "minecraft:piston" -> Material.PISTON;
                case "minecraft:moss_block" -> Material.MOSS_BLOCK;
                case "minecraft:gourd" -> Material.GOURD;
                case "minecraft:egg" -> Material.EGG;
                case "minecraft:cake" -> Material.CAKE;
                case "minecraft:amethyst" -> Material.AMETHYST;
                case "minecraft:powdered_snow" -> Material.POWDER_SNOW;
                default -> Material.AIR;
            };
        }
    }

    public RenderLayer getRenderLayer() {
        return switch(renderLayer) {
            case "SOLID" -> RenderLayer.getSolid();
            case "CUTOUT" -> RenderLayer.getCutout();
            case "CUTOUT_MIPPED" -> RenderLayer.getCutoutMipped();
            case "TRANSLUCENT" -> RenderLayer.getTranslucent();
            case "TRANSLUCENT_MOVING_BLOCK" -> RenderLayer.getTranslucentMovingBlock();
            case "TRANSLUCENT_NO_CRUMBLING" -> RenderLayer.getTranslucentNoCrumbling();
            case "WATER_MASK" -> RenderLayer.getWaterMask();
            case "END_PORTAL" -> RenderLayer.getEndPortal();
            case "END_GATEWAY" -> RenderLayer.getEndGateway();
            case "GLINT" -> RenderLayer.getGlint();
            case "DIRECT_GLINT" -> RenderLayer.getDirectGlint();
            case "TRIPWIRE" -> RenderLayer.getTripwire();
            default -> throw new IllegalStateException("Unexpected value: " + renderLayer);
        };
    }

    public ItemGroup getItemGroup() {
        return Obsidian.ITEM_GROUP_REGISTRY.get(item_group);
    }

}