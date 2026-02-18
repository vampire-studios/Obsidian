package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.conditions.ConditionFactory;
import io.github.vampirestudios.obsidian.api.crucible.skills.*;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.EffectFactory;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import io.github.vampirestudios.obsidian.api.crucible.targets.TargetFactory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.vampirestudios.obsidian.api.crucible.conditions.ConditionFactory.parsePotionEffect;
import static io.github.vampirestudios.obsidian.api.crucible.skills.effects.ParticleEffect.parseColor;

public class SkillParser {

	private static final String SKILL_TYPE_KEY = "__skillType";
	private static volatile String modId;

	public static void setModId(String modId1) {
		modId = modId1;
	}

	// skill{...} @Target{...} ~event
	private static final Pattern SKILL_PATTERN  = Pattern.compile("([a-zA-Z0-9_:\\-]+)(?:\\{(.*?)})?");
	private static final Pattern TARGET_PATTERN = Pattern.compile("@([a-zA-Z0-9_:\\-]+)(?:\\{(.+?)})?");
	private static final Pattern EVENT_PATTERN  = Pattern.compile("~([a-zA-Z0-9_\\-]+)(?::([a-zA-Z0-9_\\-]+))?");

	// <skill.foo>
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<skill\\.(\\w+)>");

	// Mapping of canonical parameter names to aliases (KEYS ONLY)
	private static final Map<String, List<String>> parameterAliases = new HashMap<>() {{
		put("forward", List.of("f", "amount", "a"));
		put("rotate", List.of("rot"));
		put("yOffset", List.of("yo", "y"));
		put("xOffset", List.of("xo", "x"));
		put("zOffset", List.of("zo", "z"));
		put("viewDistance", List.of("vd"));
		put("useEyeLocation", List.of("uel"));
		put("lockPitch", List.of("lockpitch"));
		put("blockTypes", List.of("blocktype", "bt", "t", "material", "materials", "mat", "m", "blocks", "block", "b"));
		put("limit", List.of("max", "l"));
		put("originMustMatch", List.of("match"));
		put("forwardOffset", List.of("fo", "foffset"));
		put("sideOffset", List.of("so", "soffset"));
		put("radius", List.of("r", "maxradius", "maxr"));
		put("minRadius", List.of("minr", "minradius"));
		put("spacing", List.of("s", "gap", "distanceBetween", "db"));
		put("height", List.of("h", "yHeight", "verticalOffset"));
		put("angle", List.of("ang", "degrees", "rotationAngle"));
		put("pattern", List.of("patt", "type", "shape"));
		put("points", List.of("pt", "numPoints", "p"));
		put("targetDuration", List.of("tdur", "time", "duration"));
		put("offset", List.of("off", "shift", "displace"));
		put("direction", List.of("dir", "d"));
		put("targetType", List.of("ttype", "entityType", "t"));
		put("ignoreBlocks", List.of("ignoreb", "excludeblocks", "exclb"));
		put("mustSeeTarget", List.of("see", "lineOfSight", "los"));
		put("aboveOnly", List.of("above", "aonly"));
		put("startYOffset", List.of("syo", "ystartoffset", "ys"));
		put("targetYOffset", List.of("tyo", "ytargetoffset", "yt"));
		put("fromOrigin", List.of("fromorigin", "fo"));
		put("zigzag", List.of("zz"));
		put("zigzags", List.of("zzs"));
		put("zigzagOffset", List.of("zzo"));
		put("maxDistance", List.of("md"));
		put("particle", List.of("particleType"));
		put("color", List.of("c"));
		put("color2", List.of("c2"));
		put("modelData", List.of("m", "model"));
	}};

	private static final Map<String, String> aliasToParameter = new HashMap<>();
	static {
		for (var e : parameterAliases.entrySet()) {
			for (String alias : e.getValue()) {
				aliasToParameter.put(alias.toLowerCase(Locale.ROOT), e.getKey());
			}
			aliasToParameter.put(e.getKey().toLowerCase(Locale.ROOT), e.getKey());
		}
	}

