package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WearableAndDyeableItemImpl extends DyeableItemImpl implements Equipment {

	public WearableAndDyeableItemImpl(Item item, Settings settings) {
		super(item, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.equipAndSwap(this, world, user, hand);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.byName(item.information.wearableSlot);
	}
}
