package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.world.item.ToolMaterial;

/**
 * A plain sword. It adds nothing to {@link ItemImpl} but the sword attack profile — the daggers, cleavers
 * and rapiers that do add something all build on the same base.
 */
public class MeleeWeaponImpl extends ItemImpl {

	public MeleeWeaponImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, settings.sword(toolMaterial, attackDamage, attackSpeed)));
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}

}
