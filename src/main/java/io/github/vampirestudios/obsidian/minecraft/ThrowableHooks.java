package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ThrowableHooks {
    private ThrowableHooks() {}

    public static void initialize() {
        UseItemCallback.EVENT.register(ThrowableHooks::onUseItem);
    }

    private static InteractionResult onUseItem(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ThrowableComponent cfg = stack.get(OItemComponents.THROWABLE);
        if (cfg == null) return InteractionResult.PASS;

        // cooldown
        if (!world.isClientSide()) {
            player.getCooldowns().addCooldown(stack, cfg.cooldownTicks());

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(cfg.projectileEntity());
            Entity e = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
            if (!(e instanceof Projectile proj)) {
                return InteractionResult.FAIL; // misconfigured entity
            }

            proj.setOwner(player);
            proj.setPos(
                player.getX(),
                player.getEyeY() - 0.1,
                player.getZ()
            );

            // If it's a thrown-item projectile, set the item so it renders like the stack
            if (proj instanceof ThrowableItemProjectile tie) {
                tie.setItem(stack.copyWithCount(1));
            }

            proj.shootFromRotation(player, player.getYRot(), player.getYRot(), 0.0f, cfg.power(), cfg.inaccuracy());
            world.addFreshEntity(proj);

            // sound
            SoundEvent se = BuiltInRegistries.SOUND_EVENT.getValue(cfg.throwSound());
            world.playSound(null, player.blockPosition(), se, SoundSource.PLAYERS, 0.5f, 0.9f + world.getRandom().nextFloat() * 0.2f);

            // consume
            if (!player.isCreative() && cfg.consume()) {
                stack.consume(1, player);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
