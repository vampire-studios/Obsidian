package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ShooterHooks {
    private ShooterHooks() {}

    public static void initialize() {
        UseItemCallback.EVENT.register(ShooterHooks::onUseItem);
    }

    private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.has(OItemComponents.SHOOTER)) {
            return InteractionResult.PASS;
        }

        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

}
