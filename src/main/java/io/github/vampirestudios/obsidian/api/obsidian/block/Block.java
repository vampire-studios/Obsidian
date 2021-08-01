package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.api.obsidian.BlockProperty;
import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Block {
    public BlockType block_type = BlockType.BLOCK;
    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;
    public CampfireProperties campfire_properties;
    public List<Identifier> can_plant_on;
    public Identifier particle_type;
    public Growable growable;
    public OxidizableProperties oxidizable_properties;
    public Map<String, BlockProperty> events;

    public List<net.minecraft.block.Block> getSupportableBlocks() {
        List<net.minecraft.block.Block> blocks2 = new ArrayList<>();
        can_plant_on.forEach(identifier -> blocks2.add(Registry.BLOCK.get(identifier)));
        return blocks2;
    }

    public enum BlockType {
        BLOCK,
        HORIZONTAL_FACING_BLOCK,
        ROTATABLE_BLOCK,
        CAMPFIRE,
        STAIRS,
        SLAB,
        WALL,
        FENCE,
        FENCE_GATE,
        CAKE,
        TRAPDOOR,
        DOOR,
        LOG,
        WOOD,
        OXIDIZING_BLOCK,
        PLANT,
        PILLAR,
        HORIZONTAL_FACING_PLANT,
        SAPLING,
        TORCH,
        BEEHIVE,
        LEAVES,
        LADDER,
        PATH,
        BUTTON,
        DOUBLE_PLANT,
        HORIZONTAL_FACING_DOUBLE_PLANT,
        HANGING_DOUBLE_LEAVES,
        EIGHT_DIRECTIONAL_BLOCK,
        LANTERN,
        CHAIN,
        PANE,
        DYEABLE
    }

    public static class OxidizableProperties {
        public List<OxidationStage> stages;
        public List<String> cycle;

        public static class OxidationStage {
            public boolean can_be_waxed;
            public List<VariantBlock> blocks;
            public boolean stairs;
            public boolean slab;

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

}