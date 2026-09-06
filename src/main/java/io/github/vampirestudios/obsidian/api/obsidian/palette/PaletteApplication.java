package io.github.vampirestudios.obsidian.api.obsidian.palette;

import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.registry.components.PaletteApplicator;
import io.github.vampirestudios.obsidian.registry.components.PaletteComponent;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Applying a palette to something, and the interactions that trigger it.
 *
 * <p>An applicator is any item carrying an {@code obsidian:palette_applicator} component — there is
 * no dedicated item class, so a "chroma set" can be an ordinary Obsidian item, a food, a tool, or a
 * block item, and can itself be colourable so its own model shows the colours it carries.</p>
 *
 * <p>The interaction: hold the applicator in one hand and the item to paint in the other, then use.
 * That is handled on both the item and the block use paths, since right-clicking while looking at a
 * block never reaches the item one.</p>
 */
public final class PaletteApplication {

	private PaletteApplication() {
	}

	// -------------------------------------------------------------------------
	// Applying
	// -------------------------------------------------------------------------

	/**
	 * Works out what a target's palette selection becomes when {@code applicator} is used on it, or
	 * {@code null} when nothing would change.
	 *
	 * <p>A full-repaint applicator hands over its palette and clears any leftover per-channel
	 * overrides. One restricted to named channels writes just those, resolved through its own paint,
	 * as overrides — which is how a "detail brush" recolours the trim of an item without disturbing
	 * the palette it was painted with.</p>
	 *
	 * @param channelsId the palette describing the target's channels
	 */
	public static PaletteComponent repaint(PaletteComponent current, Identifier channelsId, PaletteApplicator applicator) {
		Palette channels = PaletteResolver.palette(channelsId);
		if (channels == null) return null;

		PaletteComponent paint = applicator.paint();
		Palette paintPalette = paint.palette().map(PaletteResolver::palette).orElse(null);
		if (paintPalette != null && !channels.accepts(paintPalette)) return null;

		Map<String, PaletteChannel> available = channels.allChannels();

		if (applicator.isFullRepaint() && paintPalette != null) {
			PaletteComponent repainted = new PaletteComponent(paint.palette(), Map.of());
			return withOverrides(repainted, available, paint, available.keySet());
		}

		List<String> targets = applicator.targetChannels(available.keySet());
		PaletteComponent repainted = withOverrides(current, available, paint, targets);
		return repainted.equals(current) ? null : repainted;
	}

	private static PaletteComponent withOverrides(PaletteComponent base, Map<String, PaletteChannel> available,
	                                              PaletteComponent paint, Iterable<String> targets) {
		Map<String, Integer> overrides = new LinkedHashMap<>(base.overrides());

		for (String channelName : targets) {
			PaletteChannel channel = available.get(channelName);
			if (channel == null || channel.locked) continue;

			// Ad-hoc colours on the applicator win over its palette, exactly as they do on a stack.
			Integer explicit = paint.overrides().get(channelName);
			if (explicit != null) {
				overrides.put(channelName, explicit);
				continue;
			}

			Palette paintPalette = paint.palette().map(PaletteResolver::palette).orElse(null);
			if (paintPalette == null) continue;

			Integer resolved = PaletteResolver.resolveAll(paintPalette).get(channel.sourceChannel());
			if (resolved != null) overrides.put(channelName, resolved);
		}

		return new PaletteComponent(base.palette(), Map.copyOf(overrides));
	}

	/**
	 * Paints {@code target} with {@code applicatorStack} when that stack is an applicator, spending
	 * it as configured. Used by the inventory-click path, where either stack may be the applicator.
	 *
	 * @return whether the click was consumed
	 */
	public static boolean applyByClick(ItemStack applicatorStack, ItemStack target, Player player) {
		PaletteApplicator applicator = applicatorStack.get(OItemComponents.PALETTE_APPLICATOR);
		if (applicator == null || target.isEmpty()) return false;
		if (PaletteResolver.channelsOf(target) == null) return false;

		if (!applyToStack(target, applicator)) return false;

		spendStack(player, applicatorStack, applicator);
		// The click runs on both sides; only the server should broadcast the sound.
		if (!player.level().isClientSide()) playPaintSound(player.level(), player.blockPosition());
		return true;
	}

