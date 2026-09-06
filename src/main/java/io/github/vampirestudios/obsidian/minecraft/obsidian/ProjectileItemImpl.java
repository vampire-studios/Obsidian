package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.projectile.Projectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The item half of an {@code item/projectile}: right-clicking throws one.
 *
 * <p>Everything else about the item — name, settings, model, tooltip, its other events — comes from
 * {@link ItemImpl}, which this extends, so a projectile is a normal item that happens to throw itself.
 */
public class ProjectileItemImpl extends ItemImpl {

	private final Projectile projectile;

	public ProjectileItemImpl(Projectile projectile, Properties settings) {
		super(projectile, settings);
		this.projectile = projectile;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);

		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!level.isClientSide()) {
			ProjectileEntityImpl thrown = new ProjectileEntityImpl(Obsidian.PROJECTILE, player, level, held.copyWithCount(1));
			thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F,
					projectile.flight.speed, projectile.flight.inaccuracy);
			level.addFreshEntity(thrown);

			EventActionHandler.handleOnShoot(player, projectile);
		}

		if (projectile.cooldown > 0) {
			player.getCooldowns().addCooldown(held, projectile.cooldown);
		}

		// A consume_count of zero is a reusable thrower — a wand, not a stack of grenades.
		if (projectile.consumeCount > 0 && !player.getAbilities().instabuild) {
			held.shrink(projectile.consumeCount);
		}

		return InteractionResult.SUCCESS;
	}
}
