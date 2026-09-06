package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 3×3 pickaxe + shovel combo — drills through both stone and dirt. */
public class DrillItemImpl extends ItemImpl {

	public DrillItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
		super(item, settings.pickaxe(material, 0f, -2.8f));
	}

	public ToolItem tool() {
		return (ToolItem) item;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
			ToolMaterial mat = tool().getToolMaterial();
			if (mat != null) return mat.speed();
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return super.isCorrectToolForDrops(stack, state) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);

		if (!level.isClientSide()) {
			for (BlockPos areaPos : HammerItemImpl.getAreaPositions(pos, player, tool().mining_radius)) {
				BlockState areaState = level.getBlockState(areaPos);
				if (!areaState.isAir()
						&& (areaState.is(BlockTags.MINEABLE_WITH_PICKAXE) || areaState.is(BlockTags.MINEABLE_WITH_SHOVEL))
						&& stack.isCorrectToolForDrops(areaState)) {
					level.destroyBlock(areaPos, !player.isCreative(), player);
					stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				}
			}
		}
		return super.mineBlock(stack, level, state, pos, miningEntity);
	}
}
