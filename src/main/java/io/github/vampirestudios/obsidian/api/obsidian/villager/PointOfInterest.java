package io.github.vampirestudios.obsidian.api.obsidian.villager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

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
        blocks.forEach(identifier -> blocks2.add(BuiltInRegistries.BLOCK.getValue(identifier).defaultBlockState()));
        return Set.copyOf(blocks2);
    }

}
