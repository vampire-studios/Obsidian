package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class Block {

    public BlockType block_type;
    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;
    public CampfireProperties campfire_properties;
    public List<Identifier> can_plant_on;
    public Identifier particle_type;

    public List<net.minecraft.block.Block> getSupportableBlocks() {
        List<net.minecraft.block.Block> blocks2 = new ArrayList<>();
        can_plant_on.forEach(identifier -> blocks2.add(Registry.BLOCK.get(identifier)));
        return blocks2;
    }

    public enum BlockType {
        CAMPFIRE,
        STAIRS,
        SLAB,
        FENCE,
        FENCE_GATE,
        CAKE,
        TRAPDOOR,
        DOOR,
        LOG,
        OXIDIZING_BLOCK,
        PLANT,
        SAPLING,
        TORCH,
        BEE_HIVE,
        LEAVES,
        LADDER,
        PATH
    }

    public static class CampfireProperties {

        public boolean emits_particles;
        public int fire_damage;
        public int luminance;

    }

}