	private static String resolveParamKey(String key) {
		if (key == null) return null;
		return aliasToParameter.getOrDefault(key.toLowerCase(Locale.ROOT), key);
	}

	// Skill type aliases
	private static final Map<String, String> aliasMap = new HashMap<>();
	static {
		aliasMap.put("setmodel", "setitemmodel");
		aliasMap.put("modelset", "setitemmodel");
		aliasMap.put("setitemmodel", "setitemmodel");

		aliasMap.put("effect:sound", "sound");
		aliasMap.put("s", "sound");
		aliasMap.put("e:s", "sound");
		aliasMap.put("e:sound", "sound");
		aliasMap.put("sound", "sound");

		aliasMap.put("effect:particles", "particles");
		aliasMap.put("effect:particle", "particles");
		aliasMap.put("particle", "particles");
		aliasMap.put("e:particles", "particles");
		aliasMap.put("e:particle", "particles");
		aliasMap.put("e:p", "particles");
		aliasMap.put("particles", "particles");

		aliasMap.put("aura", "aura");
		aliasMap.put("Aura", "aura");
	}

	public static SkillEntry parseSkillString(String skillStr) {
		if (skillStr == null || skillStr.isBlank()) return null;

		SkillEntry skillEntry = new SkillEntry();

		Matcher skillMatcher = SKILL_PATTERN.matcher(skillStr);
		if (!skillMatcher.find()) {
			System.err.println("Error: Failed to match skill pattern in skill string: " + skillStr);
			return null;
		}

		String rawSkillType = skillMatcher.group(1);
		String resolvedSkillType = aliasMap.getOrDefault(rawSkillType.toLowerCase(Locale.ROOT), rawSkillType.toLowerCase(Locale.ROOT));
		skillEntry.getSkillParameters().put(SKILL_TYPE_KEY, resolvedSkillType);

		String paramsStr = skillMatcher.group(2);
		if (paramsStr != null && !paramsStr.isBlank()) {
			Map<String, String> merged = new HashMap<>(skillEntry.getSkillParameters()); // keep type
			merged.putAll(parseSkillParameters(paramsStr, merged)); // parse may use runtime params
			skillEntry.setSkillParameters(merged);

			String repeatStr = merged.get("repeat");
			if (repeatStr != null) skillEntry.setRepeat(Integer.parseInt(repeatStr));

			String riStr = merged.get("repeatinterval");
			if (riStr != null) skillEntry.setRepeatInterval(Integer.parseInt(riStr));
		}

		// @target
		Matcher targetMatcher = TARGET_PATTERN.matcher(skillStr);
		if (targetMatcher.find()) {
			String targetType = targetMatcher.group(1).toLowerCase(Locale.ROOT);
			skillEntry.setTarget(targetType);

			String targetParamsStr = targetMatcher.group(2);
			if (targetParamsStr != null && !targetParamsStr.isBlank()) {
				skillEntry.setTargetParameters(parseTargetParameters(targetParamsStr));
			}
		} else {
			skillEntry.setTarget("trigger");
		}

		// ~event
		Matcher eventMatcher = EVENT_PATTERN.matcher(skillStr);
		if (eventMatcher.find()) {
			skillEntry.setEventName(eventMatcher.group(1));
			skillEntry.setEventParameter(eventMatcher.group(2));
		} else {
			skillEntry.setEventName("onCombat"); // keep your default
		}

		return skillEntry;
	}

