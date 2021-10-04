package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WearableItemImpl extends ItemImpl implements Wearable {

	public WearableItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Settings settings) {
		super(item, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
		ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			user.equipStack(equipmentSlot, itemStack.copy());
			if (!world.isClient()) {
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			itemStack.setCount(0);
			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

}
