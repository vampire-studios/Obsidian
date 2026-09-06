package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** 3×3 pickaxe — heavy damage, slow swing. */
public class HammerItemImpl extends ItemImpl {

	public HammerItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
		super(item, settings.pickaxe(material, 5f, -3.6f));
	}

	public ToolItem tool() {
		return (ToolItem) item;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);

		if (!level.isClientSide()) {
			for (BlockPos areaPos : getAreaPositions(pos, player, tool().mining_radius)) {
				BlockState areaState = level.getBlockState(areaPos);
				if (!areaState.isAir() && areaState.is(BlockTags.MINEABLE_WITH_PICKAXE)
						&& stack.isCorrectToolForDrops(areaState)) {
					level.destroyBlock(areaPos, !player.isCreative(), player);
					stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				}
			}
		}
		return super.mineBlock(stack, level, state, pos, miningEntity);
	}

	static List<BlockPos> getAreaPositions(BlockPos center, LivingEntity miner, int radius) {
		Vec3 look = miner.getLookAngle();
		Direction facing = Direction.getApproximateNearest(look.x, look.y, look.z);
		Direction.Axis axis = facing.getAxis();
		List<BlockPos> positions = new ArrayList<>();
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i == 0 && j == 0) continue;
				positions.add(switch (axis) {
					case Y -> center.offset(i, 0, j);
					case X -> center.offset(0, i, j);
					case Z -> center.offset(i, j, 0);
				});
			}
		}
		return positions;
	}
}
