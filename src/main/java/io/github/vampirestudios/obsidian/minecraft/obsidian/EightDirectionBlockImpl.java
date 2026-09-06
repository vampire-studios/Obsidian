package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class EightDirectionBlockImpl extends RotationBlockImpl {

	public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);

	public EightDirectionBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public IntegerProperty rotationProperty() {
		return ROTATION;
	}

	@Override
	public int segments() {
		return 8;
	}
}
