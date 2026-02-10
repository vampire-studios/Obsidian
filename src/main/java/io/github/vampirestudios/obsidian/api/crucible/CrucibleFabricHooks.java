package io.github.vampirestudios.obsidian.api.crucible;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

import java.util.IdentityHashMap;
import java.util.Map;

public final class CrucibleFabricHooks {
    private static boolean installed = false;

    // simple per-caster timer accumulator (ticks)
    private static final Map<LivingEntity, Integer> TIMER_ACC = new IdentityHashMap<>();

    // default: fire TIMER every 20 ticks (1s). You can later make this configurable.
    private static final int TIMER_PERIOD_TICKS = 20;


    public static void install() {
        if (installed) return;
        installed = true;

        // TICK (server): fire once per player per tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
                CrucibleEvents.fire(SkillTrigger.TICK,
                        SkillContext.builder(sp)
                                .level(sp.level())
                                .build()
                );
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            CrucibleEvents.fire(SkillTrigger.USE,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .build()
            );
            return InteractionResult.PASS;
        });

        // RIGHTCLICK/INTERACT block (right click block)
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            CrucibleEvents.fire(SkillTrigger.BLOCK_USE,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .position(hitResult.getBlockPos())
                            .build()
            );

            CrucibleEvents.fire(SkillTrigger.RIGHTCLICK,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .position(hitResult.getBlockPos())
                            .build()
            );
            return InteractionResult.PASS;
        });

        // ENTITY_INTERACT
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            CrucibleEvents.fire(SkillTrigger.ENTITY_INTERACT,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .target(entity instanceof LivingEntity le ? le : null)
                            .build()
            );

            CrucibleEvents.fire(SkillTrigger.INTERACT,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .target(entity instanceof LivingEntity le ? le : null)
                            .build()
            );

            return InteractionResult.PASS;
        });

        // ATTACK (left click entity)
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            CrucibleEvents.fire(SkillTrigger.ATTACK,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .target(entity instanceof LivingEntity le ? le : null)
                            .build()
            );
            CrucibleEvents.fire(SkillTrigger.SWING,
                    SkillContext.builder(sp)
                            .level(sp.level())
                            .hand(hand)
                            .stack(sp.getItemInHand(hand))
                            .target(entity instanceof LivingEntity le ? le : null)
                            .build()
            );

            return InteractionResult.PASS;
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer sp = handler.getPlayer();
            CrucibleEvents.fire(
                    SkillTrigger.JOIN,
                    SkillContext.builder(sp).level(sp.level()).build()
            );
            CrucibleEvents.fire(
                    SkillTrigger.READY,
                    SkillContext.builder(sp).level(sp.level()).build()
            );
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> CrucibleEvents.fire(
                SkillTrigger.RESPAWN,
				SkillContext.builder(newPlayer).level(newPlayer.level()).build()
		));


        ServerLivingEntityEvents.AFTER_DEATH.register((victim, source) -> {
            LivingEntity killer = source.getEntity() instanceof LivingEntity le ? le : null;

            CrucibleEvents.fire(
                    SkillTrigger.DEATH,
                    SkillContext.builder(victim)
                            .level((ServerLevel) victim.level())
                            .target(killer)
                            .build()
            );

            if (killer instanceof ServerPlayer sp) {
                CrucibleEvents.fire(
                        SkillTrigger.KILL,
                        SkillContext.builder(sp)
                                .level(sp.level())
                                .target(victim)
                                .build()
                );

                if (victim instanceof ServerPlayer sp1) {
                    CrucibleEvents.fire(SkillTrigger.KILLPLAYER,
                            SkillContext.builder(sp).level(sp.level()).target(sp1).build()
                    );
                }
            }
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> CrucibleEvents.fire(
                SkillTrigger.CHANGE_WORLD,
				SkillContext.builder(player).level(destination).build()
		));

        ServerLivingEntityEvents.AFTER_DAMAGE.register((victim, source, base, taken, blocked) -> {
            LivingEntity attacker = source.getEntity() instanceof LivingEntity le ? le : null;

            CrucibleEvents.fire(
                    SkillTrigger.DAMAGED,
                    SkillContext.builder(victim)
                            .level((ServerLevel) victim.level())
                            .target(attacker)
                            .build()
            );
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
                int next = TIMER_ACC.getOrDefault(sp, 0) + 1;
                if (next >= TIMER_PERIOD_TICKS) {
                    next = 0;
                    CrucibleEvents.fire(
                            SkillTrigger.TIMER,
                            SkillContext.builder(sp).level(sp.level()).build()
                    );
                }
                TIMER_ACC.put(sp, next);
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof LivingEntity le)) return;

            CrucibleEvents.fire(
                    SkillTrigger.SPAWN_OR_LOAD,
                    SkillContext.builder(le)
                            .level(world)
                            .build()
            );
        });
    }
}