	private static Map<String, String> parseSkillParameters(String paramsStr, Map<String, String> runtimeParams) {
		Map<String, String> parameters = new HashMap<>();
		String[] paramPairs = paramsStr.split(";");

		for (String param : paramPairs) {
			if (param == null) continue;
			param = param.trim();
			if (param.isEmpty()) continue;

			String[] keyValue = param.split("=", 2);
			if (keyValue.length != 2) {
				parameters.put(resolveParamKey(keyValue[0].trim()), null);
				continue;
			}

			String key = resolveParamKey(keyValue[0].trim());
			String value = keyValue[1].trim();

			if (isPlaceholder(value)) {
				value = resolvePlaceholder(value, runtimeParams);
			}

			parameters.put(key, value);
		}
		return parameters;
	}

	private static Map<String, String> parseTargetParameters(String paramsStr) {
		Map<String, String> parameters = new HashMap<>();
		String[] params = paramsStr.split(";");

		for (String param : params) {
			if (param == null) continue;
			param = param.trim();
			if (param.isEmpty()) continue;

			String[] keyValue = param.split("=", 2);
			String key = resolveParamKey(keyValue[0].trim());
			String value = (keyValue.length == 2) ? keyValue[1].trim() : null;
			parameters.put(key, value);
		}
		return parameters;
	}

	private static boolean isPlaceholder(String value) {
		return value != null && PLACEHOLDER_PATTERN.matcher(value).matches();
	}

	private static String resolvePlaceholder(String value, Map<String, String> runtimeParams) {
		if (value == null) return null;

		Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
		if (!matcher.find()) return value;

		String placeholderKey = matcher.group(1);
		String resolvedValue = runtimeParams.get(placeholderKey);
		return resolvedValue != null ? resolvedValue : value;
	}

	public static Skill createSkillFromEntry(SkillEntry entry) {
		if (entry == null) return null;

		String type = entry.getSkillParameters().get(SKILL_TYPE_KEY);
		String skillId = entry.getSkillParameters().get("s"); // optional for some actions

		if (type == null) {
			System.err.println("ERROR: Skill type missing. Params: " + entry.getSkillParameters());
			return null;
		}

		String targetType = entry.getTarget();
		Map<String, String> targetParams = entry.getTargetParameters();

		SkillTarget<?> skillTarget = TargetFactory.getTarget(targetType, targetParams);
		SkillTrigger trigger = SkillTrigger.findByName(entry.getEventName());

		if (skillTarget == null) {
			System.err.println("Unknown target: " + targetType);
			return null;
		}
		if (trigger == null) {
			System.err.println("Unknown trigger: " + entry.getEventName());
			return null;
		}

		Skill skill;
		if ("effect".equals(type)) {
			String resolvedEffect = resolvePlaceholder(entry.getSkillParameters().get("e"), entry.getSkillParameters());
			if (EffectRegistry.hasEffect(resolvedEffect)) {
				Effect effect = EffectRegistry.getEffectById(resolvedEffect);
				skill = new EffectSkill(skillId, skillTarget, trigger, effect);
			} else {
				System.err.println("Unknown effect ID: " + resolvedEffect);
				return null;
			}
		} else {
			skill = handleOtherSkillTypes(entry, skillId, skillTarget, trigger);
		}

		if (skill == null) return null;

		skill.setRepeat(entry.getRepeat());
		skill.setRepeatInterval(entry.getRepeatInterval());

		// Per-line conditions (optional; wrapper should also set these)
		skill.setConditions(ConditionParser.parseConditions(entry.getConditions(), true));
		skill.setTargetConditions(ConditionParser.parseConditions(entry.getTargetConditions(), false));

		return skill;
	}

