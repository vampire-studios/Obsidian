package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

//    @Override
//    public BakedModel getModel(ItemStack stack, ItemDisplayContext mode, BakedModel original) {
//        if (!item.information.getItemSettings().customRenderMode) return original;
//
//        for (RenderModeModel renderModeModel : item.information.getItemSettings().renderModeModels) {
//            BakedModel model = getModelBasedOnMode(mode, renderModeModel, original);
//            if (model != original) return model;
//        }
//
//        return original;
//    }
//
//    private BakedModel getModelBasedOnMode(ItemDisplayContext mode, RenderModeModel renderModeModel, BakedModel original) {
//        for (String renderMode : renderModeModel.modes) {
//            boolean matchesMode = switch (renderMode) {
//                case "HAND" -> isHandMode(mode);
//                case "FIRST_PERSON_HAND" -> isFPHandMode(mode);
//                case "THIRD_PERSON_HAND" -> isTPHandMode(mode);
//                default -> mode.equals(ItemDisplayContext.valueOf(renderMode));
//            };
//            if (matchesMode)
//                return Minecraft.getInstance().getModelManager().getModel(new ModelIdentifier(renderModeModel.model, renderMode));
//        }
//        return original;
//    }
//
//    private boolean isHandMode(ItemDisplayContext mode) {
//        return mode.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
//                || mode.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
//                || mode.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
//                || mode.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
//    }
//
//    private boolean isFPHandMode(ItemDisplayContext mode) {
//        return mode.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
//                || mode.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
//    }
//
//    private boolean isTPHandMode(ItemDisplayContext mode) {
//        return mode.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
//                || mode.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
//    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return item.useActions.getUseAnimation();
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (item.useActions != null && item.useActions.right_click_actions != null && !item.useActions.right_click_actions.isEmpty()) {
            switch (item.useActions.right_click_actions) {
                case "open_gui":
                    user.openMenu(item.useActions.openGui(ContainerLevelAccess.create(world, user.blockPosition())));
                    break;
                case "run_command":
                    //TODO
                    break;
                case "open_url":
                    if (world.isClientSide())
                        Minecraft.getInstance().setScreen(new ConfirmLinkScreen(bl -> {
                            if (bl) {
                                Util.getPlatform().openUri(item.useActions.url);
                            }
                        }, item.useActions.url, true));
                    break;
            }
        }
        EventActionHandler.handleOnUse(user, item);
        return InteractionResult.PASS;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (item.information.getItemSettings().conversion != null) {
            if(item.information.getItemSettings().conversion.from().contains(BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()))) {
                Item toItem = BuiltInRegistries.ITEM.getValue(item.information.getItemSettings().conversion.to());
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
}
