package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.function.Predicate;

/**
 * What a ranged weapon will fire.
 *
 * <p>A weapon naming {@code ammo} fires only what it names — vanilla arrows will not load into it, and
 * its ammunition will not load into a vanilla bow, since a custom arrow item is not in
 * {@code minecraft:arrows}. Weapon and ammunition are designed together, which is what separates a
 * crossbow's bolts from a bow's arrows.
 */
public final class AmmoPredicate {

	private AmmoPredicate() {
	}

	public static Predicate<ItemStack> of(RangedWeaponItem weapon, Predicate<ItemStack> vanilla) {
		if (!weapon.hasCustomAmmo()) return vanilla;

		return stack -> {
			Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
			return id != null && weapon.ammo.contains(id);
		};
	}

	/** The vanilla default for a bow: anything tagged as an arrow. */
	public static Predicate<ItemStack> arrows() {
		return ProjectileWeaponItem.ARROW_ONLY;
	}

	/** The vanilla default for a crossbow: arrows and fireworks. */
	public static Predicate<ItemStack> arrowsOrFireworks() {
		return ProjectileWeaponItem.ARROW_OR_FIREWORK;
	}
}
