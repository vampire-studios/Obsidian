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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Block {

    /*public static final MapCodec<Block> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Description.CODEC.fieldOf("description").forGetter(block -> block.description),
            Codec.STRING.fieldOf("block_type").forGetter(block -> block.block_type),

    ).apply(instance, Block::new));*/

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
    public boolean is_multi_block = false;
    public MultiBlockInformation multi_block_information;
    public ResourceLocation placable_feature;

    public TooltipInformation[] lore = new TooltipInformation[0];

    public Block(Description description, String block_type, BlockInformation information, DisplayInformation rendering, DropInformation dropInformation, AdditionalBlockInformation additional_information, Functions functions, OreInformation ore_information, FoodInformation food_information, CampfireProperties campfire_properties, List<ResourceLocation> can_plant_on, ResourceLocation particle_type, Growable growable, OxidizableProperties oxidizable_properties, boolean is_multi_block, MultiBlockInformation multi_block_information, ResourceLocation placable_feature, TooltipInformation[] lore) {
        this.description = description;
        this.block_type = block_type;
        this.information = information;
        this.rendering = rendering;
        this.dropInformation = dropInformation;
        this.additional_information = additional_information;
        this.functions = functions;
        this.ore_information = ore_information;
        this.food_information = food_information;
        this.campfire_properties = campfire_properties;
        this.can_plant_on = can_plant_on;
        this.particle_type = particle_type;
        this.growable = growable;
        this.oxidizable_properties = oxidizable_properties;
        this.is_multi_block = is_multi_block;
        this.multi_block_information = multi_block_information;
        this.placable_feature = placable_feature;
        this.lore = lore;
    }

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
        FENCE_GATE,
        CAKE,
        BED,
        TRAPDOOR,
        DOOR,
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
        BUTTON,
        PRESSURE_PLATE,
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