package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class FurnitureItemImpl extends BlockItem {
	private final NexoItem item;

	public FurnitureItemImpl(Block block,
							 NexoItem item,
							 Item.Properties props) {
		super(block, props);
		this.item = item;
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext ctx) {
		// which face did they click?
		Direction face = ctx.getClickedFace();
		var lp = item.mechanics.furniture.limited_placing;

		// if the user set any limits, enforce them
		if (lp != null) {
			boolean ok =
					(face == Direction.UP    && lp.floor) ||
							(face == Direction.DOWN  && lp.roof ) ||
							(face.getAxis().isHorizontal() && lp.wall);

			if (!ok) return InteractionResult.FAIL;
		}

		// fallback to vanilla placement logic
		return super.useOn(ctx);
	}
}
