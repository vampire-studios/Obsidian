package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

import java.util.List;

/**
 * Heavy, slow blade that cleaves through multiple targets.
 * aoe_radius > 0 causes nearby entities to take aoe_damage_multiplier × attackDamage.
 */
public class CleaverItemImpl extends ItemImpl {

	public CleaverItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, settings.sword(toolMaterial, attackDamage, attackSpeed)));
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		if (!(attacker instanceof Player player)) return;

		WeaponItem weapon = weapon();
		if (weapon.aoe_radius > 0 && !attacker.level().isClientSide()) {
			float aoeDamage = weapon.attackDamage * weapon.aoe_damage_multiplier;
			List<LivingEntity> nearby = attacker.level().getEntitiesOfClass(
					LivingEntity.class,
					target.getBoundingBox().inflate(weapon.aoe_radius),
					e -> e != attacker && e != target);
			for (LivingEntity nearbyEntity : nearby) {
				nearbyEntity.hurt(player.damageSources().playerAttack(player), aoeDamage);
			}
		}
	}
}
