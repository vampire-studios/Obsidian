package io.github.vampirestudios.obsidian.api.nexo;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SetBonusManager {
    private static final SetBonusManager INSTANCE = new SetBonusManager();

    public static SetBonusManager getInstance() {
        return INSTANCE;
    }

    public void tick(Level world) {
        if (world.isClientSide) {
            return;
        }

        for (Player player : world.players()) {
            checkPlayerSetBonuses(player);
        }
    }

    private void checkPlayerSetBonuses(Player player) {
        // Iterate over all OraxenItems that have set bonuses
        for (NexoItem nexoItem : ContentRegistries.NEXO_ITEMS) {
            if (nexoItem.mechanics != null && nexoItem.mechanics.set_bonus != null) {
                nexoItem.mechanics.set_bonus.checkAndApplyBonus(player);
            }
        }
    }
}
