package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class ItemImpl extends Item implements IRenderModeAware {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public BakedModel getModel(ItemStack stack, ModelTransformation.Mode mode, BakedModel original) {
        if (item.information.customRenderMode) {
            for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                for (String renderMode : renderModeModel.modes) {
                    switch (renderMode) {
                        case "HAND" -> {
                            if (mode.equals(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) ||
                                    mode.equals(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND) ||
                                    mode.equals(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND) ||
                                    mode.equals(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND)) {
                                return MinecraftClient.getInstance().getBakedModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        case "FIRST_PERSON_HAND" -> {
                            if (mode.equals(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) ||
                                    mode.equals(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND)) {
                                return MinecraftClient.getInstance().getBakedModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        case "THIRD_PERSON_HAND" -> {
                            if (mode.equals(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND) ||
                                    mode.equals(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND)) {
                                return MinecraftClient.getInstance().getBakedModelManager().getModel(renderModeModel.model);
                            } else {
                                return original;
                            }
                        }
                        default -> {
                            if (mode.equals(ModelTransformation.Mode.valueOf(renderMode))) {
                                return MinecraftClient.getInstance().getBakedModelManager().getModel(renderModeModel.model);
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
    public UseAction getUseAction(ItemStack stack) {
        return item.useActions.getAction();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (item.useActions != null && item.useActions.rightClickAction.equals("open_gui")) {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) -> switch (item.useActions.guiSize) {
                case 1 -> GenericContainerScreenHandler.createGeneric9x1(syncId, playerx.getInventory());
                case 2 -> GenericContainerScreenHandler.createGeneric9x2(syncId, playerx.getInventory());
                case 3 -> GenericContainerScreenHandler.createGeneric9x3(syncId, playerx.getInventory());
                case 4 -> GenericContainerScreenHandler.createGeneric9x4(syncId, playerx.getInventory());
                case 5 -> GenericContainerScreenHandler.createGeneric9x5(syncId, playerx.getInventory());
                case 6 -> GenericContainerScreenHandler.createGeneric9x6(syncId, playerx.getInventory());
                default -> throw new IllegalStateException("Unexpected value: " + item.useActions.guiSize);
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
