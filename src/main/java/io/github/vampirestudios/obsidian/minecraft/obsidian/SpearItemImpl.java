package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.world.item.ToolMaterial;

public class SpearItemImpl extends ItemImpl {

	public SpearItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, buildProps(item, toolMaterial, settings)));
	}

	private static Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, Properties settings) {
		return settings.spear(
				toolMaterial,
				item.spear.charge_time,         // attackDuration
				item.spear.damage_multiplier,   // damageMultiplier
				item.spear.delay,               // delay
				item.spear.dismount_time,       // dismountTime
				item.spear.dismount_threshold,  // dismountThreshold
				item.spear.knockback_time,      // knockbackTime
				item.spear.knockback_threshold, // knockbackThreshold
				item.spear.damage_time,         // damageTime
				item.spear.damage_threshold     // damageThreshold
		);
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}
}
