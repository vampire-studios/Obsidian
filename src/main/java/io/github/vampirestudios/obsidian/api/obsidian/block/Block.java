package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import com.electronwill.nightconfig.core.conversion.Path;
import io.github.vampirestudios.obsidian.api.bedrock.Description;
import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class Block {

    public Description description;
    public String block_type = "block";
    public BlockInformation information;
    public DisplayInformation rendering;
    @SerializedName("drop_information")
    @com.google.gson.annotations.SerializedName("drop_information")
    @Path("drop_information")
    public DropInformation dropInformation;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;
    public CampfireProperties campfire_properties;
    public List<ResourceLocation> can_plant_on = new ArrayList<>();
    public ResourceLocation particle_type;
    public Growable growable;
    public OxidizableProperties oxidizable_properties;
//    public Map<String, Event> events;
    public boolean is_multi_block = false;
    public MultiBlockInformation multi_block_information;
    public ResourceLocation placable_feature;
//    public Properties properties;

    public TooltipInformation[] lore = new TooltipInformation[0];

    public List<net.minecraft.world.level.block.Block> getSupportableBlocks() {
        List<net.minecraft.world.level.block.Block> blocks2 = new ArrayList<>();
        can_plant_on.forEach(identifier -> blocks2.add(BuiltInRegistries.BLOCK.get(identifier)));
        return blocks2;
    }

    public BlockType getBlockType() {
        return BlockType.valueOf(block_type.toUpperCase(Locale.ROOT));
    }

    public enum BlockType {
        BLOCK,
        HORIZONTAL_DIRECTIONAL,
        DIRECTIONAL,
        CAMPFIRE,
        STAIRS,
        SLAB,
        WALL,
        FENCE,
        OVERWORLD_FENCE_GATE,
        NETHER_FENCE_GATE,
        BAMBOO_FENCE_GATE,
        CAKE,
        BED,
        OVERWORLD_TRAPDOOR,
        NETHER_TRAPDOOR,
        BAMBOO_TRAPDOOR,
        METAL_DOOR,
        OVERWORLD_DOOR,
        NETHER_DOOR,
        BAMBOO_DOOR,
        LOG,
        STEM,
        WOOD,
        OXIDIZING_BLOCK,
        PLANT,
        ROTATED_PILLAR,
        HORIZONTAL_FACING_PLANT,
        SAPLING,
        TORCH,
        BEEHIVE,
        LEAVES,
        LADDER,
        PATH,
        OVERWORLD_WOOD_BUTTON,
        NETHER_WOOD_BUTTON,
        BAMBOO_BUTTON,
        STONE_BUTTON,
        DOUBLE_PLANT,
        HORIZONTAL_FACING_DOUBLE_PLANT,
        HANGING_DOUBLE_LEAVES,
        EIGHT_DIRECTIONAL_BLOCK,
        LANTERN,
        CHAIN,
        PANE,
        DYEABLE,
        LOOM,
        GRINDSTONE,
        CRAFTING_TABLE,
        PISTON,
        NOTEBLOCK,
        JUKEBOX,
        SMOKER,
        FURNACE,
        BLAST_FURNACE,
        LECTERN,
        FLETCHING_TABLE,
        BARREL,
        COMPOSTER,
        RAILS,
        CARTOGRAPHY_TABLE,
        CARPET
    }

    public static class OxidizableProperties {
        public List<OxidationStage> stages;
        public List<String> cycle;

        public static class OxidationStage {
            public boolean can_be_waxed = true;
            public List<VariantBlock> blocks;
            public boolean stairs = true;
            public boolean slab = true;

            public static class VariantBlock {
                public NameInformation name;
                public DisplayInformation display;
            }
        }
    }

    public static class CampfireProperties {
        public boolean emits_particles;
        public int fire_damage;
        public int luminance;
    }

    public static class MultiBlockInformation {
        public int width;
        public int height;
    }

    public static class Properties {
        public String facing;
//        public Map<String, PropertiesInfo> properties = new HashMap<>();

        public static class PropertiesInfo {
            public String type;
            public String[] values;
            public int min = 0, max = 1;
        }
    }
}