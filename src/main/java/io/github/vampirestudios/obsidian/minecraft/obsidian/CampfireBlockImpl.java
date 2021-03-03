package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class CampfireBlockImpl extends CampfireBlock {
    public CampfireBlockImpl(Block.CampfireProperties campfireProperties) {
        super(campfireProperties.emits_particles, campfireProperties.fire_damage, Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD)
                .luminance((value) -> campfireProperties.luminance).ticksRandomly());
    }
}
