package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** Axe + Hoe combo — chops axe blocks and tills dirt. */
public class MattockItemImpl extends ItemImpl {

	public MattockItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
		super(item, settings.axe(material, 2f, -2.0f));
	}

	public ToolItem tool() {
		return (ToolItem) item;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE)) {
			ToolMaterial mat = tool().getToolMaterial();
			if (mat != null) return mat.speed();
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return super.isCorrectToolForDrops(stack, state) || state.is(BlockTags.MINEABLE_WITH_HOE);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);

		if (context.getClickedFace() != Direction.DOWN) {
			BlockState tilled = getTilledStateOf(state);
			if (tilled != null) {
				Player player = context.getPlayer();
				world.playSound(player, pos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
				if (!world.isClientSide()) {
					world.setBlock(pos, tilled, 11);
					if (player != null) context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				}
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(context);
	}

	static @Nullable BlockState getTilledStateOf(BlockState state) {
		if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH))
			return Blocks.FARMLAND.defaultBlockState();
		if (state.is(Blocks.COARSE_DIRT))
			return Blocks.DIRT.defaultBlockState();
		return null;
	}
}
