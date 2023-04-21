package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;

public class CampfireBlockImpl extends CampfireBlock {
    public CampfireBlockImpl(Block.CampfireProperties campfireProperties) {
        super(campfireProperties.emits_particles, campfireProperties.fire_damage, Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F).sounds(SoundType.WOOD)
                .luminance((value) -> campfireProperties.luminance).ticksRandomly());
    }
}
