package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TorchBaseBlock extends TorchBlock {
	public TorchBaseBlock(BlockBehaviour.Properties properties) {
		super(ParticleTypes.FLAME, properties);
	}

	public TorchBaseBlock(SimpleParticleType particleType, BlockBehaviour.Properties properties) {
		super(particleType, properties);
	}
}
