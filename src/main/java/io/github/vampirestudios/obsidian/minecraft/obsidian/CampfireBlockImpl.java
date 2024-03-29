package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class CampfireBlockImpl extends CampfireBlock {
    public CampfireBlockImpl(Block.CampfireProperties campfireProperties) {
        super(campfireProperties.emits_particles, campfireProperties.fire_damage, Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.0F).sound(SoundType.WOOD)
                .lightLevel((value) -> campfireProperties.luminance).randomTicks());
    }
}
