package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockInformation {
    public Identifier parentBlock;

    public BoundingBox boundingBox;
    public boolean has_advanced_bounding_box = false;
    public float[] bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] north_bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] south_bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] east_bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] west_bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] up_bounding_box = new float[] {0, 0, 0, 16, 16, 16};
    public float[] down_bounding_box = new float[] {0, 0, 0, 16, 16, 16};

    public NameInformation name;

    public int cake_slices = 1;

    public boolean has_item = true;

    public String renderLayer = "SOLID";

    public List<String> removedTooltipSections;

    public boolean requires_mod_loaded = false;
    public String required_mod_id;

    public boolean wearable = false;
    public Identifier wearableModel;

    @SerializedName("block_properties") public BlockProperties blockProperties;
    @SerializedName("item_properties") public ItemProperties itemProperties;

    public List<ItemStack.TooltipSection> getRemovedTooltipSections() {
        return List.of();
    }

    /*public RenderLayer getRenderLayer() {
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
    }*/

    public static class BlockProperties {
        private static final Map<Identifier, Material> vanillaMaterials = new HashMap<>();
        private static final Map<Identifier, BlockSoundGroup> vanillaSoundGroups = new HashMap<>();

        static {
            vanillaMaterials.put(new Identifier("structure_void"), Material.STRUCTURE_VOID);
            vanillaMaterials.put(new Identifier("portal"), Material.PORTAL);
            vanillaMaterials.put(new Identifier("carpet"), Material.CARPET);
            vanillaMaterials.put(new Identifier("plant"), Material.PLANT);
            vanillaMaterials.put(new Identifier("underwater_plant"), Material.UNDERWATER_PLANT);
            vanillaMaterials.put(new Identifier("replaceable_plant"), Material.REPLACEABLE_PLANT);
            vanillaMaterials.put(new Identifier("nether_shoots"), Material.NETHER_SHOOTS);
            vanillaMaterials.put(new Identifier("replaceable_underwater_plant"), Material.REPLACEABLE_UNDERWATER_PLANT);
            vanillaMaterials.put(new Identifier("water"), Material.WATER);
            vanillaMaterials.put(new Identifier("bubble_column"), Material.BUBBLE_COLUMN);
            vanillaMaterials.put(new Identifier("lava"), Material.LAVA);
            vanillaMaterials.put(new Identifier("snow_layer"), Material.SNOW_LAYER);
            vanillaMaterials.put(new Identifier("fire"), Material.FIRE);
            vanillaMaterials.put(new Identifier("decoration"), Material.DECORATION);
            vanillaMaterials.put(new Identifier("cobweb"), Material.COBWEB);
            vanillaMaterials.put(new Identifier("redstone_lamp"), Material.REDSTONE_LAMP);
            vanillaMaterials.put(new Identifier("organic_product"), Material.ORGANIC_PRODUCT);
            vanillaMaterials.put(new Identifier("soil"), Material.SOIL);
            vanillaMaterials.put(new Identifier("solid_organic"), Material.SOLID_ORGANIC);
            vanillaMaterials.put(new Identifier("dense_ice"), Material.DENSE_ICE);
            vanillaMaterials.put(new Identifier("aggregate"), Material.AGGREGATE);
            vanillaMaterials.put(new Identifier("sponge"), Material.SPONGE);
            vanillaMaterials.put(new Identifier("shulker_box"), Material.SHULKER_BOX);
            vanillaMaterials.put(new Identifier("wood"), Material.WOOD);
            vanillaMaterials.put(new Identifier("nether_wood"), Material.NETHER_WOOD);
            vanillaMaterials.put(new Identifier("bamboo_sapling"), Material.BAMBOO_SAPLING);
            vanillaMaterials.put(new Identifier("bamboo"), Material.BAMBOO);
            vanillaMaterials.put(new Identifier("wool"), Material.WOOL);
            vanillaMaterials.put(new Identifier("tnt"), Material.TNT);
            vanillaMaterials.put(new Identifier("leaves"), Material.LEAVES);
            vanillaMaterials.put(new Identifier("glass"), Material.GLASS);
            vanillaMaterials.put(new Identifier("ice"), Material.ICE);
            vanillaMaterials.put(new Identifier("cactus"), Material.CACTUS);
            vanillaMaterials.put(new Identifier("stone"), Material.STONE);
            vanillaMaterials.put(new Identifier("metal"), Material.METAL);
            vanillaMaterials.put(new Identifier("snow_block"), Material.SNOW_BLOCK);
            vanillaMaterials.put(new Identifier("repair_station"), Material.REPAIR_STATION);
            vanillaMaterials.put(new Identifier("barrier"), Material.BARRIER);
            vanillaMaterials.put(new Identifier("piston"), Material.PISTON);
            vanillaMaterials.put(new Identifier("moss_block"), Material.MOSS_BLOCK);
            vanillaMaterials.put(new Identifier("gourd"), Material.GOURD);
            vanillaMaterials.put(new Identifier("egg"), Material.EGG);
            vanillaMaterials.put(new Identifier("cake"), Material.CAKE);
            vanillaMaterials.put(new Identifier("amethyst"), Material.AMETHYST);
            vanillaMaterials.put(new Identifier("powder_snow"), Material.POWDER_SNOW);
            vanillaMaterials.put(new Identifier("air"), Material.AIR);
            vanillaMaterials.put(new Identifier("sculk"), Material.SCULK);
            vanillaMaterials.put(new Identifier("frog_spawn"), Material.FROG_SPAWN);
            vanillaMaterials.put(new Identifier("froglight"), Material.FROGLIGHT);

            vanillaSoundGroups.put(new Identifier("wood"), BlockSoundGroup.WOOD);
            vanillaSoundGroups.put(new Identifier("gravel"), BlockSoundGroup.GRAVEL);
            vanillaSoundGroups.put(new Identifier("grass"), BlockSoundGroup.GRASS);
            vanillaSoundGroups.put(new Identifier("lily_pad"), BlockSoundGroup.LILY_PAD);
            vanillaSoundGroups.put(new Identifier("metal"), BlockSoundGroup.METAL);
            vanillaSoundGroups.put(new Identifier("glass"), BlockSoundGroup.GLASS);
            vanillaSoundGroups.put(new Identifier("wool"), BlockSoundGroup.WOOL);
            vanillaSoundGroups.put(new Identifier("sand"), BlockSoundGroup.SAND);
            vanillaSoundGroups.put(new Identifier("snow"), BlockSoundGroup.SNOW);
            vanillaSoundGroups.put(new Identifier("powder_snow"), BlockSoundGroup.POWDER_SNOW);
            vanillaSoundGroups.put(new Identifier("ladder"), BlockSoundGroup.LADDER);
            vanillaSoundGroups.put(new Identifier("anvil"), BlockSoundGroup.ANVIL);
            vanillaSoundGroups.put(new Identifier("snow"), BlockSoundGroup.SNOW);
            vanillaSoundGroups.put(new Identifier("slime"), BlockSoundGroup.SLIME);
            vanillaSoundGroups.put(new Identifier("honey"), BlockSoundGroup.HONEY);
            vanillaSoundGroups.put(new Identifier("wet_grass"), BlockSoundGroup.WET_GRASS);
            vanillaSoundGroups.put(new Identifier("coral"), BlockSoundGroup.CORAL);
            vanillaSoundGroups.put(new Identifier("bamboo"), BlockSoundGroup.BAMBOO);
            vanillaSoundGroups.put(new Identifier("bamboo_sapling"), BlockSoundGroup.BAMBOO_SAPLING);
            vanillaSoundGroups.put(new Identifier("scaffolding"), BlockSoundGroup.SCAFFOLDING);
            vanillaSoundGroups.put(new Identifier("sweet_berry_bush"), BlockSoundGroup.SWEET_BERRY_BUSH);
            vanillaSoundGroups.put(new Identifier("crop"), BlockSoundGroup.CROP);
            vanillaSoundGroups.put(new Identifier("stem"), BlockSoundGroup.STEM);
            vanillaSoundGroups.put(new Identifier("vine"), BlockSoundGroup.VINE);
            vanillaSoundGroups.put(new Identifier("nether_wart"), BlockSoundGroup.NETHER_WART);
            vanillaSoundGroups.put(new Identifier("lantern"), BlockSoundGroup.LANTERN);
            vanillaSoundGroups.put(new Identifier("nether_stem"), BlockSoundGroup.NETHER_STEM);
            vanillaSoundGroups.put(new Identifier("nylium"), BlockSoundGroup.NYLIUM);
            vanillaSoundGroups.put(new Identifier("fungus"), BlockSoundGroup.FUNGUS);
            vanillaSoundGroups.put(new Identifier("roots"), BlockSoundGroup.ROOTS);
            vanillaSoundGroups.put(new Identifier("shroomlight"), BlockSoundGroup.SHROOMLIGHT);
            vanillaSoundGroups.put(new Identifier("weeping_vines"), BlockSoundGroup.WEEPING_VINES);
            vanillaSoundGroups.put(new Identifier("weeping_vines_low_pitch"), BlockSoundGroup.WEEPING_VINES_LOW_PITCH);
            vanillaSoundGroups.put(new Identifier("soul_sand"), BlockSoundGroup.SOUL_SAND);
            vanillaSoundGroups.put(new Identifier("soul_soil"), BlockSoundGroup.SOUL_SOIL);
            vanillaSoundGroups.put(new Identifier("basalt"), BlockSoundGroup.BASALT);
            vanillaSoundGroups.put(new Identifier("wart_block"), BlockSoundGroup.WART_BLOCK);
            vanillaSoundGroups.put(new Identifier("netherrack"), BlockSoundGroup.NETHERRACK);
            vanillaSoundGroups.put(new Identifier("nether_bricks"), BlockSoundGroup.NETHER_BRICKS);
            vanillaSoundGroups.put(new Identifier("nether_sprouts"), BlockSoundGroup.NETHER_SPROUTS);
            vanillaSoundGroups.put(new Identifier("nether_ore"), BlockSoundGroup.NETHER_ORE);
            vanillaSoundGroups.put(new Identifier("bone"), BlockSoundGroup.BONE);
            vanillaSoundGroups.put(new Identifier("netherite"), BlockSoundGroup.NETHERITE);
            vanillaSoundGroups.put(new Identifier("ancient_debris"), BlockSoundGroup.ANCIENT_DEBRIS);
            vanillaSoundGroups.put(new Identifier("lodestone"), BlockSoundGroup.LODESTONE);
            vanillaSoundGroups.put(new Identifier("chain"), BlockSoundGroup.CHAIN);
            vanillaSoundGroups.put(new Identifier("nether_gold_ore"), BlockSoundGroup.NETHER_GOLD_ORE);
            vanillaSoundGroups.put(new Identifier("gilded_blackstone"), BlockSoundGroup.GILDED_BLACKSTONE);
            vanillaSoundGroups.put(new Identifier("candle"), BlockSoundGroup.CANDLE);
            vanillaSoundGroups.put(new Identifier("amethyst_block"), BlockSoundGroup.AMETHYST_BLOCK);
            vanillaSoundGroups.put(new Identifier("amethyst_cluster"), BlockSoundGroup.AMETHYST_CLUSTER);
            vanillaSoundGroups.put(new Identifier("small_amethyst_bud"), BlockSoundGroup.SMALL_AMETHYST_BUD);
            vanillaSoundGroups.put(new Identifier("medium_amethyst_bud"), BlockSoundGroup.MEDIUM_AMETHYST_BUD);
            vanillaSoundGroups.put(new Identifier("large_amethyst_bud"), BlockSoundGroup.LARGE_AMETHYST_BUD);
            vanillaSoundGroups.put(new Identifier("tuff"), BlockSoundGroup.TUFF);
            vanillaSoundGroups.put(new Identifier("calcite"), BlockSoundGroup.CALCITE);
            vanillaSoundGroups.put(new Identifier("dripstone_block"), BlockSoundGroup.DRIPSTONE_BLOCK);
            vanillaSoundGroups.put(new Identifier("pointed_dripstone"), BlockSoundGroup.POINTED_DRIPSTONE);
            vanillaSoundGroups.put(new Identifier("copper"), BlockSoundGroup.COPPER);
            vanillaSoundGroups.put(new Identifier("cave_vines"), BlockSoundGroup.CAVE_VINES);
            vanillaSoundGroups.put(new Identifier("spore_blossom"), BlockSoundGroup.SPORE_BLOSSOM);
            vanillaSoundGroups.put(new Identifier("azalea"), BlockSoundGroup.AZALEA);
            vanillaSoundGroups.put(new Identifier("flowering_azalea"), BlockSoundGroup.FLOWERING_AZALEA);
            vanillaSoundGroups.put(new Identifier("moss_carpet"), BlockSoundGroup.MOSS_CARPET);
            vanillaSoundGroups.put(new Identifier("moss_block"), BlockSoundGroup.MOSS_BLOCK);
            vanillaSoundGroups.put(new Identifier("big_dripleaf"), BlockSoundGroup.BIG_DRIPLEAF);
            vanillaSoundGroups.put(new Identifier("small_dripleaf"), BlockSoundGroup.SMALL_DRIPLEAF);
            vanillaSoundGroups.put(new Identifier("rooted_dirt"), BlockSoundGroup.ROOTED_DIRT);
            vanillaSoundGroups.put(new Identifier("hanging_roots"), BlockSoundGroup.HANGING_ROOTS);
            vanillaSoundGroups.put(new Identifier("azalea_leaves"), BlockSoundGroup.AZALEA_LEAVES);
            vanillaSoundGroups.put(new Identifier("sculk_sensor"), BlockSoundGroup.SCULK_SENSOR);
            vanillaSoundGroups.put(new Identifier("sculk_catalyst"), BlockSoundGroup.SCULK_CATALYST);
            vanillaSoundGroups.put(new Identifier("sculk"), BlockSoundGroup.SCULK);
            vanillaSoundGroups.put(new Identifier("sculk_vein"), BlockSoundGroup.SCULK_VEIN);
            vanillaSoundGroups.put(new Identifier("sculk_shrieker"), BlockSoundGroup.SCULK_SHRIEKER);
            vanillaSoundGroups.put(new Identifier("glow_lichen"), BlockSoundGroup.GLOW_LICHEN);
            vanillaSoundGroups.put(new Identifier("deepslate"), BlockSoundGroup.DEEPSLATE);
            vanillaSoundGroups.put(new Identifier("deepslate_bricks"), BlockSoundGroup.DEEPSLATE_BRICKS);
            vanillaSoundGroups.put(new Identifier("deepslate_tiles"), BlockSoundGroup.DEEPSLATE_TILES);
            vanillaSoundGroups.put(new Identifier("polished_deepslate"), BlockSoundGroup.POLISHED_DEEPSLATE);
            vanillaSoundGroups.put(new Identifier("froglight"), BlockSoundGroup.FROGLIGHT);
            vanillaSoundGroups.put(new Identifier("frogspawn"), BlockSoundGroup.FROGSPAWN);
            vanillaSoundGroups.put(new Identifier("mangrove_roots"), BlockSoundGroup.MANGROVE_ROOTS);
            vanillaSoundGroups.put(new Identifier("muddy_mangrove_roots"), BlockSoundGroup.MUDDY_MANGROVE_ROOTS);
            vanillaSoundGroups.put(new Identifier("mud"), BlockSoundGroup.MUD);
            vanillaSoundGroups.put(new Identifier("mud_bricks"), BlockSoundGroup.MUD_BRICKS);
            vanillaSoundGroups.put(new Identifier("packed_mud"), BlockSoundGroup.PACKED_MUD);
            vanillaSoundGroups.put(new Identifier("stone"), BlockSoundGroup.STONE);
        }

        @SerializedName("material") public Identifier vanillaMaterial = new Identifier("air");
        public Identifier customMaterial;

        @SerializedName("sound_group")
        @com.google.gson.annotations.SerializedName("sound_group")
        public Identifier vanillaSoundGroup = new Identifier("stone");

        @SerializedName("new_sound_group")
        @com.google.gson.annotations.SerializedName("new_sound_group")
        public Identifier customSoundGroup;

        public boolean collidable = true;
        public float hardness = 3.0F;
        public float resistance = 3.0F;
        public boolean randomTicks = false;
        public boolean instant_break = false;
        public float slipperiness = 0.6F;
        public Identifier drop = new Identifier("stone");
        public float velocity_modifier = 1.0F;
        public float jump_velocity_modifier = 1.0F;
        public int luminance = 0;
        public boolean is_emissive = false;
        public boolean translucent = false;
        public boolean dynamic_boundaries = false;

        public BlockSoundGroup getBlockSoundGroup() {
            if (customSoundGroup != null) {
                CustomSoundGroup soundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.get(this.customSoundGroup);
                assert soundGroup != null;
                SoundEvent breakSound = Registry.SOUND_EVENT.get(soundGroup.break_sound);
                SoundEvent stepSound = Registry.SOUND_EVENT.get(soundGroup.step_sound);
                SoundEvent placeSound = Registry.SOUND_EVENT.get(soundGroup.place_sound);
                SoundEvent hitSound = Registry.SOUND_EVENT.get(soundGroup.hit_sound);
                SoundEvent fallSound = Registry.SOUND_EVENT.get(soundGroup.fall_sound);
                return new BlockSoundGroup(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
            } else {
                return vanillaSoundGroups.get(vanillaSoundGroup);
            }
        }

        public Material getMaterial() {
            if (customMaterial != null) {
                CustomMaterial customMaterial = ContentRegistries.BLOCK_MATERIALS.get(this.customMaterial);
                assert customMaterial != null;
                return new Material(customMaterial.getMapColor(), customMaterial.liquid, customMaterial.solid,
                        customMaterial.blocks_movement, customMaterial.blocks_light, customMaterial.burnable,
                        customMaterial.replaceable, customMaterial.getPistonBehavior());
            } else {
                return vanillaMaterials.get(vanillaMaterial);
            }
        }
    }

    public static class ItemProperties {
        @SerializedName("item_group")
        @com.google.gson.annotations.SerializedName("item_group")
        public Identifier creativeTab;

        public boolean fireproof = false;
        public boolean has_glint = false;
        public boolean is_enchantable = false;
        public int enchantability = 5;
        @SerializedName("max_stack_size") public int maxStackSize = 64;
        @SerializedName("max_durability") public int maxDurability = 0;
        public String rarity = "common";
        public String equipmentSlot = "";

        public ItemGroup getItemGroup() {
            return Registries.ITEM_GROUP_REGISTRY.get(creativeTab);
        }
    }

    public static class BoundingBox {
        public boolean advanced = false;
        public float[] full_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] north_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] south_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] east_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] west_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] up_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] down_shape = new float[] {0, 0, 0, 16, 16, 16};
    }

}