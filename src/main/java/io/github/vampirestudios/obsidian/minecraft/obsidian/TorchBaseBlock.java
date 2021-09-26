package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.Material;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;

public class TorchBaseBlock extends TorchBlock {
    public TorchBaseBlock() {
        super(Settings.of(Material.WOOD), ParticleTypes.FLAME);
    }

    public TorchBaseBlock(DefaultParticleType particleType) {
        super(Settings.of(Material.WOOD), particleType);
    }
}
