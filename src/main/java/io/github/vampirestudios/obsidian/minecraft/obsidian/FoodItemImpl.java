package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class FoodItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem item;

    public FoodItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (stack.isEmpty()) {
            if (item.food_information.returnItem != null) {
                return new ItemStack(Registry.ITEM.get(item.food_information.returnItem));
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            if (user instanceof PlayerEntity && !((PlayerEntity)user).isCreative()) {
                PlayerEntity playerEntity = (PlayerEntity) user;
                ItemStack itemStack = new ItemStack(Registry.ITEM.get(item.food_information.returnItem));
                if (!playerEntity.inventory.insertStack(itemStack)) {
                    playerEntity.dropItem(itemStack, false);
                }
            }

            return stack;
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return item.food_information.use_time;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return item.food_information.drinkable ? UseAction.DRINK : UseAction.EAT;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return Registry.SOUND_EVENT.get(item.food_information.drinkSound);
    }

    @Override
    public SoundEvent getEatSound() {
        return Registry.SOUND_EVENT.get(item.food_information.eatSound);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.is_enchantable;
    }

    @Override
    public int getEnchantability() {
        return item.information.enchantability;
    }

    @Override
    public Text getName() {
        return item.information.name.getName("item");
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}
