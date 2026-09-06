package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EffectFactory {

	private static final int TICKS_PER_SECOND = 20;

	public static Effect createEffect(String effectLine) {
		EffectType effectType = parseEffectType(effectLine);
		Map<String, Object> config = parseEffectConfig(effectLine);

		return createEffect(effectType, config);
	}

	public static Effect createEffect(EffectType type, Map<String, Object> config) {
		return switch (type) {
			case PARTICLE -> createParticleEffect(config);
			case SOUND -> createSoundEffect(config);
			case DAMAGE -> new DamageEffect(number(config, "amount", 1.0).floatValue());
			case HEAL -> new HealEffect(number(config, "amount", 1.0).floatValue());
			case BUFF -> new BuffEffect(createPotionEffect(config));
			case DEBUFF -> new DebuffEffect(createPotionEffect(config));
		};
	}

	/**
	 * The status effect a buff or debuff applies. {@code duration} is in seconds, matching the rest of
	 * the skill format, and {@code amplifier} is 0-based as the game counts it.
	 */
	private static MobEffectInstance createPotionEffect(Map<String, Object> config) {
		String effectId = (String) config.get("effect");
		if (effectId == null) throw new IllegalArgumentException("A buff or debuff needs an \"effect\".");

		Identifier id = Identifier.tryParse(effectId.toLowerCase(Locale.ROOT));
		Holder.Reference<MobEffect> effect = id == null ? null
				: BuiltInRegistries.MOB_EFFECT.get(id).orElse(null);
		if (effect == null) throw new IllegalArgumentException("Unknown effect: " + effectId);

		int duration = number(config, "duration", 5.0).intValue() * TICKS_PER_SECOND;
		int amplifier = number(config, "amplifier", 0.0).intValue();
		boolean ambient = bool(config, "ambient", false);
		boolean particles = bool(config, "particles", true);
		boolean icon = bool(config, "icon", true);

		return new MobEffectInstance(effect, duration, amplifier, ambient, particles, icon);
	}

	private static Number number(Map<String, Object> config, String key, double fallback) {
		Object value = config.get(key);
		return value instanceof Number number ? number : fallback;
	}

	private static boolean bool(Map<String, Object> config, String key, boolean fallback) {
		Object value = config.get(key);
		if (value instanceof Boolean flag) return flag;
		if (value instanceof String text) return Boolean.parseBoolean(text);
		return fallback;
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

	/** The name in front of the braces on an effect line: {@code damage{amount=4}}. */
	private static EffectType parseEffectType(String effectLine) {
		int brace = effectLine.indexOf('{');
		String name = (brace == -1 ? effectLine : effectLine.substring(0, brace)).trim();

		return EffectType.fromString(name);
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
