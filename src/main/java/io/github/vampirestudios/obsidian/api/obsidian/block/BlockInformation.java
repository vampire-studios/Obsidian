package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.MapColors;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlockInformation {
    @SerializedName("parent_block") public ResourceLocation parentBlock;

    @SerializedName("bounding_box") public BoundingBox boundingBox;

    public NameInformation name;

    public int cake_slices = 1;

    public boolean has_item = true;

    public List<String> removedTooltipSections;

    public boolean wearable = false;
    public ResourceLocation wearableModel;


    @SerializedName("block_properties")
    @com.google.gson.annotations.SerializedName("block_properties") public BlockProperties blockProperties;

    @SerializedName("item_properties")
    @com.google.gson.annotations.SerializedName("item_properties") public ItemProperties itemProperties;

    public List<ItemStack.TooltipPart> getRemovedTooltipSections() {
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
        private static final Map<ResourceLocation, SoundType> vanillaSoundGroups = new HashMap<>();

        static {
            vanillaSoundGroups.put(new ResourceLocation("wood"), SoundType.WOOD);
            vanillaSoundGroups.put(new ResourceLocation("gravel"), SoundType.GRAVEL);
            vanillaSoundGroups.put(new ResourceLocation("grass"), SoundType.GRASS);
            vanillaSoundGroups.put(new ResourceLocation("lily_pad"), SoundType.LILY_PAD);
            vanillaSoundGroups.put(new ResourceLocation("stone"), SoundType.STONE);
            vanillaSoundGroups.put(new ResourceLocation("metal"), SoundType.METAL);
            vanillaSoundGroups.put(new ResourceLocation("glass"), SoundType.GLASS);
            vanillaSoundGroups.put(new ResourceLocation("wool"), SoundType.WOOL);
            vanillaSoundGroups.put(new ResourceLocation("sand"), SoundType.SAND);
            vanillaSoundGroups.put(new ResourceLocation("snow"), SoundType.SNOW);
            vanillaSoundGroups.put(new ResourceLocation("powder_snow"), SoundType.POWDER_SNOW);
            vanillaSoundGroups.put(new ResourceLocation("ladder"), SoundType.LADDER);
            vanillaSoundGroups.put(new ResourceLocation("anvil"), SoundType.ANVIL);
            vanillaSoundGroups.put(new ResourceLocation("slime"), SoundType.SLIME_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("honey"), SoundType.HONEY_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("wet_grass"), SoundType.WET_GRASS);
            vanillaSoundGroups.put(new ResourceLocation("coral"), SoundType.CORAL_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("bamboo"), SoundType.BAMBOO);
            vanillaSoundGroups.put(new ResourceLocation("bamboo_sapling"), SoundType.BAMBOO_SAPLING);
            vanillaSoundGroups.put(new ResourceLocation("scaffolding"), SoundType.SCAFFOLDING);
            vanillaSoundGroups.put(new ResourceLocation("sweet_berry_bush"), SoundType.SWEET_BERRY_BUSH);
            vanillaSoundGroups.put(new ResourceLocation("crop"), SoundType.CROP);
            vanillaSoundGroups.put(new ResourceLocation("stem"), SoundType.HARD_CROP);
            vanillaSoundGroups.put(new ResourceLocation("vine"), SoundType.VINE);
            vanillaSoundGroups.put(new ResourceLocation("nether_wart"), SoundType.NETHER_WART);
            vanillaSoundGroups.put(new ResourceLocation("lantern"), SoundType.LANTERN);
            vanillaSoundGroups.put(new ResourceLocation("nether_stem"), SoundType.STEM);
            vanillaSoundGroups.put(new ResourceLocation("nylium"), SoundType.NYLIUM);
            vanillaSoundGroups.put(new ResourceLocation("fungus"), SoundType.FUNGUS);
            vanillaSoundGroups.put(new ResourceLocation("roots"), SoundType.ROOTS);
            vanillaSoundGroups.put(new ResourceLocation("shroomlight"), SoundType.SHROOMLIGHT);
            vanillaSoundGroups.put(new ResourceLocation("weeping_vines"), SoundType.WEEPING_VINES);
            vanillaSoundGroups.put(new ResourceLocation("weeping_vines_low_pitch"), SoundType.TWISTING_VINES);
            vanillaSoundGroups.put(new ResourceLocation("soul_sand"), SoundType.SOUL_SAND);
            vanillaSoundGroups.put(new ResourceLocation("soul_soil"), SoundType.SOUL_SOIL);
            vanillaSoundGroups.put(new ResourceLocation("basalt"), SoundType.BASALT);
            vanillaSoundGroups.put(new ResourceLocation("wart_block"), SoundType.WART_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("netherrack"), SoundType.NETHERRACK);
            vanillaSoundGroups.put(new ResourceLocation("nether_bricks"), SoundType.NETHER_BRICKS);
            vanillaSoundGroups.put(new ResourceLocation("nether_sprouts"), SoundType.NETHER_SPROUTS);
            vanillaSoundGroups.put(new ResourceLocation("nether_ore"), SoundType.NETHER_ORE);
            vanillaSoundGroups.put(new ResourceLocation("bone"), SoundType.BONE_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("netherite"), SoundType.NETHERITE_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("ancient_debris"), SoundType.ANCIENT_DEBRIS);
            vanillaSoundGroups.put(new ResourceLocation("lodestone"), SoundType.LODESTONE);
            vanillaSoundGroups.put(new ResourceLocation("chain"), SoundType.CHAIN);
            vanillaSoundGroups.put(new ResourceLocation("nether_gold_ore"), SoundType.NETHER_GOLD_ORE);
            vanillaSoundGroups.put(new ResourceLocation("gilded_blackstone"), SoundType.GILDED_BLACKSTONE);
            vanillaSoundGroups.put(new ResourceLocation("candle"), SoundType.CANDLE);
            vanillaSoundGroups.put(new ResourceLocation("amethyst_block"), SoundType.AMETHYST);
            vanillaSoundGroups.put(new ResourceLocation("amethyst_cluster"), SoundType.AMETHYST_CLUSTER);
            vanillaSoundGroups.put(new ResourceLocation("small_amethyst_bud"), SoundType.SMALL_AMETHYST_BUD);
            vanillaSoundGroups.put(new ResourceLocation("medium_amethyst_bud"), SoundType.MEDIUM_AMETHYST_BUD);
            vanillaSoundGroups.put(new ResourceLocation("large_amethyst_bud"), SoundType.LARGE_AMETHYST_BUD);
            vanillaSoundGroups.put(new ResourceLocation("tuff"), SoundType.TUFF);
            vanillaSoundGroups.put(new ResourceLocation("calcite"), SoundType.CALCITE);
            vanillaSoundGroups.put(new ResourceLocation("dripstone_block"), SoundType.DRIPSTONE_BLOCK);
            vanillaSoundGroups.put(new ResourceLocation("pointed_dripstone"), SoundType.POINTED_DRIPSTONE);
            vanillaSoundGroups.put(new ResourceLocation("copper"), SoundType.COPPER);
            vanillaSoundGroups.put(new ResourceLocation("cave_vines"), SoundType.CAVE_VINES);
            vanillaSoundGroups.put(new ResourceLocation("spore_blossom"), SoundType.SPORE_BLOSSOM);
            vanillaSoundGroups.put(new ResourceLocation("azalea"), SoundType.AZALEA);
            vanillaSoundGroups.put(new ResourceLocation("flowering_azalea"), SoundType.FLOWERING_AZALEA);
            vanillaSoundGroups.put(new ResourceLocation("moss_carpet"), SoundType.MOSS_CARPET);
            vanillaSoundGroups.put(new ResourceLocation("pink_petals"), SoundType.PINK_PETALS);
            vanillaSoundGroups.put(new ResourceLocation("moss_block"), SoundType.MOSS);
            vanillaSoundGroups.put(new ResourceLocation("big_dripleaf"), SoundType.BIG_DRIPLEAF);
            vanillaSoundGroups.put(new ResourceLocation("small_dripleaf"), SoundType.SMALL_DRIPLEAF);
            vanillaSoundGroups.put(new ResourceLocation("rooted_dirt"), SoundType.ROOTED_DIRT);
            vanillaSoundGroups.put(new ResourceLocation("hanging_roots"), SoundType.HANGING_ROOTS);
            vanillaSoundGroups.put(new ResourceLocation("azalea_leaves"), SoundType.AZALEA_LEAVES);
            vanillaSoundGroups.put(new ResourceLocation("sculk_sensor"), SoundType.SCULK_SENSOR);
            vanillaSoundGroups.put(new ResourceLocation("sculk_catalyst"), SoundType.SCULK_CATALYST);
            vanillaSoundGroups.put(new ResourceLocation("sculk"), SoundType.SCULK);
            vanillaSoundGroups.put(new ResourceLocation("sculk_vein"), SoundType.SCULK_VEIN);
            vanillaSoundGroups.put(new ResourceLocation("sculk_shrieker"), SoundType.SCULK_SHRIEKER);
            vanillaSoundGroups.put(new ResourceLocation("glow_lichen"), SoundType.GLOW_LICHEN);
            vanillaSoundGroups.put(new ResourceLocation("deepslate"), SoundType.DEEPSLATE);
            vanillaSoundGroups.put(new ResourceLocation("deepslate_bricks"), SoundType.DEEPSLATE_BRICKS);
            vanillaSoundGroups.put(new ResourceLocation("deepslate_tiles"), SoundType.DEEPSLATE_TILES);
            vanillaSoundGroups.put(new ResourceLocation("polished_deepslate"), SoundType.POLISHED_DEEPSLATE);
            vanillaSoundGroups.put(new ResourceLocation("froglight"), SoundType.FROGLIGHT);
            vanillaSoundGroups.put(new ResourceLocation("frogspawn"), SoundType.FROGSPAWN);
            vanillaSoundGroups.put(new ResourceLocation("mangrove_roots"), SoundType.MANGROVE_ROOTS);
            vanillaSoundGroups.put(new ResourceLocation("muddy_mangrove_roots"), SoundType.MUDDY_MANGROVE_ROOTS);
            vanillaSoundGroups.put(new ResourceLocation("mud"), SoundType.MUD);
            vanillaSoundGroups.put(new ResourceLocation("mud_bricks"), SoundType.MUD_BRICKS);
            vanillaSoundGroups.put(new ResourceLocation("packed_mud"), SoundType.PACKED_MUD);
            vanillaSoundGroups.put(new ResourceLocation("hanging_sign"), SoundType.HANGING_SIGN);
            vanillaSoundGroups.put(new ResourceLocation("nether_wood_hanging_sign"), SoundType.NETHER_WOOD_HANGING_SIGN);
            vanillaSoundGroups.put(new ResourceLocation("bamboo_wood_hanging_sign"), SoundType.BAMBOO_WOOD_HANGING_SIGN);
            vanillaSoundGroups.put(new ResourceLocation("bamboo_wood"), SoundType.BAMBOO_WOOD);
            vanillaSoundGroups.put(new ResourceLocation("nether_wood"), SoundType.NETHER_WOOD);
            vanillaSoundGroups.put(new ResourceLocation("cherry_wood"), SoundType.CHERRY_WOOD);
            vanillaSoundGroups.put(new ResourceLocation("cherry_sapling"), SoundType.CHERRY_SAPLING);
            vanillaSoundGroups.put(new ResourceLocation("cherry_leaves"), SoundType.CHERRY_LEAVES);
            vanillaSoundGroups.put(new ResourceLocation("cherry_wood_hanging_sign"), SoundType.CHERRY_WOOD_HANGING_SIGN);
            vanillaSoundGroups.put(new ResourceLocation("chiseled_bookshelf"), SoundType.CHISELED_BOOKSHELF);
            vanillaSoundGroups.put(new ResourceLocation("suspicious_sand"), SoundType.SUSPICIOUS_SAND);
            vanillaSoundGroups.put(new ResourceLocation("suspicious_gravel"), SoundType.SUSPICIOUS_GRAVEL);
            vanillaSoundGroups.put(new ResourceLocation("decorated_pot"), SoundType.DECORATED_POT);
            vanillaSoundGroups.put(new ResourceLocation("decorated_pot_shatter"), SoundType.DECORATED_POT_CRACKED);
        }

        @SerializedName("sound_group")
        @com.google.gson.annotations.SerializedName("sound_group")
        public Object soundGroup = new ResourceLocation("stone");

        public boolean collidable = true;
        public float hardness = 3.0F;
        public float resistance = 3.0F;
        public boolean randomTicks = false;
        public boolean instant_break = false;
        public float slipperiness = 0.6F;
        public ResourceLocation drop = new ResourceLocation("stone");
        public float velocity_modifier = 1.0F;
        public float jump_velocity_modifier = 1.0F;
        public int luminance = 0;
        public boolean is_emissive = false;
        public boolean translucent = false;
        public boolean dynamic_boundaries = false;
        public String push_reaction = "NORMAL";
        public String map_color = "STONE";

        public MapColor getMapColor() {
            return MapColors.get(map_color);
        }

        public PushReaction getPushReaction() {
            return switch(push_reaction.toUpperCase(Locale.ROOT)) {
                case "NORMAL" -> PushReaction.NORMAL;
                case "DESTROY" -> PushReaction.DESTROY;
                case "BLOCK" -> PushReaction.BLOCK;
                case "IGNORE" -> PushReaction.IGNORE;
                case "PUSH_ONLY" -> PushReaction.PUSH_ONLY;
                default -> throw new IllegalStateException("Unexpected value: " + push_reaction);
            };
        }

        public SoundType getBlockSoundGroup() {
            if (soundGroup instanceof ResourceLocation resourceLocation) {
                if (!resourceLocation.getNamespace().equals("minecraft")) {
                    CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.get(resourceLocation);
                    assert customSoundGroup != null;
                    return createSoundType(customSoundGroup);
                } else {
                    return vanillaSoundGroups.get(resourceLocation);
                }
            } else if (soundGroup instanceof CustomSoundGroup customSoundGroup) {
                return createSoundType(customSoundGroup);
            } else {
                return SoundType.STONE;
            }
        }

        private SoundType createSoundType(CustomSoundGroup customSoundGroup) {
            SoundEvent breakSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.break_sound);
            SoundEvent stepSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.step_sound);
            SoundEvent placeSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.place_sound);
            SoundEvent hitSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.hit_sound);
            SoundEvent fallSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.fall_sound);
            return new SoundType(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
        }
    }

    public static class ItemProperties {
        @SerializedName("item_group")
        @com.google.gson.annotations.SerializedName("item_group")
        public ResourceLocation creativeTab;

        public boolean fireproof = false;
        public boolean has_glint = false;
        public boolean is_enchantable = false;
        public int enchantability = 5;
        @SerializedName("max_stack_size") public int maxStackSize = 64;
        @SerializedName("max_durability") public int maxDurability = 0;
        public String rarity = "common";
        public String equipmentSlot = "";

        public ResourceKey<CreativeModeTab> getItemGroup() {
            return ResourceKey.create(Registries.CREATIVE_MODE_TAB, creativeTab);
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
//        private DynamicShape generalShape;
//        private DynamicShape collisionShape;
//        private DynamicShape raytraceShape;
//        private DynamicShape renderShape;
//
//        public static class Shape {
//
//        }
    }

}