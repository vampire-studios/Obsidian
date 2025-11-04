package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PickaxeItemImpl extends Item {

    public ToolItem item;

    public PickaxeItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
        super(settings.pickaxe(material, 1, 1));
        this.item = item;
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
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        EventActionHandler.handleOnUse(user, item);
        return InteractionResult.PASS;
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

}