	/** Repaints a stack in place. Returns {@code false} when the applicator has no effect on it. */
	public static boolean applyToStack(ItemStack target, PaletteApplicator applicator) {
		Identifier channels = PaletteResolver.channelsOf(target);
		if (channels == null) return false;

		PaletteComponent repainted = repaint(PaletteResolver.selectionOf(target), channels, applicator);
		if (repainted == null) return false;

		target.set(OItemComponents.PALETTE, repainted);
		return true;
	}

	// -------------------------------------------------------------------------
	// Interactions
	// -------------------------------------------------------------------------

	public static void register() {
		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (level.isClientSide()) return InteractionResult.PASS;

			PaletteApplicator applicator = player.getItemInHand(hand).get(OItemComponents.PALETTE_APPLICATOR);
			if (applicator == null) return InteractionResult.PASS;

			InteractionResult result = repaintOtherHand(player, hand, applicator);
			if (result == InteractionResult.PASS) {
				// Aimed at nothing, holding nothing to paint: say so, rather than looking broken.
				player.sendOverlayMessage(Component.translatable("obsidian.palette.no_target"));
			}
			return result;
		});

		// Right-clicking while looking at a block never reaches UseItemCallback, so the same
		// interaction has to be handled here too — otherwise an applicator only works aimed at air.
		// Passes silently when there is nothing to paint, so ordinary block use still works.
		UseBlockCallback.EVENT.register((player, level, hand, _) -> {
			if (level.isClientSide()) return InteractionResult.PASS;

			PaletteApplicator applicator = player.getItemInHand(hand).get(OItemComponents.PALETTE_APPLICATOR);
			if (applicator == null) return InteractionResult.PASS;

			return repaintOtherHand(player, hand, applicator);
		});
	}

	/**
	 * Paints the stack in the hand opposite {@code hand}. Returns {@link InteractionResult#PASS}
	 * when there is nothing there to paint.
	 */
	private static InteractionResult repaintOtherHand(Player player, InteractionHand hand, PaletteApplicator applicator) {
		InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		ItemStack target = player.getItemInHand(other);

		if (target.isEmpty() || PaletteResolver.channelsOf(target) == null) return InteractionResult.PASS;

		if (!applyToStack(target, applicator)) {
			player.sendOverlayMessage(Component.translatable("obsidian.palette.no_effect"));
			return InteractionResult.PASS;
		}

		spend(player, hand, player.getItemInHand(hand), applicator);
		playPaintSound(player.level(), player.blockPosition());
		return InteractionResult.SUCCESS;
	}

	private static void spend(Player player, InteractionHand hand, ItemStack held, PaletteApplicator applicator) {
		if (player.hasInfiniteMaterials()) return;

		if (applicator.consumes()) held.shrink(1);
		else if (applicator.durabilityCost() > 0) {
			held.hurtAndBreak(applicator.durabilityCost(), player, hand.asEquipmentSlot());
		}
	}

	/**
	 * As {@link #spend}, for a stack that is not in a hand — an applicator clicked in an inventory
	 * has no equipment slot to report a break from, so durability is spent server-side only and the
	 * client picks up the result from the slot sync.
	 */
	private static void spendStack(Player player, ItemStack stack, PaletteApplicator applicator) {
		if (player.hasInfiniteMaterials()) return;

		if (applicator.consumes()) {
			stack.shrink(1);
			return;
		}

		int cost = applicator.durabilityCost();
		if (cost <= 0 || !stack.isDamageableItem()) return;

		if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel serverLevel) {
			stack.hurtAndBreak(cost, serverLevel, serverPlayer, _ -> {
			});
		}
	}

	private static void playPaintSound(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
	}
}
