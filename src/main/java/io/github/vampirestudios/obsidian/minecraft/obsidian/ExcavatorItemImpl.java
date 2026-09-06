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

/** 3×3 shovel — clears large dirt/gravel/sand areas quickly. */
public class ExcavatorItemImpl extends ItemImpl {

	public ExcavatorItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
		super(item, settings.shovel(material, 2f, -3.0f));
	}

	public ToolItem tool() {
		return (ToolItem) item;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);

		if (!level.isClientSide()) {
			for (BlockPos areaPos : HammerItemImpl.getAreaPositions(pos, player, tool().mining_radius)) {
				BlockState areaState = level.getBlockState(areaPos);
				if (!areaState.isAir() && areaState.is(BlockTags.MINEABLE_WITH_SHOVEL)
						&& stack.isCorrectToolForDrops(areaState)) {
					level.destroyBlock(areaPos, !player.isCreative(), player);
					stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				}
			}
		}
		return super.mineBlock(stack, level, state, pos, miningEntity);
	}
}