	private static Skill handleOtherSkillTypes(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		Map<String, String> params = entry.getSkillParameters();
		String type = params.get(SKILL_TYPE_KEY);

		return switch (type) {
			case "sound" -> {
				String sound = params.get("s");
				String soundSource = params.getOrDefault("ss", "master");
				float volume = Float.parseFloat(params.getOrDefault("volume", "1.0"));
				float pitch = Float.parseFloat(params.getOrDefault("pitch", "1.0"));
				yield new SoundSkill(skillId, target, trigger, sound, soundSource, pitch, volume);
			}

			case "setitemmodel" -> {
				// accept m or modelData
				String md = params.getOrDefault("modelData", params.get("m"));
				if (md == null) md = "0";
				int customModelData = Integer.parseInt(md);
				yield new SetCustomModelDataSkill(skillId, target, trigger, customModelData);
			}

			case "skill" -> {
				String triggeredSkillId = params.get("s");
				if (triggeredSkillId == null) {
					System.err.println("skill{...} missing s=");
					yield null;
				}
				Identifier id = Identifier.fromNamespaceAndPath(modId, triggeredSkillId.toLowerCase(Locale.ROOT));
				Skill triggeredSkill = SkillManager.getInstance().getSkillById(id);
				if (triggeredSkill == null) {
					System.err.println("No valid skill found for nested skill: " + id);
					yield null;
				}
				yield new SkillSkill(skillId, target, trigger, triggeredSkill);
			}

			case "heal" -> {
				// YAML uses a= for heal amount
				String amt = params.getOrDefault("amount", params.get("a"));
				float healAmount = Float.parseFloat(amt);
				yield new HealSkill(skillId, target, trigger, healAmount);
			}

			case "potion" -> {
				MobEffect effectType = parsePotionEffect(params.getOrDefault("type", params.get("effect")));
				int duration = Integer.parseInt(params.getOrDefault("duration", "0"));

				// YAML often uses level + hasParticles
				int amplifier = Integer.parseInt(params.getOrDefault("amplifier",
						params.getOrDefault("level", "0")));
				boolean showParticles = Boolean.parseBoolean(params.getOrDefault("showParticles",
						params.getOrDefault("hasParticles", "true")));

				yield new PotionSkill(skillId, target, trigger, effectType, duration, amplifier, showParticles);
			}

			case "particles" -> parseParticleSkill(entry, skillId, target, trigger);

			case "particleline" -> parseParticleLineSkill(entry, skillId, target, trigger);

			case "teleport" -> {
				double maxDistance = Double.parseDouble(params.getOrDefault("maxDistance", params.getOrDefault("distance", "16")));
				boolean random = Boolean.parseBoolean(params.getOrDefault("random", "false"));
				yield new TeleportSkill(skillId, target, trigger, maxDistance, random);
			}

			case "summon" -> {
				EntityType<?> entityType = parseEntityType(params.getOrDefault("entity", params.get("typeId")));
				if (entityType == null) {
					System.err.println("summon{...} missing or invalid entity=");
					yield null;
				}
				int summonDuration = Integer.parseInt(params.getOrDefault("duration", "0"));
				yield new SummonSkill(skillId, target, trigger, entityType, summonDuration);
			}

			case "projectile" -> {
				EntityType<?> projectileType = parseEntityType(params.getOrDefault("projectile", params.get("entity")));
				if (projectileType == null) {
					System.err.println("projectile{...} missing or invalid projectile=");
					yield null;
				}
				float speed = Float.parseFloat(params.getOrDefault("speed", "1.0"));
				yield new ProjectileSkill(skillId, target, trigger, projectileType, speed, parseInlineEffects(params));
			}

			case "beam" -> {
				double distance = Double.parseDouble(params.getOrDefault("distance", "20"));
				Optional<Integer> beamColor = parseOptionalColor(params.get("color"));
				Optional<String> item = Optional.ofNullable(params.get("item"));
				Optional<Integer> modelData = parseOptionalInt(params.get("modelData"));
				Optional<String> action = Optional.ofNullable(params.get("action"));
				Optional<String> reason = Optional.ofNullable(params.get("reason"));
				Optional<Float> amount = parseOptionalFloat(params.get("amount"));
				yield new BeamSkill(skillId, target, trigger, distance, beamColor, item, modelData, action, reason, amount);
			}

			case "enderbeam" -> {
				int duration = Integer.parseInt(params.getOrDefault("duration", "20"));
				double yOffset = Double.parseDouble(params.getOrDefault("yOffset", "0"));
				yield new EnderBeamSkill(skillId, target, trigger, duration, yOffset);
			}

			case "channeling" -> {
				int duration = Integer.parseInt(params.getOrDefault("duration", "20"));
				yield new ChannelingSkill(skillId, target, trigger, duration, parseInlineEffects(params));
			}

			case "aoe" -> {
				double radius = Double.parseDouble(params.getOrDefault("radius", "3.0"));
				yield new AoESkill(skillId, target, trigger, radius, parseInlineEffects(params));
			}

			case "trap" -> {
				double activationRadius = Double.parseDouble(params.getOrDefault("radius", "2.0"));
				int trapLifetime = Integer.parseInt(params.getOrDefault("duration", "10"));
				yield new TrapSkill(skillId, target, trigger, activationRadius, parseInlineEffects(params), trapLifetime);
			}

			case "aura" -> {
				String auraId = params.getOrDefault("aura", skillId != null ? skillId : "aura");
				double radius = Double.parseDouble(params.getOrDefault("radius", "3.0"));
				int duration = Integer.parseInt(params.getOrDefault("duration", "100"));
				int tickInterval = Integer.parseInt(params.getOrDefault("tickInterval", "20"));
				boolean affectsCaster = Boolean.parseBoolean(params.getOrDefault("affectsCaster", "false"));
				yield new AuraSkill(skillId, target, trigger, auraId, radius, duration, tickInterval, parseConditions(entry.getConditions()), parseInlineEffects(params), affectsCaster);
			}

			case "setvariable", "setvar" -> {
				String variable = params.getOrDefault("name", params.get("var"));
				String value = params.getOrDefault("value", params.get("v"));
				if (variable == null || value == null) {
					System.err.println("setvariable{...} missing name/var or value");
					yield null;
				}
				yield new SetVariableSkill(skillId, target, trigger, variable, value);
			}

			case "unsetvariable", "unsetvar" -> {
				String variable = params.getOrDefault("name", params.get("var"));
				if (variable == null) {
					System.err.println("unsetvariable{...} missing name/var");
					yield null;
				}
				yield new VariableUnsetSkill(skillId, target, trigger, variable);
			}

			default -> {
				System.err.println("Unknown skill type: " + type);
				yield null;
			}
		};
	}

