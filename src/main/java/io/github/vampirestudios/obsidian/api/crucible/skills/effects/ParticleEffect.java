package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParticleEffect extends Effect {
    private final ParticleOptions particleOptions;
    private final String pattern;
    private final int count;
    private final double speed;
    private final Vec3 offset;
    private final int interval;
    private final int duration;
    private final List<Keyframe> keyframes;

    public ParticleEffect(String particleTypeName, String pattern, int count, double speed, Vec3 offset, int interval, int duration, Optional<Integer> color, List<Keyframe> keyframes) {
        ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(ResourceLocation.parse(particleTypeName));
        if (particleType == null) {
            throw new IllegalArgumentException("Unknown particle type: " + particleTypeName);
        }

        this.particleOptions = createParticleOptions(particleType);
        this.pattern = pattern;
        this.count = count;
        this.speed = speed;
        this.offset = offset;
        this.interval = interval;
        this.duration = duration;
        this.keyframes = keyframes;
    }

    private ParticleOptions createParticleOptions(ParticleType<?> particleType) {
        return (ParticleOptions) particleType;
    }

    @Override
    public void apply(LivingEntity target) {
        for (int i = 0; i < count; i++) {
            Vec3 particleOffset = generatePatternOffset(i);
            if (!keyframes.isEmpty()) {
                AnimatedParticleEffect animatedEffect = new AnimatedParticleEffect(
                        target,
                        particleOptions,
                        offset.add(particleOffset),
                        interval,
                        duration,
                        keyframes
                );
                AnimationManager.getInstance().addAnimation(animatedEffect);
            }
        }
    }

    private Vec3 generatePatternOffset(int index) {
        double radius = 1.0; // Default radius for patterns
        double height = 1.0; // Default height for vertical patterns

        return switch (pattern) {
            case "circle" -> {
                double angle = 2 * Math.PI * index / count;
                yield new Vec3(radius * Math.cos(angle), 0, radius * Math.sin(angle));
            }
            case "sphere" -> {
                double theta = 2 * Math.PI * Math.random();
                double phi = Math.acos(2 * Math.random() - 1);
                yield new Vec3(
                        radius * Math.sin(phi) * Math.cos(theta),
                        radius * Math.sin(phi) * Math.sin(theta),
                        radius * Math.cos(phi)
                );
            }
            case "spiral" -> {
                double spiralAngle = 0.1 * index;
                double spiralHeight = height * index / count;
                yield new Vec3(radius * Math.cos(spiralAngle), spiralHeight, radius * Math.sin(spiralAngle));
            }
            default -> Vec3.ZERO;
        };
    }

    // Parsing keyframes using parseKeyframes (from YAML or other sources)
    public static List<Keyframe> parseKeyframes(List<Map<String, Object>> keyframeConfigs) {
        List<Keyframe> keyframes = new ArrayList<>();

        for (Map<String, Object> config : keyframeConfigs) {
            int time = ((Number) config.get("time")).intValue();
            Optional<Double> radius = config.containsKey("radius") ? Optional.of(((Number) config.get("radius")).doubleValue()) : Optional.empty();
            Optional<Integer> color = config.containsKey("color") ? Optional.of(parseColor(config.get("color"))) : Optional.empty();
            Optional<Double> expandRadius = config.containsKey("expandRadius") ? Optional.of(((Number) config.get("expandRadius")).doubleValue()) : Optional.empty();
            Optional<Boolean> fadeOut = config.containsKey("fadeOut") ? Optional.of((Boolean) config.get("fadeOut")) : Optional.empty();

            keyframes.add(new Keyframe(time, radius, color, expandRadius, fadeOut));
        }

        return keyframes;
    }

    public static int parseColor(Object colorValue) {
        return switch (colorValue) {
            case List color -> {
                if (color.size() == 4) {
                    int r = ((Number) color.get(0)).intValue();
                    int g = ((Number) color.get(1)).intValue();
                    int b = ((Number) color.get(2)).intValue();
                    int a = ((Number) color.get(3)).intValue();
                    yield ARGB.color(a, r, g, b);
                } else if (color.size() == 3) {
                    int r = ((Number) color.get(0)).intValue();
                    int g = ((Number) color.get(1)).intValue();
                    int b = ((Number) color.get(2)).intValue();
                    yield ARGB.color(r, g, b);
                } else {
                    throw new IllegalArgumentException("Color list must have 3 or 4 values: " + colorValue);
                }
            }
            case String s -> {
                if (s.startsWith("#") && (s.length() == 7 || s.length() == 9)) {
                    String col = s.substring(1); // Remove #
                    if (col.length() == 6) {
                        col = "FF" + col; // Add alpha if missing
                    }
                    try {
                        yield Integer.parseUnsignedInt(col, 16); // Parse ARGB hex
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid hex color string: " + s, e);
                    }
                } else {
                    throw new IllegalArgumentException("String must be formatted as \"#rrggbb\" or \"#aarrggbb\": " + colorValue);
                }
            }
            case Number number -> number.intValue();
            case null, default -> throw new IllegalArgumentException(colorValue + " is not a supported color value. Only supported values are: [r,g,b,a], [r,g,b], \"#rrggbb\", \"#aarrggbb\" or int");
        };
    }
}

