package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CustomArmorItem extends ItemImpl {
    public CustomArmorItem(NexoItem item, Item.Properties settings) {
        super(item, settings);
    }

    @Override
    public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int slot) {
        if (!world.isClientSide() && entity instanceof Player player) {
            if (item.mechanics != null && item.mechanics.set_bonus != null) {
                item.mechanics.set_bonus.checkAndApplyBonus(player);
            }
        }
        super.onUseTick(world, entity, stack, slot);
    }
}
