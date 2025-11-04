package io.github.vampirestudios.obsidian.minecraft;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.OMenus;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CustomMenuItem extends ItemImpl {
    public CustomMenuItem(Item item, Properties settings) {
        super(item, settings);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (item.useActions != null && item.useActions.right_click_actions != null && !item.useActions.right_click_actions.isEmpty()) {
            switch (item.useActions.right_click_actions) {
                case "open_gui":
                    player.openMenu(item.useActions.openGui(ContainerLevelAccess.create(level, player.blockPosition())));
                    break;
                case "run_command":
                    //TODO
                    break;
                case "open_url":
                    if (level.isClientSide())
                        Minecraft.getInstance().setScreen(new ConfirmLinkScreen(bl -> {
                            if (bl) {
                                Util.getPlatform().openUri(item.useActions.url);
                            }
                        }, item.useActions.url, true));
                    break;
            }
        }
        EventActionHandler.handleOnUse(player, item);
        if (!level.isClientSide) {
            MenuType<ChestMenu> chestMenuMenuType = switch (item.menuConfig.rows) {
                case 1:
                    yield MenuType.GENERIC_9x1;
                case 2:
                    yield MenuType.GENERIC_9x2;
                case 3:
                    yield MenuType.GENERIC_9x3;
                case 4:
                    yield MenuType.GENERIC_9x4;
                case 5:
                    yield MenuType.GENERIC_9x5;
                case 6:
                    yield MenuType.GENERIC_9x6;
                case 7:
                    yield OMenus.GENERIC_9x7;
                case 8:
                    yield OMenus.GENERIC_9x8;
                case 9:
                    yield OMenus.GENERIC_9x9;
                default:
                    throw new IllegalStateException(STR."Unexpected value: \{item.menuConfig.rows}");
			};
            player.openMenu(new SimpleMenuProvider(
                    (syncId, inv, _) -> chestMenuMenuType.create(syncId, inv),
                    TagParser.QUICK_TEXT_WITH_STF.parseNode(item.menuConfig.title).toText()
            ));
        }
        return InteractionResult.SUCCESS;
    }
}