package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/**
 * {@code behaviour.lock}: a block that refuses to be used until someone holds the right item.
 *
 * <p>The lock sits in front of every other interaction — seats, containers, {@code on_interact} events —
 * so a locked block does nothing at all until it is opened. Whether opening is permanent is the
 * declaration's choice: a lock that {@code discard}s itself remembers being opened in the block state,
 * and one that does not asks for the key every time.
 */
public final class LockLogic {

	private static final Logger LOGGER = LogManager.getLogger();

	/** Set once a discarding lock has been opened. See {@link BlockVariants#unlocked()}. */
	public static final BooleanProperty UNLOCKED = BooleanProperty.create("unlocked");

	private LockLogic() {
	}

	public static Block.Behaviour.@Nullable Lock lockOf(@Nullable Block block) {
		return block == null || block.behaviour == null ? null : block.behaviour.lock;
	}

	/** Whether this block's lock is opened for good the first time it is unlocked. */
	public static boolean discards(@Nullable Block block) {
		Block.Behaviour.Lock lock = lockOf(block);
		return lock != null && lock.discard;
	}

	/**
	 * Handles the click on a locked block.
	 *
	 * @return {@link InteractionResult#PASS} when the block is not locked, or has already been opened, so
	 * the caller carries on to its own behaviour; anything else means the lock consumed the interaction.
	 */
	public static InteractionResult tryUnlock(Level level, BlockPos pos, BlockState state, Player player,
	                                          InteractionHand hand, @Nullable Block block,
	                                          @Nullable BlockVariants variants) {
		Block.Behaviour.Lock lock = lockOf(block);
		if (lock == null) return InteractionResult.PASS;

		BooleanProperty unlocked = variants == null ? null : variants.unlocked();
		if (unlocked != null && state.hasProperty(unlocked) && state.getValue(unlocked)) {
			return InteractionResult.PASS;
		}

		ItemStack held = player.getItemInHand(hand);
		if (!matchesKey(lock, held)) {
			// Vanilla's own locked-container feedback — the rattle plus the block's name in the action
			// bar. It plays the sound itself, so there is nothing else to do here.
			if (!level.isClientSide()) {
				BaseContainerBlockEntity.sendChestLockedNotifications(Vec3.atCenterOf(pos), player,
						state.getBlock().getName());
			}
			return InteractionResult.SUCCESS;
		}

		// The key fits. The client predicts nothing here and just carries on down its own chain; only the
		// server consumes the key, remembers the unlock and runs the command.
		if (level.isClientSide()) return InteractionResult.PASS;

		if (lock.consumeKey && !player.getAbilities().instabuild) {
			held.shrink(1);
		}

		if (lock.discard && unlocked != null && state.hasProperty(unlocked)) {
			level.setBlock(pos, state.setValue(unlocked, true), net.minecraft.world.level.block.Block.UPDATE_ALL);
		}

		runCommand(level, pos, player, lock);

		// Opening the lock steps aside so the same click goes on to do whatever the block does — a locked
		// chest opens, a locked chair seats you. A lock that does not discard itself simply asks for the
		// key again next time.
		return InteractionResult.PASS;
	}

	private static boolean matchesKey(Block.Behaviour.Lock lock, ItemStack held) {
		// A lock naming no key is a lock nobody can open — refuse rather than let anything through.
		if (lock.key == null) return false;

		Item key = BuiltInRegistries.ITEM.getValue(lock.key);
		return !held.isEmpty() && held.is(key);
	}

	private static void runCommand(Level level, BlockPos pos, Player player, Block.Behaviour.Lock lock) {
		if (lock.command == null || lock.command.isBlank()) return;
		if (!(level instanceof ServerLevel serverLevel)) return;

		MinecraftServer server = serverLevel.getServer();
		try {
			server.getCommands().performPrefixedCommand(
					server.createCommandSourceStack()
							.withLevel(serverLevel)
							.withPosition(Vec3.atCenterOf(pos))
							.withEntity(player)
							.withPermission(permission -> permission == Permissions.COMMANDS_GAMEMASTER)
							.withSuppressedOutput(),
					lock.command);
		} catch (Exception e) {
			LOGGER.warn("behaviour.lock command \"{}\" failed at {}", lock.command, pos, e);
		}
	}
}
