package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;

public class TorchBaseBlock extends TorchBlock {
    public TorchBaseBlock() {
        super(ParticleTypes.FLAME, Properties.of());
    }

    public TorchBaseBlock(SimpleParticleType particleType) {
        super(particleType, Properties.of());
    }
}
