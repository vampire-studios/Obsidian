package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.addon_modules.ItemModuleHelper;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.InteractionContext;
import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianItemHolder;
import io.github.vampirestudios.obsidian.api.obsidian.item.Tier;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class ItemImpl extends Item implements ObsidianItemHolder {

	private static final Logger LOGGER = LogManager.getLogger();

	public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

	public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
		super(applyTierRepairItem(item, settings));
		this.item = item;
	}

	private static Properties applyTierRepairItem(
			io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
		if (item.components != null && item.components.get(DataComponents.REPAIRABLE) != null) return settings;

		return switch (item) {
			case ToolItem tool -> Tier.applyRepairItem(tool.material, settings);
			case WeaponItem weapon -> Tier.applyRepairItem(weapon.material, settings);
			default -> settings;
		};
	}

	@Override
	public io.github.vampirestudios.obsidian.api.obsidian.item.Item obsidianItem() {
		return item;
	}

	/**
	 * Applies the pack-declared data components onto the item properties, returning the same instance so
	 * it can be passed straight to {@code super(...)}. Shared by every item implementation.
	 */
	public static Properties applyComponents(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
		if (item.components != null) ItemModuleHelper.applyAllComponents(settings, item.components);
		return settings;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return item.useActions != null ? item.useActions.getUseAnimation() : ItemUseAnimation.NONE;
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		runRightClickShorthand(world, user);
		EventActionHandler.handleOnUse(user, item);
		return InteractionResult.PASS;
	}

	/**
	 * Runs the {@code use_actions.right_click_actions} shorthand, if the item has one.
	 *
	 * <p>Everything the shorthand can do server-side is an ordinary event action, so it is converted to one
	 * and run through the same dispatch as the {@code events} list — one code path, one set of behaviour.
	 * {@code open_url} is the exception: it has to open a screen on the client, so it stays here.
	 *
	 * @return whether the shorthand opened a screen, so callers do not open a second one over it.
	 */
	protected boolean runRightClickShorthand(Level world, Player user) {
		if (item.useActions == null || !item.useActions.hasRightClickAction()) return false;

		Map<String, Object> shorthand = item.useActions.toActionConfig();
		if (shorthand != null) {
			EventActionHandler.dispatch(InteractionContext.of(user), (String) shorthand.get("action"), shorthand, "on_use");
			return item.useActions.opensGui();
		}

		if (item.useActions.opensUrl()) {
			if (world.isClientSide())
				Minecraft.getInstance().gui.setScreen(new ConfirmLinkScreen(bl -> {
					if (bl) {
						Util.getPlatform().openUri(item.useActions.url);
					}
				}, item.useActions.url, true));
			return true;
		}

		LOGGER.warn("Unknown right_click_actions value \"{}\" on item {}.",
				item.useActions.right_click_actions, item.information.id);
		return false;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (item.information.conversion != null) {
			if (item.information.conversion.from().contains(BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()))) {
				Item toItem = BuiltInRegistries.ITEM.getValue(item.information.conversion.to());
				ItemStack toStack = toItem.getDefaultInstance();
				toStack.transmuteCopy(slot.getItem().getItem(), slot.getItem().getCount());
				slot.set(toStack);
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag context) {
		item.addLore(tooltip);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!(attacker instanceof Player player)) return;
		EventActionHandler.handleHurtEnemy(target, player, item);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);
		EventActionHandler.handleOnMiningBlock(player, state, pos, item, this);
		return super.mineBlock(stack, level, state, pos, miningEntity);
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		if (!(livingEntity instanceof Player player)) {
			super.onUseTick(level, livingEntity, stack, remainingUseDuration);
			return;
		}
		EventActionHandler.handleOnUseTick(player, item);
		super.onUseTick(level, livingEntity, stack, remainingUseDuration);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		EventActionHandler.handleOnUseOn(context, item);
		return super.useOn(context);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player player)) return super.finishUsingItem(stack, level, livingEntity);
		EventActionHandler.handleOnFinishUsing(player, item);
		return super.finishUsingItem(stack, level, livingEntity);
	}

	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
		if (!(entity instanceof Player player)) {
			super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
			return;
		}
		EventActionHandler.handleOnInventoryTick(player, item);
		super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
	}

	@Override
	public void onCraftedBy(ItemStack itemStack, Player player) {
		EventActionHandler.handleOnItemCrafted(player, item);
		super.onCraftedBy(itemStack, player);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		EventActionHandler.handleInteractEntity(player, target, item);
		return super.interactLivingEntity(stack, player, target, hand);
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
		if (livingEntity instanceof Player player) {
			EventActionHandler.handleOnStopUsing(player, item);
		}
		return super.releaseUsing(stack, level, livingEntity, timeLeft);
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		// Actions all act on a player, so this only fires for a stack that a player dropped.
		if (itemEntity.getOwner() instanceof Player player) {
			EventActionHandler.handleOnDestroyed(player, item);
		}
		super.onDestroyed(itemEntity);
	}
}
