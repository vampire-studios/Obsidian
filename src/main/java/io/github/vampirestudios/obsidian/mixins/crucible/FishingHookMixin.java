package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    /**
     * Mojmaps field name in many 1.20-1.21 lines: "hookedIn".
     * If your mappings use a different name, rename this shadow to match.
     */
    @Shadow private Entity hookedIn;

    @Unique private boolean crucible$spawnedLootThisRetrieve = false;
    @Unique private boolean crucible$wasOnGround = false;

    // -----------------------------
    // REEL + CATCH ENTITY
    // -----------------------------
    @Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"))
    private void crucible$fishReel(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        FishingHook hook = (FishingHook) (Object) this;
        if (!(hook.level() instanceof ServerLevel sl)) return;

        Entity owner = getPlayerOwner();
        if (!(owner instanceof LivingEntity caster)) return;

        crucible$spawnedLootThisRetrieve = false;

        LivingEntity grabbed = (hookedIn instanceof LivingEntity le) ? le : null;

        // Always REEL on retrieve start
        CrucibleEvents.fire(
                SkillTrigger.FISH_REEL,
                SkillContext.builder(caster)
                        .level(sl)
                        .stack(rod)
                        .projectile(hook)
                        .target(grabbed)
                        .build()
        );

        // If hooked entity, also fire CATCH_ENTITY immediately
        if (grabbed != null) {
            CrucibleEvents.fire(
                    SkillTrigger.FISH_CATCH_ENTITY,
                    SkillContext.builder(caster)
                            .level(sl)
                            .stack(rod)
                            .projectile(hook)
                            .target(grabbed)
                            .build()
            );
        }
    }

    // -----------------------------
    // CATCH FISH (loot spawn)
    // Redirect addFreshEntity inside retrieve; if it spawns ItemEntity => loot.
    // -----------------------------
    @Redirect(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean crucible$detectLootSpawn(Level level, Entity entity) {
        boolean result = level.addFreshEntity(entity);

        if (!level.isClientSide() && entity instanceof ItemEntity) {
            crucible$spawnedLootThisRetrieve = true;

            FishingHook hook = (FishingHook) (Object) this;
            if (hook.level() instanceof ServerLevel sl) {
                Entity owner = getPlayerOwner();
                if (owner instanceof LivingEntity caster) {
                    CrucibleEvents.fire(
                            SkillTrigger.FISH_CATCH_FISH,
                            SkillContext.builder(caster)
                                    .level(sl)
                                    .projectile(hook)
                                    .build()
                    );
                }
            }
        }

        return result;
    }

    // -----------------------------
    // FAIL detection (after retrieve)
    // -----------------------------
    @Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"))
    private void crucible$fishFail(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        FishingHook hook = (FishingHook) (Object) this;
        if (!(hook.level() instanceof ServerLevel sl)) return;

        Entity owner = getPlayerOwner();
        if (!(owner instanceof LivingEntity caster)) return;

        boolean hasEntity = hookedIn != null;
        boolean spawnedLoot = crucible$spawnedLootThisRetrieve;
        int ret = cir.getReturnValue();

        // If nothing happened (no entity, no loot, return value 0) => FAIL
        if (!hasEntity && !spawnedLoot && ret == 0) {
            CrucibleEvents.fire(
                    SkillTrigger.FISH_FAIL,
                    SkillContext.builder(caster)
                            .level(sl)
                            .stack(rod)
                            .projectile(hook)
                            .build()
            );
        }
    }

    // ------------------------------------------------------------
    // BITE (your code uses: this.playSound(FISHING_BOBBER_SPLASH,...))
    // So redirect that invocation inside catchingFish(...)
    // ------------------------------------------------------------
    @Redirect(
            method = "catchingFish(Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
            )
    )
    private void crucible$detectBiteSound(FishingHook self, SoundEvent sound, float volume, float pitch) {
        // keep vanilla behavior
        self.playSound(sound, volume, pitch);

        if (!(self.level() instanceof ServerLevel sl)) return;
        if (sound != SoundEvents.FISHING_BOBBER_SPLASH) return;

        Entity owner = getPlayerOwner();
        if (!(owner instanceof LivingEntity caster)) return;

        CrucibleEvents.fire(
                SkillTrigger.FISH_BITE,
                SkillContext.builder(caster)
                        .level(sl)
                        .projectile(self)
                        .build()
        );
    }

    // -----------------------------
    // GROUND detection (edge detect onGround)
    // -----------------------------
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void crucible$fishGround(CallbackInfo ci) {
        FishingHook hook = (FishingHook) (Object) this;
        if (!(hook.level() instanceof ServerLevel sl)) return;

        boolean onGround = hook.onGround();

        if (!crucible$wasOnGround && onGround) {
            Entity owner = getPlayerOwner();
            if (owner instanceof LivingEntity caster) {
                CrucibleEvents.fire(
                        SkillTrigger.FISH_GROUND,
                        SkillContext.builder(caster)
                                .level(sl)
                                .projectile(hook)
                                .build()
                );
            }
        }

        crucible$wasOnGround = onGround;
    }
}
