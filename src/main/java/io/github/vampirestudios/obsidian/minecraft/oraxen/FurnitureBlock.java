package io.github.vampirestudios.obsidian.minecraft.oraxen;

import com.mojang.serialization.MapCodec;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// 3) The Block itself: handles rotation, hitbox, seats, storage & lights
public class FurnitureBlock extends Block {
	public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	public final NexoItem.Mechanics.Furniture mech;

	public FurnitureBlock(NexoItem.Mechanics.Furniture mech, BlockBehaviour.Properties settings) {
		super(settings);
		this.mech = mech;
		BlockState state = defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(OCCUPIED, false);
		this.registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, OCCUPIED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		float yaw = ctx.getRotation();
		float realYaw = 0;
		if (mech != null) {
			realYaw = mech.rotatable ? yaw : 0;
			if (mech.restricted_rotation == NexoItem.Mechanics.Furniture.RestrictedRotation.VERY_STRICT) {
				realYaw = Math.round(realYaw / 90f) * 90f;
			} else if (mech.restricted_rotation == NexoItem.Mechanics.Furniture.RestrictedRotation.LOOSE) {
				realYaw = Math.round(realYaw / 45f) * 45f;
			}
		}
		Direction snapped = Direction.fromYRot(realYaw);
		return this.defaultBlockState().setValue(FACING, snapped);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (mech == null || mech.hitbox == null || mech.hitbox.barrierOffsets.isEmpty()) {
			return Shapes.block();
		}
		VoxelShape s = Shapes.empty();
		for (Vec3 off : mech.hitbox.barrierOffsets) {
			s = Shapes.or(s, Shapes.box(off.x, off.y, off.z, off.x + 1, off.y + 1, off.z + 1));
		}
		return s;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getCollisionShape(state, level, pos, context);
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		// 1) start with collision shape
		VoxelShape s = getCollisionShape(state, level, pos, CollisionContext.empty());

		// 2) add each interactionBox as a non-collision but clickable region
		if (mech != null && mech.hitbox != null && mech.hitbox.interactionBoxes != null) {
			for (var ib : mech.hitbox.interactionBoxes) {
				Vec3 o = ib.offset();
				double half = ib.width() * 0.5;
				VoxelShape zone = Shapes.box(o.x - half,     // center x minus half width
						o.y,            // bottom y
						o.z - half,     // center z minus half width
						o.x + half,     // center x plus half width
						o.y + ib.height(), o.z + half);
				s = Shapes.or(s, zone);
			}
		}

		return s;
	}

	private void updateLights(Level level, BlockPos pos, boolean on) {
		var lights = mech.lights.lights;
		for (var light : lights) {
			BlockPos lp = pos.offset((int) light.pos.x, (int) light.pos.y, (int) light.pos.z);
			if (on) {
				// place vanilla light block
				level.setBlockAndUpdate(lp, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, light.lightLevel));
			} else {
				// remove it if it’s ours
				if (level.getBlockState(lp).is(Blocks.LIGHT)) {
					level.setBlockAndUpdate(lp, Blocks.AIR.defaultBlockState());
				}
			}
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);

		if (!level.isClientSide()) {
//			// 2) spawn static seats from mech.seats offsets
//			if (mech.seats != null) {
//				for (String s : mech.seats) {
//					String[] parts = s.replace(" ", "").split(",");
//					int dx = Integer.parseInt(parts[0]);
//					int dy = Integer.parseInt(parts[1]);
//					int dz = Integer.parseInt(parts[2]);
//					SeatLogic.spawnSeat(level, pos.offset(dx, dy, dz), state.getValue(FACING));
//				}
//			}

			if (mech != null && mech.lights != null && !mech.lights.toggleable) {
				// mark block state lit=true
				level.setBlockAndUpdate(pos, state.setValue(LIT, true));
				updateLights(level, pos, true);
			}
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (mech != null && mech.seats != null) {
			if (!level.isClientSide()) {
				Entity entity = null;
				List<SeatEntity> entities = level.getEntities(Obsidian.SEAT, new AABB(pos), chair -> true);
				if(entities.isEmpty()) {
					entity = Obsidian.SEAT.spawn((ServerLevel) level, pos, EntitySpawnReason.TRIGGERED);
				} else {
					entity = entities.getFirst();
				}

				player.startRiding(entity);
			}
		}
		if (mech != null && mech.lights != null && mech.lights.toggleable) {
			boolean nowOn = !state.getValue(LIT);
			level.setBlockAndUpdate(pos, state.setValue(LIT, nowOn));
			updateLights(level, pos, nowOn);
			return InteractionResult.SUCCESS;
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState newState, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, newState, blockEntity, tool);
		// clear any lights when the block goes away
		if (mech != null && mech.lights != null) {
			updateLights(level, pos, false);
		}
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}
}
