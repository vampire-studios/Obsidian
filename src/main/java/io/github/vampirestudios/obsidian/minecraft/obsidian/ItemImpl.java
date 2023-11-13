package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemImpl extends Item implements IRenderModeAware {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public BakedModel getModel(ItemStack stack, ItemDisplayContext mode, BakedModel original) {
        if (!item.information.getItemSettings().customRenderMode) return original;

        for (RenderModeModel renderModeModel : item.information.getItemSettings().renderModeModels) {
            BakedModel model = getModelBasedOnMode(mode, renderModeModel, original);
            if (model != original) return model;
        }

        return original;
    }

    private BakedModel getModelBasedOnMode(ItemDisplayContext mode, RenderModeModel renderModeModel, BakedModel original) {
        for (String renderMode : renderModeModel.modes) {
            boolean matchesMode = switch (renderMode) {
                case "HAND" -> isHandMode(mode);
                case "FIRST_PERSON_HAND" -> isFPHandMode(mode);
                case "THIRD_PERSON_HAND" -> isTPHandMode(mode);
                default -> mode.equals(ItemDisplayContext.valueOf(renderMode));
            };
            if (matchesMode)
                return Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(renderModeModel.model, "inventory"));
        }
        return original;
    }

    private boolean isHandMode(ItemDisplayContext mode) {
        return mode.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                || mode.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                || mode.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                || mode.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    private boolean isFPHandMode(ItemDisplayContext mode) {
        return mode.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                || mode.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
    }

    private boolean isTPHandMode(ItemDisplayContext mode) {
        return mode.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                || mode.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return item.useActions.getUseAnimation();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (item.useActions != null && !item.useActions.right_click_actions.isEmpty()) {
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
        return InteractionResultHolder.pass(getDefaultInstance());
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (item.information.getItemSettings().conversion != null) {
            if(item.information.getItemSettings().conversion.getFrom().contains(BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()))) {
                Item toItem = BuiltInRegistries.ITEM.get(item.information.getItemSettings().conversion.getTo());
                ItemStack toStack = toItem.getDefaultInstance();
                toStack.setTag(slot.getItem().getTag().copy());
                slot.set(toStack);
                stack.shrink(1);
                return true;
            }
        }
        return false;
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
