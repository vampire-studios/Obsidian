package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.material.Material;

public class TorchBaseBlock extends TorchBlock {
    public TorchBaseBlock() {
        super(Properties.of(Material.DEPRECATED), ParticleTypes.FLAME);
    }

    public TorchBaseBlock(SimpleParticleType particleType) {
        super(Properties.of(Material.DEPRECATED), particleType);
    }
}
