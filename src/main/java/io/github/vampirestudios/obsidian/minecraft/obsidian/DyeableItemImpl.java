package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class DyeableItemImpl extends Item implements IRenderModeAware, DyeableItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public DyeableItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : item.information.defaultColor;
    }

    @Override
    public BakedModel getModel(ItemStack stack, ModelTransformation.Mode mode, BakedModel original) {
        if (item.information.customRenderMode) {
            for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                for (String renderMode : renderModeModel.modes) {
                    if (mode.equals(ModelTransformation.Mode.valueOf(renderMode))) {
                        return MinecraftClient.getInstance().getBakedModelManager().getModel(renderModeModel.model);
                    }
                }
            }
        }
        return original;
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableItem.super.setColor(stack, color);
        if (color == item.information.defaultColor) {
            stack.addHideFlag(ItemStack.TooltipSection.DYE);
        } else {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipSection.DYE.getFlag());
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            this.setColor(stack, item.information.defaultColor);
            stacks.add(stack);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return item.useActions.getAction();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (item.useActions != null && item.useActions.rightClickAction.equals("open_gui")) {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) -> {
                return switch (item.useActions.guiSize) {
                    case 1 -> GenericContainerScreenHandler.createGeneric9x1(syncId, playerx.getInventory());
                    case 2 -> GenericContainerScreenHandler.createGeneric9x2(syncId, playerx.getInventory());
                    case 3 -> GenericContainerScreenHandler.createGeneric9x3(syncId, playerx.getInventory());
                    case 4 -> GenericContainerScreenHandler.createGeneric9x4(syncId, playerx.getInventory());
                    case 5 -> GenericContainerScreenHandler.createGeneric9x5(syncId, playerx.getInventory());
                    case 6 -> GenericContainerScreenHandler.createGeneric9x6(syncId, playerx.getInventory());
                    default -> throw new IllegalStateException("Unexpected value: " + item.useActions.guiSize);
                };
            }, new LiteralText(item.useActions.inventoryName)));
        }

        return super.use(world, user, hand);
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
