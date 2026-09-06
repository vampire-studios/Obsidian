package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
		var lp = item.mechanics.furniture.limited_placing;
		if (lp != null && !canPlace(ctx, getBlock(), lp)) return InteractionResult.FAIL;
		return super.useOn(ctx);
	}

	static boolean canPlace(UseOnContext ctx, Block placedBlock, NexoItem.Mechanics.LimitedPlacing lp) {
		Direction face = ctx.getClickedFace();
		boolean ok = (face == Direction.UP && lp.floor) || (face == Direction.DOWN && lp.roof)
				|| (face.getAxis().isHorizontal() && lp.wall);
		if (!ok) return false;

		BlockState support = ctx.getLevel().getBlockState(ctx.getClickedPos());
		boolean hasSelectors = hasEntries(lp.blockTypes) || hasEntries(lp.blockTags) || hasEntries(lp.nexoBlocks);
		boolean matches = matchesBlock(support, lp.blockTypes) || matchesTag(support, lp.blockTags)
				|| matchesBlock(support, lp.nexoBlocks);
		if (hasSelectors && ((lp.type == NexoItem.Mechanics.LimitedPlacing.LimitedPlacingType.ALLOW && !matches)
				|| (lp.type == NexoItem.Mechanics.LimitedPlacing.LimitedPlacingType.DENY && matches))) {
			return false;
		}

		if (lp.radius_limitation != null && lp.radius_limitation.radius > 0
				&& lp.radius_limitation.amount >= 0 && exceedsRadiusLimit(ctx, placedBlock, lp.radius_limitation)) {
			return false;
		}
		return true;
	}

	private static boolean exceedsRadiusLimit(UseOnContext context, Block placedBlock,
	                                          NexoItem.Mechanics.LimitedPlacing.RadiusLimitation limit) {
		if (limit.amount == 0) return true;
		BlockPos center = context.getClickedPos().relative(context.getClickedFace());
		int radius = Math.min(limit.radius, 64);
		int found = 0;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius),
				center.offset(radius, radius, radius))) {
			if (pos.distSqr(center) > (double) radius * radius) continue;
			if (context.getLevel().getBlockState(pos).is(placedBlock) && ++found >= limit.amount) return true;
		}
		return false;
	}

	private static boolean matchesBlock(BlockState state, java.util.List<String> configured) {
		if (!hasEntries(configured)) return false;
		Identifier actual = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		for (String raw : configured) {
			if (raw == null || raw.isBlank()) continue;
			String normalized = raw.toLowerCase(java.util.Locale.ROOT);
			Identifier wanted = Identifier.tryParse(normalized);
			if (wanted != null && (actual.equals(wanted) || (!raw.contains(":") && actual.getPath().equals(normalized)))) {
				return true;
			}
		}
		return false;
	}

	private static boolean matchesTag(BlockState state, java.util.List<String> configured) {
		if (!hasEntries(configured)) return false;
		for (String raw : configured) {
			if (raw == null || raw.isBlank()) continue;
			String normalized = (raw.startsWith("#") ? raw.substring(1) : raw).toLowerCase(java.util.Locale.ROOT);
			Identifier id = Identifier.tryParse(normalized);
			if (id != null && state.is(TagKey.create(Registries.BLOCK, id))) return true;
		}
		return false;
	}

	private static boolean hasEntries(java.util.List<String> values) {
		return values != null && !values.isEmpty();
	}
}
