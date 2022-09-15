package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import net.minecraft.item.BowItem;

public class BowItemImpl extends BowItem {
	public RangedWeaponItem rangedWeaponItem;

	public BowItemImpl(RangedWeaponItem rangedWeaponItem, Settings settings) {
		super(settings);
		this.rangedWeaponItem = rangedWeaponItem;
	}

	@Override
	public boolean isDamageable() {
		return rangedWeaponItem.damageable;
	}
}