	private static EntityType<?> parseEntityType(String entityId) {
		if (entityId == null || entityId.isBlank()) return null;
		Identifier id = Identifier.tryParse(entityId);
		if (id == null) return null;
		return BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
	}

	private static Optional<Integer> parseOptionalInt(String value) {
		if (value == null || value.isBlank()) return Optional.empty();
		return Optional.of(Integer.parseInt(value));
	}

	private static Optional<Float> parseOptionalFloat(String value) {
		if (value == null || value.isBlank()) return Optional.empty();
		return Optional.of(Float.parseFloat(value));
	}

	private static Optional<Integer> parseOptionalColor(String value) {
		if (value == null || value.isBlank()) return Optional.empty();
		return Optional.of(parseColor(value));
	}

	private static List<Effect> parseInlineEffects(Map<String, String> params) {
		String effectsRaw = params.getOrDefault("effects", params.get("e"));
		if (effectsRaw == null || effectsRaw.isBlank()) return List.of();
		List<String> effectLines = Arrays.stream(effectsRaw.split("[;|]"))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();
		return parseEffects(effectLines);
	}

	// ---- Particle parsing helpers (kept from you, but fixed some key alias usage) ----

	private static ParticleSkill parseParticleSkill(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		Map<String, Object> common = parseParticleParameters(entry);

		return new ParticleSkill(
				skillId, target, trigger,
				(String) common.get("particleType"),
				Optional.ofNullable((String) common.get("mob")),
				(int) common.get("amount"),
				(double) common.get("spread"),
				(double) common.get("hSpread"),
				(double) common.get("vSpread"),
				(double) common.get("xSpread"),
				(double) common.get("ySpread"),
				(double) common.get("zSpread"),
				(double) common.get("speed"),
				(double) common.get("yOffset"),
				(int) common.get("viewDistance"),
				(boolean) common.get("fromOrigin"),
				(boolean) common.get("directional"),
				(boolean) common.get("directionReversed"),
				(Vec3) common.get("direction"),
				(int) common.get("fixedYaw"),
				(int) common.get("fixedPitch"),
				(Optional<Integer>) common.get("color"),
				(Optional<Integer>) common.get("color2"),
				(Optional<Integer>) common.get("duration"),
				(Optional<String>) common.get("block"),
				(Optional<String>) common.get("item"),
				(Optional<Vec3>) common.get("targetPos"),
				(boolean) common.get("exactOffsets"),
				(Vec3) common.get("forwardOffset"),
				(Vec3) common.get("sideOffset")
		);
	}

