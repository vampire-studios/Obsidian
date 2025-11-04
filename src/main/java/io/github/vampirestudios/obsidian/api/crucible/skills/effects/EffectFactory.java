package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EffectFactory {

    public static Effect createEffect(String effectLine) {
        EffectType effectType = parseEffectType(effectLine);
        Map<String, Object> config = parseEffectConfig(effectLine);

        return createEffect(effectType, config);
    }

    public static Effect createEffect(EffectType type, Map<String, Object> config) {
        return switch (type) {
            case PARTICLE -> createParticleEffect(config);
            case SOUND -> createSoundEffect(config);
            default -> throw new IllegalArgumentException("Unsupported effect type: " + type);
        };
    }

    private static ParticleEffect createParticleEffect(Map<String, Object> config) {
        String particleType = (String) config.get("particleType");
        String pattern = (String) config.get("pattern");
        int count = ((Number) config.getOrDefault("count", 10)).intValue();
        double speed = ((Number) config.getOrDefault("speed", 0.1)).doubleValue();
        Vec3 offset = parseVec3((List<Double>) config.get("offset"));
        int interval = ((Number) config.getOrDefault("interval", 2)).intValue();
        int duration = ((Number) config.getOrDefault("duration", 40)).intValue();
        Optional<Integer> color = config.containsKey("color") ? Optional.of(ParticleEffect.parseColor(config.get("color"))) : Optional.empty();
        List<Map<String, Object>> keyframesConfig = (List<Map<String, Object>>) config.get("keyframes");
        List<Keyframe> keyframes = config.containsKey("keyframes") ? ParticleEffect.parseKeyframes(keyframesConfig) : List.of();

        return new ParticleEffect(particleType, pattern, count, speed, offset, interval, duration, color, keyframes);
    }

    private static SoundEffect createSoundEffect(Map<String, Object> config) {
        String sound = (String) config.get("sound");
        float volume = ((Number) config.getOrDefault("volume", 1.0)).floatValue();
        float pitch = ((Number) config.getOrDefault("pitch", 1.0)).floatValue();

        return new SoundEffect(sound, volume, pitch);
    }

    private static Vec3 parseVec3(List<Double> values) {
        return new Vec3(values.get(0), values.get(1), values.get(2));
    }

    private static EffectType parseEffectType(String effectLine) {
        if (effectLine.startsWith("particles")) {
            return EffectType.PARTICLE;
        } else if (effectLine.startsWith("sound")) {
            return EffectType.SOUND;
        }
        throw new IllegalArgumentException("Unknown effect type in effect line: " + effectLine);
    }

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("([a-zA-Z]+)=(\\[[^\\]]*\\]|[^;]+)");

    public static Map<String, Object> parseEffectConfig(String effectLine) {
        Map<String, Object> config = new HashMap<>();

        // Extract the content within braces {}
        int startIndex = effectLine.indexOf("{");
        int endIndex = effectLine.lastIndexOf("}");

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            throw new IllegalArgumentException("Invalid effect line format: " + effectLine);
        }

        String paramsString = effectLine.substring(startIndex + 1, endIndex);

        Matcher matcher = PARAMETER_PATTERN.matcher(paramsString);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            if (value.startsWith("[")) {
                // Parse list as Vec3 or list of numbers
                List<Double> parsedList = parseList(value);
                config.put(key, parsedList);
            } else {
                // Add as a double if numeric, otherwise as a string
                config.put(key, tryParseNumber(value));
            }
        }

        return config;
    }

    private static List<Double> parseList(String listString) {
        listString = listString.substring(1, listString.length() - 1); // Remove square brackets
        String[] parts = listString.split(",");

        return List.of(Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()), Double.parseDouble(parts[2].trim()));
    }

    private static Object tryParseNumber(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value; // Return as string if not a number
        }
    }
}
