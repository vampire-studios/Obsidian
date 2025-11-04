package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

public class FoodItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem item;

    public FoodItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }

        if (stack.isEmpty()) {
            if (item.food_information.returnItem != null) {
                return new ItemStack(BuiltInRegistries.ITEM.getValue(item.food_information.returnItem));
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            if (user instanceof Player playerEntity && !((Player)user).getAbilities().instabuild) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getValue(item.food_information.returnItem));
                if (!playerEntity.getInventory().add(itemStack)) {
                    playerEntity.drop(itemStack, false);
                }
            }

            return stack;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return item.food_information.use_time;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return item.food_information.drinkable ? ItemUseAnimation.DRINK : ItemUseAnimation.EAT;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }
}
