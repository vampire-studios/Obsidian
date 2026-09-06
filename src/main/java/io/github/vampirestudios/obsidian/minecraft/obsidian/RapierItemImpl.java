package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

/**
 * Fast, low-damage sword with armour penetration.
 * On hit: deals bonus armour-bypassing damage equal to (target's armour value × armor_pierce).
 */
public class RapierItemImpl extends ItemImpl {

	public RapierItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
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
		if (weapon.armor_pierce > 0) {
			float armor = (float) target.getAttributeValue(Attributes.ARMOR);
			float pierceDamage = armor * weapon.armor_pierce;
			if (pierceDamage > 0) {
				target.hurt(player.damageSources().playerAttack(player), pierceDamage);
			}
		}
	}
}
