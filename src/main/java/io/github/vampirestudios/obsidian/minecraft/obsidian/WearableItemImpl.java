package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WearableItemImpl extends ItemImpl implements Equipable {
	public WearableItemImpl(Item item, Properties settings) {
		super(item, settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		return this.swapWithEquipmentSlot(this, world, user, hand);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.byName(item.information.getItemSettings().wearableSlot);
	}
}
