package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.phys.Vec3;

/**
 * Fast, low-damage blade that deals bonus damage when striking from behind.
 * backstab_multiplier is applied as extra damage: (base - 1) × multiplier extra hits.
 */
public class DaggerItemImpl extends ItemImpl {

	public DaggerItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, settings.sword(toolMaterial, attackDamage, attackSpeed)));
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}

	/** Returns true when attacker is behind the target (target facing away). */
	static boolean isBackstab(LivingEntity attacker, LivingEntity target) {
		Vec3 targetLook = target.getLookAngle();
		Vec3 toAttacker = attacker.position().subtract(target.position());
		return targetLook.dot(toAttacker) < 0;
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		if (!(attacker instanceof Player player)) return;

		WeaponItem weapon = weapon();
		if (weapon.backstab_multiplier > 1.0f && isBackstab(attacker, target)) {
			float bonusDamage = weapon.attackDamage * (weapon.backstab_multiplier - 1.0f);
			target.hurt(player.damageSources().playerAttack(player), bonusDamage);
		}
	}
}
