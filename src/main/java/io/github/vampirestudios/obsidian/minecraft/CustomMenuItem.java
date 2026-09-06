package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

public class CustomMenuItem extends ItemImpl {

	public CustomMenuItem(Item item, Properties settings) {
		super(item, settings);
	}

	@Override
	@NullMarked
	public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
		boolean openedScreen = runRightClickShorthand(level, player);
		EventActionHandler.handleOnUse(player, item);

		// The use_actions shorthand wins if it already opened something, so the two do not fight over the screen.
		if (!openedScreen && item.menuConfig != null) {
			CrateMenus.open(player, item.menuConfig, player.getItemInHand(interactionHand));
		}
		return InteractionResult.SUCCESS;
	}
}
