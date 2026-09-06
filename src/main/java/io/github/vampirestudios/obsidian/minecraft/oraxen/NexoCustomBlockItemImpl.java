package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/** Block item for Nexo custom blocks, including their shared limited-placing rules. */
public class NexoCustomBlockItemImpl extends BlockItem {
	private final NexoItem.Mechanics.CustomBlock mechanic;

	public NexoCustomBlockItemImpl(Block block, NexoItem.Mechanics.CustomBlock mechanic, Properties properties) {
		super(block, properties);
		this.mechanic = mechanic;
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		if (mechanic != null && mechanic.limited_placing != null
				&& !FurnitureItemImpl.canPlace(context, getBlock(), mechanic.limited_placing)) {
			return InteractionResult.FAIL;
		}
		return super.useOn(context);
	}
}
