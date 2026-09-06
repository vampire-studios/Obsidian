package io.github.vampirestudios.obsidian.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Everything an event action or condition might need to know about the interaction that triggered it.
 *
 * <p>Actions used to be split by who declared them — block events could set blocks but not touch a player,
 * item events could touch a player but not see a block. The context collapses that: a trigger fills in
 * whatever it knows and every action asks the context for what it needs, so the same action works from an
 * item, a block or an entity interaction as long as the context carries the right parts.
 *
 * <p>Everything but the level is nullable, because triggers genuinely differ — a random block tick has no
 * player, and an item used in mid-air has no block position.
 */
public record InteractionContext(
		Level level,
		@Nullable Player player,
		@Nullable LivingEntity target,
		@Nullable BlockPos pos,
		@Nullable Direction facing,
		@Nullable InteractionHand hand,
		@Nullable Vec3 hitPos,
		@Nullable EquipmentSlot slot
) {

	/** An interaction with nothing but the player: right-clicking in mid-air, inventory ticks, pickups. */
	public static InteractionContext of(Player player) {
		return new InteractionContext(player.level(), player, null, null, null, null, null, null);
	}

	/** An interaction against another entity: {@code hurt_enemy}, {@code interact_entity}. */
	public static InteractionContext ofEntity(Player player, @Nullable LivingEntity target) {
		return new InteractionContext(player.level(), player, target, null, null, null, null, null);
	}

	/**
	 * An equipment event: the stack the actions are about is the one worn in {@code slot}, not whatever the
	 * player happens to be holding.
	 */
	public static InteractionContext ofEquipment(Player player, EquipmentSlot slot) {
		return new InteractionContext(player.level(), player, null, null, null, null, null, slot);
	}

	/**
	 * A projectile landing. The shooter fills in the player when there was one — a dispenser or a mob leaves
	 * it empty — and the landing fills in either the entity struck or the block hit, never both.
	 */
	public static InteractionContext ofProjectile(Level level, @Nullable Player shooter,
	                                              @Nullable LivingEntity struck, @Nullable BlockHitResult hit) {
		return new InteractionContext(
				level,
				shooter,
				struck,
				hit != null ? hit.getBlockPos() : null,
				hit != null ? hit.getDirection() : null,
				null,
				hit != null ? hit.getLocation() : null,
				null
		);
	}

	/** An interaction at a block, with or without a player behind it. */
	public static InteractionContext ofBlock(Level level, @Nullable BlockPos pos, @Nullable Player player, @Nullable Direction facing) {
		return new InteractionContext(level, player, null, pos, facing, null, null, null);
	}

	/** A block interaction that knows exactly where on the block it landed. */
	public static InteractionContext ofHit(Level level, @Nullable Player player, BlockHitResult hit) {
		return new InteractionContext(level, player, null, hit.getBlockPos(), hit.getDirection(), null, hit.getLocation(), null);
	}

	/** An item used on a block — the one trigger that knows the position, the face, the hand and the hit. */
	public static InteractionContext of(UseOnContext useOnContext) {
		return new InteractionContext(
				useOnContext.getLevel(),
				useOnContext.getPlayer(),
				null,
				useOnContext.getClickedPos(),
				useOnContext.getClickedFace(),
				useOnContext.getHand(),
				useOnContext.getClickLocation(),
				null
		);
	}

	/** The state at {@link #pos()}, or null when the interaction has no block. */
	public @Nullable BlockState blockState() {
		return pos != null ? level.getBlockState(pos) : null;
	}

	/**
	 * The stack the interaction is about — what is worn in the recorded slot, else the stack in the recorded
	 * hand, else the main hand. Empty when there is no player.
	 */
	public ItemStack heldStack() {
		if (player == null) return ItemStack.EMPTY;
		if (slot != null) return player.getItemBySlot(slot);
		return player.getItemInHand(hand != null ? hand : InteractionHand.MAIN_HAND);
	}

	/** The slot {@link #heldStack()} came out of — what break effects and cooldowns need to name. */
	public EquipmentSlot equipmentSlot() {
		if (slot != null) return slot;
		return hand != null ? hand.asEquipmentSlot() : EquipmentSlot.MAINHAND;
	}

	/** Where the interaction happened: the exact hit, the clicked block, or the player, in that order. */
	public @Nullable Vec3 position() {
		if (hitPos != null) return hitPos;
		if (pos != null) return Vec3.atCenterOf(pos);
		return player != null ? player.position() : null;
	}

	/**
	 * A block position to measure the world at — the clicked block if there is one, otherwise where the
	 * player is standing. Conditions about light, biome or height use this so they work on every trigger.
	 */
	public @Nullable BlockPos referencePos() {
		if (pos != null) return pos;
		return player != null ? player.blockPosition() : null;
	}

	/** The entity an action should act on: the target where the trigger supplies one, otherwise the player. */
	public @Nullable LivingEntity subject() {
		return target != null ? target : player;
	}

	public boolean isServer() {
		return !level.isClientSide();
	}

	/** The server level, or null on the client — for the actions that can only run server-side. */
	public @Nullable ServerLevel serverLevel() {
		return level instanceof ServerLevel serverLevel ? serverLevel : null;
	}

	public RandomSource random() {
		return level.getRandom();
	}

	public InteractionContext withTarget(@Nullable LivingEntity newTarget) {
		return new InteractionContext(level, player, newTarget, pos, facing, hand, hitPos, slot);
	}

	public InteractionContext withBlock(@Nullable BlockPos newPos, @Nullable Direction newFacing) {
		return new InteractionContext(level, player, target, newPos, newFacing, hand, hitPos, slot);
	}
}
