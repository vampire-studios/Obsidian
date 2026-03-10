package io.github.vampirestudios.obsidian.registry.components.actionPos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.UseOnContext;

public record RelativePosition(ContextedPosition origin, Vec3i offset) implements ActionPosition {
	public static final Codec<RelativePosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ContextedPosition.CODEC.fieldOf("origin").forGetter(RelativePosition::origin),
			Vec3i.CODEC.fieldOf("offset").forGetter(RelativePosition::offset)
	).apply(instance, RelativePosition::new));

	@Override
	public BlockPos getPosition(UseOnContext context) {
		return origin.getPosition(context).offset(offset);
	}
}
