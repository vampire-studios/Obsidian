package io.github.vampirestudios.obsidian.api.obsidian.villager;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class PointOfInterest {

    public Identifier id;
    public int ticket_count;
    public int search_distance;
    public List<Identifier> blocks;

    public List<Block> getBlocks() {
        List<Block> blocks2 = new ArrayList<>();
        blocks.forEach(identifier -> blocks2.add(Registry.BLOCK.get(identifier)));
        return blocks2;
    }

}
