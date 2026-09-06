/*
package io.github.vampirestudios.obsidian.api.oraxen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CognitiveEnhancement {
    private final List<Effect> effects;
    private final RandomEffectSelector effectSelector;

    public CognitiveEnhancement() {
        effects = new ArrayList<>();
        effectSelector = new RandomEffectSelector(effects);
        initializeEffects();
    }

    private void initializeEffects() {
        effects.add(new Effect("increase_game_tick_speed",
            (effectArgs) -> increaseGameTickSpeed(effectArgs.entity(), 1.0f),
            (effectArgs) -> increaseGameTickSpeed(effectArgs.entity(), 0.08F),
            30
        ));

        effects.add(new Effect("freeze_game_tick_speed",
            (effectArgs) -> freezeGameTickSpeed(effectArgs.entity(), true),
            (effectArgs) -> freezeGameTickSpeed(effectArgs.entity(), false),
            30));

        // Add other effects similarly...
    }

    public void applyEffectByName(EffectArgs effectArgs, String effectName) {
        for (Effect effect : effects) {
            if (effect.effectName.equals(effectName)) {
                effect.applyEffect(effectArgs);
                return;
            }
        }
        System.out.println("Effect not found: " + effectName);
    }

    public void applyRandomEffect(LivingEntity entity) {
//        effectSelector.applyRandomEffect(entity);
    }

    private @NotNull Runnable getRunnable(LivingEntity entity) {
        */
/*RandomEffectSelector effectSelector = new RandomEffectSelector(List.of(
                () -> increaseGameTickSpeed(entity, newTickSpeed),
                () -> freezeGameTickSpeed(entity, true),
                () -> applyTemporalDistortion(entity),
                () -> applyEnhancedReflexes(entity),
                () -> applyCognitiveOverload(entity),
                () -> applyInvisibilityFlash(entity, 10),
                () -> changePlayerGravity(entity, 30, 0.04),  // Reduced gravity
                () -> changePlayerGravity(entity, 30, 0.12),  // Increased gravity
                () -> applyHealingRain(entity, 5),
                () -> applyMysteryTeleport(entity),
                () -> applyClone(entity, 30)
        ));
        return () -> {
            System.out.println("Cognition enhancement effect applied.");
            switch (effect) {
                case "random":
                    effectSelector.applyRandomEffect();
                    break;
                case "increase_game_tick_speed":
                    increaseGameTickSpeed(entity, newTickSpeed);
                    break;
                case "freeze_game_tick_speed":
                    freezeGameTickSpeed(entity, true);
                    break;
                case "temporal_distortion":
                    applyTemporalDistortion(entity);
                    break;
                case "enhanced_reflexes":
                    applyEnhancedReflexes(entity);
                    break;
                case "cognitive_overload":
                    applyCognitiveOverload(entity);
                    break;
                case "invisibility_flash":
                    applyInvisibilityFlash(entity, duration);
                    break;
                case "change_gravity":
                    changePlayerGravity(entity, duration, gravity);
                    break;
                case "healing_rain":
                    applyHealingRain(entity, duration);
                    break;
                case "mystery_teleport":
                    applyMysteryTeleport(entity);
                    break;
                case "apply_clone":
                    applyClone(entity, duration);
                    break;
            }
        };*//*

        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }

    private void increaseGameTickSpeed(Entity entity, float newTickSpeed) {
        entity.level().tickRateManager().setTickRate(newTickSpeed);
    }

    private void freezeGameTickSpeed(Entity entity, boolean freeze) {
        entity.level().tickRateManager().setFrozen(freeze);
    }

    private void applyTemporalDistortion(EffectArgs effectArgs) {
//        if (newTickSpeed > 1.0f)
//            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, effectArgs.duration() * 20, 1));
//        else
//            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, effectArgs.duration() * 20, 1));
    }

    private void applyEnhancedReflexes(LivingEntity entity) {
//        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, effectArgs.duration() * 20, 1));
//        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectArgs.duration() * 20, 1));
    }

    private void applyCognitiveOverload(LivingEntity entity) {
//        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, effectArgs.duration() * 10, 2)); // Boost for half the duration
//        scheduler.schedule(() -> entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectArgs.duration() * 20, 1)), effectArgs.duration() * 10, TimeUnit.SECONDS); // Weakness after boost
    }

    public static void applyInvisibilityFlash(LivingEntity player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration * 20, 0));
    }

    private static final UUID GRAVITY_MODIFIER_ID = UUID.fromString("c8b26db0-eeca-4d94-9b60-5ba9d7be0c6b");

    */
/**
 * Changes the gravity for a living entity.
 *
 * @param entity The entity to modify.
 * @param duration Duration in seconds for how long the gravity change should last.
 * @param gravityFactor A double value that specifies the new gravity value (range from -1.0 to 1.0).
 *//*

    public static void changePlayerGravity(LivingEntity entity, int duration, double gravityFactor) {
        // Ensure gravityFactor is within the valid range
        double validatedGravityFactor = Math.max(-1.0, Math.min(1.0, gravityFactor));

        // Calculate the change needed from the default gravity
        double defaultGravity = 0.08;  // Default gravity value for Minecraft
        double modifierValue = validatedGravityFactor - defaultGravity;

        AttributeModifier gravityModifier = new AttributeModifier(GRAVITY_MODIFIER_ID, "Custom gravity modifier", modifierValue, AttributeModifier.Operation.ADD_VALUE);

        applyModifierWithTimeout(entity, gravityModifier, duration);
    }

    private static void applyModifierWithTimeout(LivingEntity entity, AttributeModifier modifier, int duration) {
        AttributeInstance attributeInstance = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY);
        if (attributeInstance == null) {
            return; // This entity does not support the specified attribute
        }

        // Remove old modifier if it exists to prevent stacking
        if (attributeInstance.getModifier(modifier.id()) != null) {
            attributeInstance.removeModifier(modifier);
        }

        // Apply the new modifier
        attributeInstance.addPermanentModifier(modifier);

        System.out.println("Custom gravity (" + modifier.amount() + ") applied to " + entity.getName().getString());

        if (duration != -1) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> {
                attributeInstance.removeModifier(modifier.id());
                System.out.println("Gravity effect reverted for " + entity.getName().getString());
            }, duration, TimeUnit.SECONDS);
            scheduler.schedule(scheduler::shutdown, duration + 5, TimeUnit.SECONDS);
        }
    }

    public static void applyHealingRain(LivingEntity player, int duration) {
        // Assuming this affects all players in a radius around 'player'
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.execute(() -> player.level().getEntities(player, player.getBoundingBox().inflate(5)).forEach(e ->
                ((LivingEntity) e).heal(5.0F)));
        scheduler.schedule(scheduler::shutdown, duration, TimeUnit.SECONDS);
        System.out.println("Healing rain activated around " + player.getName().getString());
    }

    public static void applyMysteryTeleport(LivingEntity player) {
        Level world = player.level();
        BlockPos randomPos = player.blockPosition().offset(world.random.nextInt(100) - 50, 0, world.random.nextInt(100) - 50);
        player.teleportTo(randomPos.getX(), world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, randomPos.getX(), randomPos.getZ()), randomPos.getZ());
    }

    public static void applyClone(LivingEntity player, int duration) {
        // Clone logic; actual implementation would depend on the modding API
        System.out.println("Creating a clone for " + player.getName().getString());
    }
}*/
