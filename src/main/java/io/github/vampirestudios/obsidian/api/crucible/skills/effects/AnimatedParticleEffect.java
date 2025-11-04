package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AnimatedParticleEffect {
    private final LivingEntity target;
    private final ParticleOptions particleOptions;
    private final Vec3 offset;
    private final int interval;
    private final int duration;
    private final List<Keyframe> keyframes;
    private int tickCount;
    private int currentKeyframeIndex;

    public AnimatedParticleEffect(LivingEntity target, ParticleOptions particleOptions, Vec3 offset, int interval, int duration, List<Keyframe> keyframes) {
        this.target = target;
        this.particleOptions = particleOptions;
        this.offset = offset;
        this.interval = interval;
        this.duration = duration;
        this.keyframes = keyframes;
        this.tickCount = 0;
        this.currentKeyframeIndex = 0;
    }

    public boolean update() {
        applyCurrentKeyframe();

        if (tickCount % interval == 0) {
            spawnParticle();
        }

        tickCount++;
        if (tickCount >= duration) return true;

        // Advance to the next keyframe if the current time exceeds it
        if (currentKeyframeIndex < keyframes.size() - 1 && tickCount >= keyframes.get(currentKeyframeIndex + 1).time()) {
            currentKeyframeIndex++;
        }

        return false;
    }

    private void applyCurrentKeyframe() {
        Keyframe currentKeyframe = keyframes.get(currentKeyframeIndex);
        Keyframe nextKeyframe = currentKeyframeIndex < keyframes.size() - 1 ? keyframes.get(currentKeyframeIndex + 1) : currentKeyframe;

        double progress = (double) (tickCount - currentKeyframe.time()) / (nextKeyframe.time() - currentKeyframe.time());

        // Interpolate radius
        if (currentKeyframe.radius().isPresent() && nextKeyframe.radius().isPresent()) {
            double interpolatedRadius = interpolate(currentKeyframe.radius().get(), nextKeyframe.radius().get(), progress);
            // Apply this radius to particle spawning pattern (e.g., radius field)
        }

        // Interpolate color
        if (currentKeyframe.color().isPresent() && nextKeyframe.color().isPresent()) {
            int startColor = currentKeyframe.color().get();
            int endColor = nextKeyframe.color().get();
            Vec3 interpolatedColor = new Vec3(
                interpolate(startColor, endColor, progress),
                interpolate(startColor, endColor, progress),
                interpolate(startColor, endColor, progress)
            );
            // Apply this color to the particle options if applicable
        }

        // Apply fadeOut and expandRadius as boolean values without interpolation
    }

    private double interpolate(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    private void spawnParticle() {
        Vec3 position = target.position().add(offset);
        target.level().addParticle(particleOptions, position.x, position.y, position.z, 0, 0, 0);
    }
}
