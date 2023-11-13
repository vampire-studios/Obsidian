package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

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
                return new ItemStack(BuiltInRegistries.ITEM.get(item.food_information.returnItem));
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            if (user instanceof Player playerEntity && !((Player)user).getAbilities().instabuild) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(item.food_information.returnItem));
                if (!playerEntity.getInventory().add(itemStack)) {
                    playerEntity.drop(itemStack, false);
                }
            }

            return stack;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return item.food_information.use_time;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return item.food_information.drinkable ? UseAnim.DRINK : UseAnim.EAT;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return BuiltInRegistries.SOUND_EVENT.get(item.food_information.drinkSound);
    }

    @Override
    public SoundEvent getEatingSound() {
        return BuiltInRegistries.SOUND_EVENT.get(item.food_information.eatSound);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.getItemSettings().isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.getItemSettings().enchantability;
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("item");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        if (item.lore != null) {
            for (TooltipInformation tooltipInformation : item.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}
