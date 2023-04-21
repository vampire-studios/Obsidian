package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import net.minecraft.world.item.CrossbowItem;

/**
 * This is the default implementation for FabricCrossbow, allowing for the easy creation of new bows with no new modded functionality.
 */
public class CrossbowItemImpl extends CrossbowItem {
	public RangedWeaponItem rangedWeaponItem;

	public CrossbowItemImpl(RangedWeaponItem rangedWeaponItem, Properties settings) {
		super(settings);
		this.rangedWeaponItem = rangedWeaponItem;
	}

	@Override
	public boolean canBeDepleted() {
		return rangedWeaponItem.damageable;
	}
}