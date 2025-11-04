package io.github.vampirestudios.obsidian.block;

import com.mojang.serialization.MapCodec;
import io.github.vampirestudios.obsidian.block.entity.PaintingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PaintingTableBlock extends BaseEntityBlock {

	public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

	public PaintingTableBlock(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof PaintingTableBlockEntity be) {
			return be.onUse(block.paintingTableInformation, player);
		}

		return super.useWithoutItem(state, level, pos, player, hit);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PaintingTableBlockEntity(pos, state);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}
}
