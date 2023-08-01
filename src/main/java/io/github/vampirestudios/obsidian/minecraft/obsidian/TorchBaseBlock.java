package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;

public class TorchBaseBlock extends TorchBlock {
    public TorchBaseBlock() {
        super(Properties.of(), ParticleTypes.FLAME);
    }

    public TorchBaseBlock(SimpleParticleType particleType) {
        super(Properties.of(), particleType);
    }
}
