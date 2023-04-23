package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.common.collect.Maps;
import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.events.FlexEventContext;
import io.github.vampirestudios.obsidian.api.events.FlexEventHandler;
import io.github.vampirestudios.obsidian.api.events.FlexEventResult;
import io.github.vampirestudios.obsidian.api.events.IEventRunner;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ItemImpl extends Item implements IRenderModeAware, IEventRunner {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public BakedModel getModel(ItemStack stack, ItemDisplayContext mode, BakedModel original) {
        if (item.information.customRenderMode) {
            for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                for (String renderMode : renderModeModel.modes) {
                    final boolean firstPersonHands = mode.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                            || mode.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
                    final boolean thirdPersonHands = mode.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                            || mode.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
                    switch (renderMode) {
                        case "HAND" -> {
                            if (firstPersonHands || thirdPersonHands) {
                                return Minecraft.getInstance().getModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        case "FIRST_PERSON_HAND" -> {
                            if (firstPersonHands) {
                                return Minecraft.getInstance().getModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        case "THIRD_PERSON_HAND" -> {
                            if (thirdPersonHands) {
                                return Minecraft.getInstance().getModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        default -> {
                            if (mode.equals(ItemDisplayContext.valueOf(renderMode))) {
                                return Minecraft.getInstance().getModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                    }
                }
            }
        }
        return original;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return item.useActions.getAction();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (item.useActions != null && item.useActions.right_click_actions.equals("open_gui")) {
            switch (item.useActions.right_click_actions) {
                case "open_gui":
                    user.openMenu(new SimpleMenuProvider((syncId, inventory, playerx) -> switch (item.useActions.gui_size) {
                        case 1 -> ChestMenu.oneRow(syncId, playerx.getInventory());
                        case 2 -> ChestMenu.twoRows(syncId, playerx.getInventory());
                        case 3 -> ChestMenu.threeRows(syncId, playerx.getInventory());
                        case 4 -> ChestMenu.fourRows(syncId, playerx.getInventory());
                        case 5 -> ChestMenu.fiveRows(syncId, playerx.getInventory());
                        case 6 -> ChestMenu.sixRows(syncId, playerx.getInventory());
                        default -> throw new IllegalStateException("Unexpected value: " + item.useActions.gui_size);
                    }, item.useActions.gui_title.getName("gui")));
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

        ItemStack heldItem = user.getItemInHand(hand);
        if (item.useActions.useTime != null && item.useActions.useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(world, user, hand, heldItem), () -> {
                user.startUsingItem(hand);
                return FlexEventResult.consume(heldItem);
            }).holder();
        else
            return runEvent("use_on_air", FlexEventContext.of(world, user, hand, heldItem), () -> FlexEventResult.of(super.use(world, user, hand))).holder();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack heldItem = context.getItemInHand();

        FlexEventResult result = runEvent("use_on_block", FlexEventContext.of(context), () -> new FlexEventResult(super.useOn(context), heldItem));

        if (result.stack() != heldItem && context.getPlayer() != null) {
            context.getPlayer().setItemInHand(context.getHand(), result.stack());
        }

        return result.result();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        runEvent("stopped_using",
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> {
                    super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
                    return FlexEventResult.pass(stack);
                });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving) {
        Supplier<FlexEventResult> resultSupplier = () -> FlexEventResult.success(super.finishUsingItem(heldItem, worldIn, entityLiving));

        FlexEventResult result = runEvent("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
        if (result.result() != InteractionResult.SUCCESS)
            return result.stack();

        return runEvent("use", FlexEventContext.of(worldIn, entityLiving, heldItem), () -> FlexEventResult.success(result.stack())).stack();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        FlexEventResult result = runEvent("update",
                FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                () -> {
                    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
                    return FlexEventResult.pass(stack);
                });
        if (result.stack() != stack) {
            entityIn.getSlot(itemSlot).set(result.stack());
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.enchantability;
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("item");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    //region IFlexItem
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    @Override
    public void addEventHandler(String eventName, FlexEventHandler eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }

    @Nullable
    @Override
    public FlexEventHandler getEventHandler(String eventName) {
        return eventHandlers.get(eventName);
    }
}
