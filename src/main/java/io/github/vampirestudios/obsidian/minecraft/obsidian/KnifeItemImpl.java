package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

/**
 * Dagger that can optionally be thrown (throwable: true).
 * Right-click throws the knife, consuming one from the stack.
 * Also applies backstab bonus when striking from behind.
 */
public class KnifeItemImpl extends ItemImpl {

	public KnifeItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, settings.sword(toolMaterial, attackDamage, attackSpeed)));
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		WeaponItem weapon = weapon();
		if (!weapon.throwable) return super.use(world, user, hand);

		ItemStack stack = user.getItemInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
				0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
		if (!world.isClientSide()) {
			ThrownKnifeEntity knife = new ThrownKnifeEntity(Obsidian.THROWN_KNIFE, world);
			knife.setOwner(user);
			knife.setItem(stack);
			knife.setDamage(weapon.attackDamage * 0.5f);
			knife.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, 1.5f, 1.0f);
			world.addFreshEntity(knife);
			if (!user.getAbilities().instabuild) stack.shrink(1);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		if (!(attacker instanceof Player player)) return;

		WeaponItem weapon = weapon();
		if (weapon.backstab_multiplier > 1.0f && DaggerItemImpl.isBackstab(attacker, target)) {
			float bonusDamage = weapon.attackDamage * (weapon.backstab_multiplier - 1.0f);
			target.hurt(player.damageSources().playerAttack(player), bonusDamage);
		}
	}
}
