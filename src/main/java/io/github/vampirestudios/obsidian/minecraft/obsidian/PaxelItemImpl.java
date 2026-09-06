package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.state.BlockState;

/** Pickaxe + Axe + Shovel combo — effective on all three block-tag sets. */
public class PaxelItemImpl extends ItemImpl {

	public PaxelItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
		super(item, settings.pickaxe(material, 1f, -3.0f));
	}

	public ToolItem tool() {
		return (ToolItem) item;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)
				|| state.is(BlockTags.MINEABLE_WITH_AXE)
				|| state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
			ToolMaterial mat = tool().getToolMaterial();
			if (mat != null) return mat.speed();
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return super.isCorrectToolForDrops(stack, state)
				|| state.is(BlockTags.MINEABLE_WITH_AXE)
				|| state.is(BlockTags.MINEABLE_WITH_SHOVEL);
	}
}
