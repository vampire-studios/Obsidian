package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.minecraft.BundleContents.Mutable;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public class BundleItem extends Item {
	public static final int MAX_SHOWN_GRID_ITEMS_X = 4;
	public static final int MAX_SHOWN_GRID_ITEMS_Y = 3;
	public static final int MAX_SHOWN_GRID_ITEMS = 12;
	public static final int OVERFLOWING_MAX_SHOWN_GRID_ITEMS = 11;
	private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
	private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);
	private static final int TICKS_AFTER_FIRST_THROW = 10;
	private static final int TICKS_BETWEEN_THROWS = 2;
	private static final int TICKS_MAX_THROW_DURATION = 200;
	private io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

	public BundleItem(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Item.Properties properties) {
		super(properties);
		this.item = item;
	}

	public static float getFullnessDisplay(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return bundleContents.weight().floatValue();
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
		BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
		if (bundleContents == null) {
			return false;
		} else {
			ItemStack itemStack2 = slot.getItem();
			Mutable mutable = new Mutable(bundleContents);
			if (clickAction == ClickAction.PRIMARY && !itemStack2.isEmpty()) {
				if (mutable.tryTransfer(slot, player) > 0) {
					playInsertSound(player);
				} else {
					playInsertFailSound(player);
				}

				itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
				this.broadcastChangesOnContainerMenu(player);
				return true;
			} else if (clickAction == ClickAction.SECONDARY && itemStack2.isEmpty()) {
				ItemStack itemStack3 = mutable.removeOne();
				if (itemStack3 != null) {
					ItemStack itemStack4 = slot.safeInsert(itemStack3);
					if (itemStack4.getCount() > 0) {
						mutable.tryInsert(itemStack4);
					} else {
						playRemoveOneSound(player);
					}
				}

				itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
				this.broadcastChangesOnContainerMenu(player);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack itemStack2, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction == ClickAction.PRIMARY && itemStack2.isEmpty()) {
			toggleSelectedItem(itemStack, -1);
			return false;
		} else {
			BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
			if (bundleContents == null) {
				return false;
			} else {
				Mutable mutable = new Mutable(bundleContents);
				if (clickAction == ClickAction.PRIMARY && !itemStack2.isEmpty()) {
					if (slot.allowModification(player) && mutable.tryInsert(itemStack2) > 0) {
						playInsertSound(player);
					} else {
						playInsertFailSound(player);
					}

					itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else if (clickAction == ClickAction.SECONDARY && itemStack2.isEmpty()) {
					if (slot.allowModification(player)) {
						ItemStack itemStack3 = mutable.removeOne();
						if (itemStack3 != null) {
							playRemoveOneSound(player);
							slotAccess.set(itemStack3);
						}
					}

					itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else {
					toggleSelectedItem(itemStack, -1);
					return false;
				}
			}
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
		player.startUsingItem(interactionHand);
		return InteractionResult.SUCCESS;
	}

	private void dropContent(Level level, Player player, ItemStack itemStack) {
		if (this.dropContent(itemStack, player)) {
			playDropContentsSound(level, player);
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return bundleContents.weight().compareTo(Fraction.ZERO) > 0;
	}

	@Override
	public int getBarWidth(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		StorageItemComponent storageItemComponent = itemStack.getOrDefault(OItemComponents.STORAGE_ITEM, StorageItemComponent.DEFAULT);
		BundleInteraction bundleInteraction = itemStack.getOrDefault(OItemComponents.BUNDLE_INTERACTION, BundleInteraction.DEFAULT);
		return Math.min(1 + Mth.mulAndTruncate(bundleContents.weight(), bundleInteraction.numViewableSlots()), bundleInteraction.numViewableSlots() + 1);
	}

	@Override
	public int getBarColor(ItemStack itemStack) {
		BundleBarColors barColorComponent = itemStack.getOrDefault(OItemComponents.BUNDLE_BAR_COLORS, BundleBarColors.DEFAULT);
		BundleContents contents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return contents.weight().compareTo(Fraction.ONE) >= 0
				? barColorComponent.fullBarColor()
				: barColorComponent.barColor();
	}

	public static void toggleSelectedItem(ItemStack itemStack, int i) {
		BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
		if (bundleContents != null) {
			Mutable mutable = new Mutable(bundleContents);
			mutable.toggleSelectedItem(i);
			itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
		}
	}

	public static boolean hasSelectedItem(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
		return bundleContents != null && bundleContents.getSelectedItem() != -1;
	}

	public static int getSelectedItem(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return bundleContents.getSelectedItem();
	}

	public static ItemStack getSelectedItemStack(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
		return bundleContents != null && bundleContents.getSelectedItem() != -1 ? bundleContents.getItemUnsafe(bundleContents.getSelectedItem()) : ItemStack.EMPTY;
	}

	public static int getNumberOfItemsToShow(ItemStack itemStack) {
		BundleContents bundleContents = itemStack.getOrDefault(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return bundleContents.getNumberOfItemsToShow();
	}

	private boolean dropContent(ItemStack itemStack, Player player) {
		BundleContents bundleContents = itemStack.get(OItemComponents.BUNDLE_CONTENTS);
		if (bundleContents != null && !bundleContents.isEmpty()) {
			Optional<ItemStack> optional = removeOneItemFromBundle(itemStack, player, bundleContents);
			if (optional.isPresent()) {
				player.drop((ItemStack) optional.get(), true, Prediction.PREDICTED);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static Optional<ItemStack> removeOneItemFromBundle(ItemStack itemStack, Player player, BundleContents bundleContents) {
		Mutable mutable = new Mutable(bundleContents);
		ItemStack itemStack2 = mutable.removeOne();
		if (itemStack2 != null) {
			playRemoveOneSound(player);
			itemStack.set(OItemComponents.BUNDLE_CONTENTS, mutable.toImmutable());
			return Optional.of(itemStack2);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
		if (livingEntity instanceof Player player) {
			int j = this.getUseDuration(itemStack, livingEntity);
			boolean bl = i == j;
			if (bl || i < j - 10 && i % 2 == 0) {
				this.dropContent(level, player, itemStack);
			}
		}
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
		return 200;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
		return ItemUseAnimation.BUNDLE;
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
		TooltipDisplay tooltipDisplay = itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		return !tooltipDisplay.shows(OItemComponents.BUNDLE_CONTENTS)
				? Optional.empty()
				: Optional.ofNullable(itemStack.get(OItemComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new);
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		BundleContents bundleContents = itemEntity.getItem().get(OItemComponents.BUNDLE_CONTENTS);
		if (bundleContents != null) {
			itemEntity.getItem().set(OItemComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
			ItemUtils.onContainerDestroyed(itemEntity, bundleContents.itemCopyStream());
		}
	}

	private static void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}

	private static void playDropContentsSound(Level level, Entity entity) {
		level.playSound(
				null, entity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F
		);
	}

	private void broadcastChangesOnContainerMenu(Player player) {
		AbstractContainerMenu abstractContainerMenu = player.containerMenu;
		if (abstractContainerMenu != null) {
			abstractContainerMenu.slotsChanged(player.getInventory());
		}
	}
}
