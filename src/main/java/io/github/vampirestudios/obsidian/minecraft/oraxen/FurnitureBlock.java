package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.ISeatProvider;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block.Behaviour;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 3) The Block itself: handles rotation, hitbox, seats, storage & lights
public class FurnitureBlock extends Block implements ISeatProvider, SimpleWaterloggedBlock {
	public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	/** State zero is Nexo's implicit base state; configured states occupy 1 through 15. */
	public static final IntegerProperty FURNITURE_STATE = IntegerProperty.create("furniture_state", 0, 15);
	public final NexoItem.Mechanics.Furniture mech;
	private static final Map<TimerKey, Long> DOOR_TIMERS = new ConcurrentHashMap<>();
	private static final Map<TimerKey, StateTimer> STATE_TIMERS = new ConcurrentHashMap<>();

	/** Nexo's singular/plural seat and bed declarations parsed once on first use. */
	private @Nullable List<Behaviour.Seat> seats;
	private @Nullable List<LocalBox> collisionBoxes;
	private @Nullable List<LocalBox> interactionBoxes;

	public FurnitureBlock(NexoItem.Mechanics.Furniture mech, BlockBehaviour.Properties settings) {
		super(settings);
		this.mech = mech;
		BlockState state = defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false)
				.setValue(OCCUPIED, false).setValue(OPEN, false).setValue(WATERLOGGED, false);
		this.registerDefaultState(state);
	}

	@Override
	public List<Behaviour.Seat> getSeats(BlockState state) {
		return this.seats();
	}

	/** Converts Nexo's comma-separated local offsets once, keeping malformed entries isolated. */
	protected List<Behaviour.Seat> seats() {
		if (this.seats != null) return this.seats;
		if (this.mech == null) {
			return this.seats = List.of();
		}

		List<Behaviour.Seat> parsed = new ArrayList<>();
		List<String> encodedSeats = new ArrayList<>();
		if (this.mech.seat != null && !this.mech.seat.isBlank()) encodedSeats.add(this.mech.seat);
		if (this.mech.seats != null) encodedSeats.addAll(this.mech.seats);
		for (String encoded : encodedSeats) {
			try {
				Behaviour.Seat seat = new Behaviour.Seat();
				seat.offset = parseOffset(encoded);
				parsed.add(seat);
			} catch (RuntimeException exception) {
				Obsidian.LOGGER.warn("Ignoring malformed Nexo furniture seat '{}': {}", encoded, exception.getMessage());
			}
		}

		List<String> encodedBeds = new ArrayList<>();
		if (this.mech.bed != null && !this.mech.bed.isBlank()) encodedBeds.add(this.mech.bed);
		if (this.mech.beds != null) encodedBeds.addAll(this.mech.beds);
		for (String encoded : encodedBeds) {
			try {
				String[] values = encoded.trim().replaceAll("\\s*,\\s*", ",").split("\\s+");
				if (values.length != 3) {
					throw new IllegalArgumentException("expected 'x,y,z skip-night reset-phantoms'");
				}

				Behaviour.Seat bed = new Behaviour.Seat();
				bed.offset = parseOffset(values[0]);
				bed.pose = Behaviour.Seat.SeatPose.LYING;
				bed.lockRotation = true;
				bed.skipNight = parseBoolean(values[1], "skip-night");
				bed.resetPhantoms = parseBoolean(values[2], "reset-phantoms");
				parsed.add(bed);
			} catch (RuntimeException exception) {
				Obsidian.LOGGER.warn("Ignoring malformed Nexo furniture bed '{}': {}", encoded, exception.getMessage());
			}
		}
		return this.seats = List.copyOf(parsed);
	}

	protected static Vector3f parseOffset(String encoded) {
		String[] values = encoded.replaceAll("\\s", "").split(",");
		if (values.length != 3) throw new IllegalArgumentException("expected x,y,z offset");
		return new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]),
				Float.parseFloat(values[2]));
	}

	private static boolean parseBoolean(String value, String name) {
		if ("true".equalsIgnoreCase(value)) return true;
		if ("false".equalsIgnoreCase(value)) return false;
		throw new IllegalArgumentException(name + " must be true or false");
	}

	private void parseHitboxes() {
		if (collisionBoxes != null && interactionBoxes != null) return;
		List<LocalBox> collisions = new ArrayList<>();
		List<LocalBox> interactions = new ArrayList<>();
		if (mech == null || mech.hitbox == null) {
			collisionBoxes = List.of();
			interactionBoxes = List.of();
			return;
		}

		NexoItem.Mechanics.Furniture.Hitbox hitbox = mech.hitbox;
		for (String encoded : declarations(hitbox.barrier, hitbox.barriers)) {
			try {
				String[] axes = encoded.replace(" ", "").split(",");
				if (axes.length != 3) throw new IllegalArgumentException("expected x,y,z");
				for (int x : expandRange(axes[0])) for (int y : expandRange(axes[1]))
					for (int z : expandRange(axes[2])) collisions.add(new LocalBox(x, y, z, x + 1, y + 1, z + 1));
			} catch (RuntimeException exception) {
				warnHitbox("barrier", encoded, exception);
			}
		}

		for (String encoded : declarations(hitbox.interaction, hitbox.interactions)) {
			try {
				String[] values = encoded.trim().split("\\s+");
				if (values.length != 3) throw new IllegalArgumentException("expected 'x,y,z width height'");
				Vector3f offset = parseOffset(values[0]);
				double halfWidth = Double.parseDouble(values[1]) * 0.5;
				double height = Double.parseDouble(values[2]);
				interactions.add(new LocalBox(offset.x() - halfWidth, offset.y(), offset.z() - halfWidth,
						offset.x() + halfWidth, offset.y() + height, offset.z() + halfWidth));
			} catch (RuntimeException exception) {
				warnHitbox("interaction", encoded, exception);
			}
		}

		for (String encoded : declarations(hitbox.shulker, hitbox.shulkers)) {
			try {
				String[] values = encoded.trim().split("\\s+");
				if (values.length < 3) throw new IllegalArgumentException("expected 'x,y,z scale length [direction] [visible]'");
				Vector3f offset = parseOffset(values[0]);
				double scale = Double.parseDouble(values[1]);
				double length = Double.parseDouble(values[2]);
				Direction direction = values.length > 3 && !isBoolean(values[3])
						? Direction.valueOf(values[3].toUpperCase(java.util.Locale.ROOT)) : Direction.UP;
				collisions.add(orientedBox(offset, scale, length, direction));
			} catch (RuntimeException exception) {
				warnHitbox("shulker", encoded, exception);
			}
		}

		for (String encoded : declarations(hitbox.ghast, hitbox.ghasts)) {
			try {
				String[] values = encoded.trim().split("\\s+");
				if (values.length < 2) throw new IllegalArgumentException("expected 'x,y,z scale [rotation] [visible]'");
				Vector3f offset = parseOffset(values[0]);
				double size = Double.parseDouble(values[1]) * 4.0;
				double half = size * 0.5;
				collisions.add(new LocalBox(offset.x() + 0.5 - half, offset.y() + 0.5 - half,
						offset.z() + 0.5 - half, offset.x() + 0.5 + half,
						offset.y() + 0.5 + half, offset.z() + 0.5 + half));
			} catch (RuntimeException exception) {
				warnHitbox("ghast", encoded, exception);
			}
		}

		collisionBoxes = List.copyOf(collisions);
		interactionBoxes = List.copyOf(interactions);
	}

	private static List<String> declarations(@Nullable String singular, @Nullable List<String> plural) {
		List<String> values = new ArrayList<>();
		if (singular != null && !singular.isBlank()) values.add(singular);
		if (plural != null) for (String value : plural) if (value != null && !value.isBlank()) values.add(value);
		return values;
	}

	private static List<Integer> expandRange(String encoded) {
		if (!encoded.contains("..")) return List.of(Integer.parseInt(encoded));
		String[] bounds = encoded.split("\\.\\.");
		if (bounds.length != 2) throw new IllegalArgumentException("invalid range");
		int start = Integer.parseInt(bounds[0]);
		int end = Integer.parseInt(bounds[1]);
		int step = start <= end ? 1 : -1;
		List<Integer> values = new ArrayList<>();
		for (int value = start; ; value += step) {
			values.add(value);
			if (value == end) break;
		}
		return values;
	}

	private static LocalBox orientedBox(Vector3f offset, double scale, double length, Direction direction) {
		double centerX = offset.x() + 0.5;
		double centerY = offset.y() + 0.5;
		double centerZ = offset.z() + 0.5;
		double halfX = scale * 0.5;
		double halfY = scale * 0.5;
		double halfZ = scale * 0.5;
		double extension = Math.max(0.0, length - 1.0) * scale;
		return switch (direction) {
			case DOWN -> new LocalBox(centerX - halfX, centerY - halfY - extension, centerZ - halfZ,
					centerX + halfX, centerY + halfY, centerZ + halfZ);
			case NORTH -> new LocalBox(centerX - halfX, centerY - halfY, centerZ - halfZ - extension,
					centerX + halfX, centerY + halfY, centerZ + halfZ);
			case SOUTH -> new LocalBox(centerX - halfX, centerY - halfY, centerZ - halfZ,
					centerX + halfX, centerY + halfY, centerZ + halfZ + extension);
			case WEST -> new LocalBox(centerX - halfX - extension, centerY - halfY, centerZ - halfZ,
					centerX + halfX, centerY + halfY, centerZ + halfZ);
			case EAST -> new LocalBox(centerX - halfX, centerY - halfY, centerZ - halfZ,
					centerX + halfX + extension, centerY + halfY, centerZ + halfZ);
			default -> new LocalBox(centerX - halfX, centerY - halfY, centerZ - halfZ,
					centerX + halfX, centerY + halfY + extension, centerZ + halfZ);
		};
	}

	private static boolean isBoolean(String value) {
		return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
	}

	private static void warnHitbox(String type, String encoded, RuntimeException exception) {
		Obsidian.LOGGER.warn("Ignoring malformed Nexo {} hitbox '{}': {}", type, encoded, exception.getMessage());
	}

	private record LocalBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		LocalBox rotate(Direction facing) {
			if (facing == Direction.NORTH) return this;
			double radians = Math.toRadians(facing.toYRot() - Direction.NORTH.toYRot());
			double sin = Math.sin(radians);
			double cos = Math.cos(radians);
			double[] xs = {minX, minX, maxX, maxX};
			double[] zs = {minZ, maxZ, minZ, maxZ};
			double rotatedMinX = Double.POSITIVE_INFINITY;
			double rotatedMaxX = Double.NEGATIVE_INFINITY;
			double rotatedMinZ = Double.POSITIVE_INFINITY;
			double rotatedMaxZ = Double.NEGATIVE_INFINITY;
			for (int index = 0; index < xs.length; index++) {
				double x = xs[index] - 0.5;
				double z = zs[index] - 0.5;
				double rotatedX = x * cos - z * sin + 0.5;
				double rotatedZ = x * sin + z * cos + 0.5;
				rotatedMinX = Math.min(rotatedMinX, rotatedX);
				rotatedMaxX = Math.max(rotatedMaxX, rotatedX);
				rotatedMinZ = Math.min(rotatedMinZ, rotatedZ);
				rotatedMaxZ = Math.max(rotatedMaxZ, rotatedZ);
			}
			return new LocalBox(rotatedMinX, minY, rotatedMinZ, rotatedMaxX, maxY, rotatedMaxZ);
		}
	}

	private static BlockPos rotateOffset(int x, int y, int z, Direction facing) {
		return switch (facing) {
			case EAST -> new BlockPos(-z, y, x);
			case SOUTH -> new BlockPos(-x, y, -z);
			case WEST -> new BlockPos(z, y, -x);
			default -> new BlockPos(x, y, z);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, OCCUPIED, OPEN, WATERLOGGED);
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
		boolean waterlogged = mech != null && mech.waterloggable
				&& ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, snapped).setValue(WATERLOGGED, waterlogged);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (mech != null && mech.door != null && mech.door.toggle_hitbox_on_open && state.getValue(OPEN)) {
			return Shapes.empty();
		}
		parseHitboxes();
		VoxelShape s = Shapes.empty();
		for (LocalBox box : collisionBoxes) {
			LocalBox rotated = box.rotate(state.getValue(FACING));
			s = Shapes.or(s, Shapes.box(rotated.minX, rotated.minY, rotated.minZ,
					rotated.maxX, rotated.maxY, rotated.maxZ));
		}
		return s;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = getCollisionShape(state, level, pos, context);
		parseHitboxes();
		for (LocalBox box : interactionBoxes) {
			LocalBox rotated = box.rotate(state.getValue(FACING));
			shape = Shapes.or(shape, Shapes.box(rotated.minX, rotated.minY, rotated.minZ,
					rotated.maxX, rotated.maxY, rotated.maxZ));
		}
		return shape.isEmpty() ? Shapes.block() : shape;
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return getShape(state, level, pos, CollisionContext.empty());
	}

	private void updateLights(Level level, BlockPos pos, boolean on) {
		var lights = mech.lights.lights;
		Direction facing = level.getBlockState(pos).hasProperty(FACING)
				? level.getBlockState(pos).getValue(FACING) : Direction.NORTH;
		for (var light : lights) {
			BlockPos offset = rotateOffset((int) light.pos.x, (int) light.pos.y, (int) light.pos.z, facing);
			BlockPos lp = pos.offset(offset);
			if (on) {
				BlockState existing = level.getBlockState(lp);
				if (existing.isAir() || existing.is(Blocks.WATER) || existing.is(Blocks.LIGHT)) {
					level.setBlockAndUpdate(lp, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, light.lightLevel));
				}
			} else {
				BlockState existing = level.getBlockState(lp);
				if (existing.is(Blocks.LIGHT) && existing.getValue(LightBlock.LEVEL) == light.lightLevel) {
					level.setBlockAndUpdate(lp, Blocks.AIR.defaultBlockState());
				}
			}
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);

		if (!level.isClientSide()) {
			if (mech != null && mech.lights != null && !mech.lights.toggleable) {
				// mark block state lit=true
				level.setBlockAndUpdate(pos, state.setValue(LIT, true));
				updateLights(level, pos, true);
			}
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return interact(state, level, pos, player, hitResult, () -> super.useItemOn(stack, state, level, pos, player, hand, hitResult));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return interact(state, level, pos, player, hitResult, () -> super.useWithoutItem(state, level, pos, player, hitResult));
	}

	private InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player,
	                                   BlockHitResult hitResult, java.util.function.Supplier<InteractionResult> fallback) {
		boolean clickHandled = NexoClickActions.run(level, pos, player, mech != null ? mech.clickActions : null);
		boolean stateHandled = cycleFurnitureState(level, pos, state, player);
		BlockState current = level.getBlockState(pos);
		if (mech != null && mech.lights != null && mech.lights.toggleable) {
			boolean nowOn = !current.getValue(LIT);
			level.setBlockAndUpdate(pos, current.setValue(LIT, nowOn));
			updateLights(level, pos, nowOn);
			clickHandled = true;
			current = level.getBlockState(pos);
		}
		if (mech != null && mech.door != null) {
			boolean open = !current.getValue(OPEN);
			level.setBlockAndUpdate(pos, current.setValue(OPEN, open));
			playDoorSound(level, pos, open);
			if (open) {
				int closeDelay = parseDurationTicks(mech.door.automatic_close_delay);
				if (closeDelay > 0 && !level.isClientSide()) {
					DOOR_TIMERS.put(timerKey(level, pos), level.getGameTime() + closeDelay);
					level.scheduleTick(pos, this, closeDelay);
				}
			} else {
				DOOR_TIMERS.remove(timerKey(level, pos));
			}
			clickHandled = true;
		}

		InteractionResult seated = SeatLogic.sit(level, pos, level.getBlockState(pos), player, hitResult);
		if (seated != InteractionResult.PASS) return seated;
		if (clickHandled || stateHandled) return InteractionResult.SUCCESS;

		return fallback.get();
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		TimerKey key = timerKey(level, pos);
		long now = level.getGameTime();
		Long doorDeadline = DOOR_TIMERS.get(key);
		if (doorDeadline != null && doorDeadline <= now) {
			DOOR_TIMERS.remove(key);
			if (state.getValue(OPEN)) {
				level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
				playDoorSound(level, pos, false);
				state = level.getBlockState(pos);
			}
		}

		StateTimer stateTimer = STATE_TIMERS.get(key);
		if (stateTimer != null && stateTimer.deadline <= now && state.hasProperty(FURNITURE_STATE)) {
			STATE_TIMERS.remove(key);
			if (stateTimer.reset) {
				setFurnitureState(level, pos, state, 0);
			} else {
				advanceFurnitureState(level, pos, state, null, true);
			}
		}
		scheduleNextTimer(level, pos);
	}

	private boolean cycleFurnitureState(Level level, BlockPos pos, BlockState state, Player player) {
		if (!state.hasProperty(FURNITURE_STATE) || mech == null || mech.states == null || mech.states.isEmpty()) {
			return false;
		}
		if (level.isClientSide()) return true;
		if (mech.states.permission != null && !NexoExpressionConditions.test(level.getServer(), player,
				"hasPermission('" + mech.states.permission.replace("'", "") + "')")) return false;
		if (!NexoExpressionConditions.passAll(level.getServer(), player, mech.states.condition)) return false;
		return advanceFurnitureState((ServerLevel) level, pos, state, player, false);
	}

	private boolean advanceFurnitureState(ServerLevel level, BlockPos pos, BlockState state,
	                                     @Nullable Player player, boolean automatic) {
		List<Map.Entry<String, NexoItem.Mechanics.Furniture.FurnitureState>> configured = configuredStates();
		if (configured.isEmpty()) return false;
		int current = state.getValue(FURNITURE_STATE);
		int total = configured.size() + 1;

		int preferred = -1;
		if (current > 0 && current <= configured.size()) {
			String nextName = configured.get(current - 1).getValue().next_state;
			if (nextName != null) {
				for (int index = 0; index < configured.size(); index++) {
					if (configured.get(index).getKey().equals(nextName)) preferred = index + 1;
				}
			}
		}

		List<Integer> candidates = new ArrayList<>();
		if (preferred >= 0 && preferred != current) candidates.add(preferred);
		for (int offset = 1; offset <= total; offset++) {
			int candidate = (current + offset) % total;
			if (candidate != current && !candidates.contains(candidate)) candidates.add(candidate);
		}
		for (int candidate : candidates) {
			if (!automatic && player != null && candidate > 0) {
				NexoItem.Mechanics.Furniture.FurnitureState target = configured.get(candidate - 1).getValue();
				if (target.permission != null && !NexoExpressionConditions.test(level.getServer(), player,
						"hasPermission('" + target.permission.replace("'", "") + "')")) continue;
				if (!NexoExpressionConditions.passAll(level.getServer(), player, target.conditions)) continue;
			}
			setFurnitureState(level, pos, state, candidate);
			return true;
		}
		return false;
	}

	private void setFurnitureState(ServerLevel level, BlockPos pos, BlockState state, int target) {
		level.setBlockAndUpdate(pos, state.setValue(FURNITURE_STATE, target));
		scheduleStateTimer(level, pos, target);
	}

	private void scheduleStateTimer(ServerLevel level, BlockPos pos, int stateIndex) {
		TimerKey key = timerKey(level, pos);
		STATE_TIMERS.remove(key);
		if (stateIndex <= 0) return;
		List<Map.Entry<String, NexoItem.Mechanics.Furniture.FurnitureState>> configured = configuredStates();
		if (stateIndex > configured.size()) return;
		NexoItem.Mechanics.Furniture.FurnitureState state = configured.get(stateIndex - 1).getValue();
		String resetAfter = state.reset_after != null ? state.reset_after : mech.states.reset_after;
		String nextAfter = state.next_after != null ? state.next_after : mech.states.next_after;
		boolean reset = resetAfter != null && !resetAfter.isBlank();
		int delay = parseDurationTicks(reset ? resetAfter : nextAfter);
		if (delay <= 0) return;
		STATE_TIMERS.put(key, new StateTimer(level.getGameTime() + delay, reset));
		level.scheduleTick(pos, this, delay);
	}

	private List<Map.Entry<String, NexoItem.Mechanics.Furniture.FurnitureState>> configuredStates() {
		if (mech == null || mech.states == null) return List.of();
		List<Map.Entry<String, NexoItem.Mechanics.Furniture.FurnitureState>> states =
				new ArrayList<>(mech.states.entrySet());
		if (states.size() > 15) {
			Obsidian.LOGGER.warn("Nexo furniture has {} states; only the first 15 can be stored without a block entity",
					states.size());
			return List.copyOf(states.subList(0, 15));
		}
		return states;
	}

	private void scheduleNextTimer(ServerLevel level, BlockPos pos) {
		TimerKey key = timerKey(level, pos);
		long deadline = Long.MAX_VALUE;
		Long door = DOOR_TIMERS.get(key);
		if (door != null) deadline = Math.min(deadline, door);
		StateTimer state = STATE_TIMERS.get(key);
		if (state != null) deadline = Math.min(deadline, state.deadline);
		if (deadline != Long.MAX_VALUE) {
			level.scheduleTick(pos, this, (int) Math.max(1, deadline - level.getGameTime()));
		}
	}

	private static TimerKey timerKey(Level level, BlockPos pos) {
		return new TimerKey(level.dimension(), pos.immutable());
	}

	private record TimerKey(ResourceKey<Level> dimension, BlockPos pos) {}

	private record StateTimer(long deadline, boolean reset) {}

	private void playDoorSound(Level level, BlockPos pos, boolean open) {
		if (level.isClientSide() || mech == null || mech.door == null) return;
		var id = open ? mech.door.open_sound : mech.door.close_sound;
		if (id == null) return;
		BuiltInRegistries.SOUND_EVENT.getOptional(id)
				.ifPresent(sound -> level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F));
	}

	private static int parseDurationTicks(@Nullable String encoded) {
		if (encoded == null || encoded.isBlank()) return -1;
		try {
			String value = encoded.trim().toLowerCase(java.util.Locale.ROOT);
			double multiplier = value.endsWith("m") ? 1200.0 : value.endsWith("s") ? 20.0 : 1.0;
			if (value.endsWith("t") || value.endsWith("s") || value.endsWith("m")) {
				value = value.substring(0, value.length() - 1);
			}
			return Math.max(1, (int) Math.ceil(Double.parseDouble(value) * multiplier));
		} catch (RuntimeException exception) {
			Obsidian.LOGGER.warn("Ignoring malformed Nexo duration '{}': {}", encoded, exception.getMessage());
			return -1;
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		SeatLogic.clearSeats(level, pos);
		if (mech != null && mech.lights != null) updateLights(level, pos, false);
		DOOR_TIMERS.remove(timerKey(level, pos));
		STATE_TIMERS.remove(timerKey(level, pos));
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
}
