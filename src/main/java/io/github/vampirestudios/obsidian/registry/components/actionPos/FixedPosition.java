package io.github.vampirestudios.obsidian.registry.components.actionPos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;

public record FixedPosition(BlockPos pos) implements ActionPosition {
	public static final Codec<FixedPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(FixedPosition::pos)
	).apply(instance, FixedPosition::new));

	@Override
	public BlockPos getPosition(UseOnContext context) {
		return pos;
	}
}
