package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Wide-arc harvesting weapon.
 * aoe_radius > 0 hits nearby entities on each melee strike.
 * Right-clicking farmable dirt tills it like a hoe.
 */
public class ScytheItemImpl extends ItemImpl {

	public ScytheItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
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

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);

		if (context.getClickedFace() != Direction.DOWN) {
			BlockState tilled = MattockItemImpl.getTilledStateOf(state);
			if (tilled != null) {
				Player player = context.getPlayer();
				world.playSound(player, pos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
				if (!world.isClientSide()) {
					world.setBlock(pos, tilled, 11);
					if (player != null) context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
				}
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(context);
	}
}
