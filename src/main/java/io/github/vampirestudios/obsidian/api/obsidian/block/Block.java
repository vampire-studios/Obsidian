package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import com.electronwill.nightconfig.core.conversion.Path;
import io.github.vampirestudios.obsidian.api.bedrock.Description;
import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {

    public Description description;
    public BlockType block_type = BlockType.BLOCK;
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
    public List<Identifier> can_plant_on = new ArrayList<>();
    public Identifier particle_type;
    public Growable growable;
    public OxidizableProperties oxidizable_properties;
    public Map<String, Event> events;
    public boolean is_multi_block = false;
    public MultiBlockInformation multi_block_information;
    public Identifier placable_feature;
    public Properties properties;

    public TooltipInformation[] lore = new TooltipInformation[0];


	public List<net.minecraft.block.Block> getSupportableBlocks() {
        List<net.minecraft.block.Block> blocks2 = new ArrayList<>();
        can_plant_on.forEach(identifier -> blocks2.add(Registry.BLOCK.get(identifier)));
        return blocks2;
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
        FENCE_GATE,
        CAKE,
        BED,
        TRAPDOOR,
        METAL_DOOR,
        WOODEN_DOOR,
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
        WOODEN_BUTTON,
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
        public Map<String, PropertiesInfo> properties = new HashMap<>();

        public static class PropertiesInfo {
            public String type;
            public String[] values;
            public int min = 0, max = 1;
        }
    }
}