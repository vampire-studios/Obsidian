package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ItemImpl extends Item implements IRenderModeAware {

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
            if (item.useActions.right_click_actions.equals("open_gui")) {
                user.openMenu(new SimpleMenuProvider((syncId, inventory, playerx) -> switch (item.useActions.gui_size) {
                    case 1 -> ChestMenu.oneRow(syncId, playerx.getInventory());
                    case 2 -> ChestMenu.twoRows(syncId, playerx.getInventory());
                    case 3 -> ChestMenu.threeRows(syncId, playerx.getInventory());
                    case 4 -> ChestMenu.fourRows(syncId, playerx.getInventory());
                    case 5 -> ChestMenu.fiveRows(syncId, playerx.getInventory());
                    case 6 -> ChestMenu.sixRows(syncId, playerx.getInventory());
                    default -> throw new IllegalStateException("Unexpected value: " + item.useActions.gui_size);
                }, item.useActions.gui_title.getName("gui")));
            } else if (item.useActions.right_click_actions.equals("run_command")) {
                //TODO
            } else if (item.useActions.right_click_actions.equals("open_url")) {
                if (world.isClientSide())
                    Minecraft.getInstance().setScreen(new ConfirmLinkScreen(bl -> {
                        if (bl) {
                            Util.getPlatform().openUri(item.useActions.url);
                        }
                    }, item.useActions.url, true));
            }
        }

        return super.use(world, user, hand);
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

}
