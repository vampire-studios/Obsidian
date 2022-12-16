package io.github.vampirestudios.obsidian.api.obsidian.villager;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PointOfInterest {

    public Identifier id;
    public int ticket_count;
    public int search_distance;
    public List<Identifier> blocks;

    public Set<BlockState> getBlocks() {
        List<BlockState> blocks2 = new ArrayList<>();
        blocks.forEach(identifier -> blocks2.add(Registries.BLOCK.get(identifier).getDefaultState()));
        return Set.copyOf(blocks2);
    }

}
