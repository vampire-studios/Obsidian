package io.github.vampirestudios.obsidian.registry.components.actionPos;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.UseOnContext;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.Function;

public enum ContextedPosition implements ActionPosition, StringRepresentable {
	USER((ctx) -> ctx.getPlayer().blockPosition()),
	TARGET(UseOnContext::getClickedPos);

	public static final Codec<ContextedPosition> CODEC = StringRepresentable.fromEnum(ContextedPosition::values);

	private final Function<UseOnContext, BlockPos> posGetter;
	private final String name = name().toLowerCase(Locale.ROOT);

	ContextedPosition(Function<UseOnContext, BlockPos> posGetter) {
		this.posGetter = posGetter;
	}

	@Override
	public BlockPos getPosition(UseOnContext context) {
		return posGetter.apply(context);
	}

	@Override
	public @NonNull String getSerializedName() {
		return name;
	}
}
