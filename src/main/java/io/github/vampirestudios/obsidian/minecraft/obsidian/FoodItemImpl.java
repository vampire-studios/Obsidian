package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class FoodItemImpl extends ItemImpl {

	/** Narrows {@link ItemImpl#item} to the food declaration; assigned separately because it shadows it. */
	public FoodItem item;

	public FoodItemImpl(FoodItem item, Properties settings) {
		super(item, settings);
		this.item = item;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		super.finishUsingItem(stack, world, user);
		if (user instanceof ServerPlayer serverPlayerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
		}

		Identifier returnItem = item.food_information != null ? item.food_information.returnItem : null;

		if (stack.isEmpty()) {
			return returnItem != null
					? new ItemStack(BuiltInRegistries.ITEM.getValue(returnItem))
					: ItemStack.EMPTY;
		}

		// Eating one out of a stack leaves the rest, so the container has to be handed over separately.
		if (returnItem != null && user instanceof Player playerEntity && !playerEntity.getAbilities().instabuild) {
			ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getValue(returnItem));
			if (!playerEntity.getInventory().add(itemStack)) {
				playerEntity.drop(itemStack, false, Prediction.PREDICTED);
			}
		}

		return stack;
	}

	@Override
	public @NonNull InteractionResult use(Level world, Player user, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, user, hand);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
	}
}
