package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IContainerProvider;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.registry.OMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/**
 * The shared half of {@code behaviour.container}: which sizes are legal, which screen a size maps to,
 * and how a right-click reaches the block entity.
 *
 * <p>Container blocks are their own block classes — {@link ContainerBlockImpl} and
 * {@link HorizontalFacingContainerBlockImpl} — so that a block which declares no container keeps no
 * block entity at all. Everything both classes would otherwise duplicate lives here.
 */
public final class ContainerLogic {

	private static final Logger LOGGER = LogManager.getLogger();

	/** A hopper-shaped screen, the one legal size that is not a row of nine. */
	public static final int HOPPER_SIZE = 5;

	/** Nine rows of nine is the largest chest screen Obsidian registers. See {@link OMenus}. */
	public static final int MAX_ROWS = 9;

	private ContainerLogic() {
	}

	public static Block.Behaviour.@Nullable Container containerOf(@Nullable Block block) {
		return block == null || block.behaviour == null ? null : block.behaviour.container;
	}

	public static boolean isContainer(@Nullable Block block) {
		return containerOf(block) != null;
	}

	/**
	 * The declared size, corrected to something a screen exists for. An unusable size is rounded up to
	 * the next full row rather than refused, so one bad number does not cost the pack its block.
	 */
	public static int slotCount(Block.Behaviour.@Nullable Container container) {
		if (container == null) return 9 * 3;
		int declared = container.size;

		if (declared == HOPPER_SIZE) return HOPPER_SIZE;
		if (declared <= 0) return 9 * 3;

		int rows = Math.clamp((declared + 8) / 9, 1, MAX_ROWS);
		int corrected = rows * 9;
		if (corrected != declared) {
			LOGGER.warn("behaviour.container.size {} is not 5 or a multiple of 9 up to {}; using {}",
					declared, MAX_ROWS * 9, corrected);
		}
		return corrected;
	}

	/** The screen for a slot count {@link #slotCount} has already corrected. */
	public static MenuType<?> menuType(int slots) {
		if (slots == HOPPER_SIZE) return MenuType.HOPPER;
		return switch (slots / 9) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 3 -> MenuType.GENERIC_9x3;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			case 6 -> MenuType.GENERIC_9x6;
			case 7 -> OMenus.GENERIC_9x7;
			case 8 -> OMenus.GENERIC_9x8;
			default -> OMenus.GENERIC_9x9;
		};
	}

	/** Builds the menu backed by {@code entity}'s slots. */
	public static AbstractContainerMenu createMenu(int syncId, Inventory inventory, ContainerBlockEntity entity) {
		int slots = entity.getContainerSize();
		if (slots == HOPPER_SIZE) return new HopperMenu(syncId, inventory, entity);

		MenuType<?> type = menuType(slots);
		return new ChestMenu(type, syncId, inventory, entity, slots / 9);
	}

	/**
	 * The screen title: the container's own {@code name} when it declares one, otherwise the block's.
	 */
	public static Component title(Block definition, Block.Behaviour.@Nullable Container container) {
		if (container != null && container.name != null) {
			return container.name.getName("block", definition.information == null ? null : definition.information.id);
		}
		if (definition.information != null && definition.information.name != null) {
			return definition.information.name.getName("block", definition.information.id);
		}
		return Component.empty();
	}

	/**
	 * Opens the container for {@code player}. Returns {@link InteractionResult#PASS} when the block
	 * entity is missing or the player is sneaking, so the caller can carry on to its other behaviours.
	 */
	public static InteractionResult open(Level level, BlockPos pos, BlockState state, Player player) {
		if (player.isShiftKeyDown()) return InteractionResult.PASS;
		if (!(state.getBlock() instanceof IContainerProvider provider) || provider.getContainer() == null) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		if (!(level.getBlockEntity(pos) instanceof ContainerBlockEntity entity)) return InteractionResult.PASS;
		if (!entity.canOpen(player)) return InteractionResult.PASS;

		player.openMenu(entity);
		return InteractionResult.SUCCESS;
	}
}