	private static ParticleLineSkill parseParticleLineSkill(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		Map<String, Object> common = parseParticleParameters(entry);

		double distanceBetween = Double.parseDouble(entry.getSkillParameters().getOrDefault("distanceBetween", "0.25"));
		double startYOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("startYOffset", "0"));
		double targetYOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("targetYOffset", "0"));
		boolean zigzag = Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("zigzag", "false"));
		int zigzags = Integer.parseInt(entry.getSkillParameters().getOrDefault("zigzags", "10"));
		double zigzagOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("zigzagOffset", "0.2"));
		double maxDistance = Double.parseDouble(entry.getSkillParameters().getOrDefault("maxDistance", "256"));

		return new ParticleLineSkill(
				skillId, target, trigger,
				(String) common.get("particleType"),
				Optional.ofNullable((String) common.get("mob")),
				(int) common.get("amount"),
				(double) common.get("spread"),
				(double) common.get("hSpread"),
				(double) common.get("vSpread"),
				(double) common.get("xSpread"),
				(double) common.get("ySpread"),
				(double) common.get("zSpread"),
				(double) common.get("speed"),
				(double) common.get("yOffset"),
				(int) common.get("viewDistance"),
				(boolean) common.get("fromOrigin"),
				(boolean) common.get("directional"),
				(boolean) common.get("directionReversed"),
				(Vec3) common.get("direction"),
				(int) common.get("fixedYaw"),
				(int) common.get("fixedPitch"),
				(Optional<Integer>) common.get("color"),
				(Optional<Integer>) common.get("color2"),
				(Optional<Integer>) common.get("duration"),
				(Optional<String>) common.get("block"),
				(Optional<String>) common.get("item"),
				(Optional<Vec3>) common.get("targetPos"),
				(boolean) common.get("exactOffsets"),
				(Vec3) common.get("forwardOffset"),
				(Vec3) common.get("sideOffset"),
				distanceBetween,
				startYOffset,
				targetYOffset,
				zigzag,
				zigzags,
				zigzagOffset,
				maxDistance
		);
	}

	private static Map<String, Object> parseParticleParameters(SkillEntry entry) {
		Map<String, Object> params = new HashMap<>();

		Map<String, String> sp = entry.getSkillParameters();

		params.put("particleType", sp.getOrDefault("particle", "reddust"));
		params.put("mob", sp.get("mob"));
		params.put("amount", Integer.parseInt(sp.getOrDefault("amount", sp.getOrDefault("a", "10"))));
		params.put("spread", Double.parseDouble(sp.getOrDefault("spread", "0")));
		params.put("hSpread", Double.parseDouble(sp.getOrDefault("hSpread", params.get("spread").toString())));
		params.put("vSpread", Double.parseDouble(sp.getOrDefault("vSpread", params.get("spread").toString())));
		params.put("xSpread", Double.parseDouble(sp.getOrDefault("xSpread", params.get("hSpread").toString())));
		params.put("ySpread", Double.parseDouble(sp.getOrDefault("ySpread", params.get("vSpread").toString())));
		params.put("zSpread", Double.parseDouble(sp.getOrDefault("zSpread", params.get("hSpread").toString())));
		params.put("speed", Double.parseDouble(sp.getOrDefault("speed", "0")));
		params.put("yOffset", Double.parseDouble(sp.getOrDefault("yOffset", "0")));
		params.put("viewDistance", Integer.parseInt(sp.getOrDefault("viewDistance", "128")));
		params.put("fromOrigin", Boolean.parseBoolean(sp.getOrDefault("fromOrigin", sp.getOrDefault("fromorigin", "false"))));
		params.put("directional", Boolean.parseBoolean(sp.getOrDefault("directional", "false")));
		params.put("directionReversed", Boolean.parseBoolean(sp.getOrDefault("directionReversed", "false")));
		params.put("direction", parseDirection(sp.getOrDefault("direction", "0,0,0")));
		params.put("fixedYaw", Integer.parseInt(sp.getOrDefault("fixedYaw", "-1111")));
		params.put("fixedPitch", Integer.parseInt(sp.getOrDefault("fixedPitch", "-1111")));

		params.put("color", sp.containsKey("color") ? Optional.of(parseColor(sp.get("color"))) : Optional.empty());
		params.put("color2", sp.containsKey("color2") ? Optional.of(parseColor(sp.get("color2"))) : Optional.empty());

		params.put("duration", sp.containsKey("duration")
				? Optional.of(Integer.parseInt(sp.get("duration")))
				: Optional.empty());

		params.put("block", sp.containsKey("block") ? Optional.ofNullable(sp.get("block")) : Optional.empty());
		params.put("item", sp.containsKey("item") ? Optional.ofNullable(sp.get("item")) : Optional.empty());
		params.put("targetPos", sp.containsKey("targetPos") ? Optional.of(parseDirection(sp.get("targetPos"))) : Optional.empty());

		params.put("exactOffsets", Boolean.parseBoolean(sp.getOrDefault("exactOffsets", "false")));
		params.put("forwardOffset", parseDirection(sp.getOrDefault("forwardOffset", "0,0,0")));
		params.put("sideOffset", parseDirection(sp.getOrDefault("sideOffset", "0,0,0")));

		return params;
	}

	private static Vec3 parseDirection(String s) {
		String[] parts = s.split(",");
		if (parts.length > 1) {
			double x = Double.parseDouble(parts[0].trim());
			double y = Double.parseDouble(parts[1].trim());
			double z = Double.parseDouble(parts[2].trim());
			return new Vec3(x, y, z);
		} else {
			double value = Double.parseDouble(parts[0].trim());
			return new Vec3(value, value, value);
		}
	}

	// Legacy helpers you had (kept for compatibility)
	public static List<Condition> parseConditions(List<String> conditionLines) {
		if (conditionLines == null || conditionLines.isEmpty()) return Collections.emptyList();

		List<Condition> conditions = new ArrayList<>();
		for (String line : conditionLines) {
			if (line == null || line.isBlank()) continue;
			ConditionParser.ConditionType type = ConditionParser.parseConditionType(line);
			Map<String, Object> parameters = ConditionParser.parseParameters(line);

			Condition condition = ConditionFactory.createCondition(type.name().toLowerCase(Locale.ROOT), parameters);
			if (condition != null) conditions.add(condition);
		}
		return conditions;
	}

	public static List<Effect> parseEffects(List<String> effectLines) {
		if (effectLines == null || effectLines.isEmpty()) return Collections.emptyList();
		List<Effect> effects = new ArrayList<>();
		for (String line : effectLines) {
			if (line == null || line.isBlank()) continue;
			effects.add(EffectFactory.createEffect(line));
		}
		return effects;
	}
}