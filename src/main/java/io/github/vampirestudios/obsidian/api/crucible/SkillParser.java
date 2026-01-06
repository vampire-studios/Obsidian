package io.github.vampirestudios.obsidian.api.crucible;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.conditions.ConditionFactory;
import io.github.vampirestudios.obsidian.api.crucible.skills.*;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.EffectFactory;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import io.github.vampirestudios.obsidian.api.crucible.targets.TargetFactory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.vampirestudios.obsidian.api.crucible.conditions.ConditionFactory.parsePotionEffect;
import static io.github.vampirestudios.obsidian.api.crucible.skills.effects.ParticleEffect.parseColor;

public class SkillParser {
	private static String modId;

	public static void setModId(String modId1) {
		modId = modId1;
	}

	// Mapping of main parameter names to their aliases
	private static final Map<String, List<String>> parameterAliases = new HashMap<>() {{
		put("forward", List.of("f", "amount", "a"));
		put("rotate", List.of("rot"));
		put("yOffset", List.of("yo", "y"));
		put("xOffset", List.of("xo", "x"));
		put("zOffset", List.of("zo", "z"));
		put("viewDistance", List.of("vd"));
		put("useEyeLocation", List.of("uel"));
		put("lockPitch", List.of("lockpitch"));
		put("onSurface", List.of("onSurface"));
		put("blockTypes", List.of("blocktype", "bt", "t", "material", "materials", "mat", "m", "blocks", "block", "b"));
		put("limit", List.of("max", "l", "m"));
		put("originMustMatch", List.of("match"));
		put("forwardOffset", List.of("fo", "foffset"));
		put("sideOffset", List.of("so", "soffset"));
		put("xoffset", List.of("xo", "x"));
		put("yoffset", List.of("yo", "y"));
		put("zoffset", List.of("zo", "z"));
		put("radius", List.of("r", "maxradius", "maxr"));
		put("minRadius", List.of("minr", "minradius"));
		put("spacing", List.of("s", "gap", "distanceBetween"));
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
		put("amount", List.of("a"));
		put("distanceBetween", List.of("db"));
		put("startYOffset", List.of("syo", "ystartoffset", "ys"));
		put("targetYOffset", List.of("tyo", "ytargetoffset", "yt"));
		put("fromOrigin", List.of("fo"));
		put("zigzag", List.of("zz"));
		put("zigzags", List.of("zzs"));
		put("zigzagOffset", List.of("zzo"));
		put("maxDistance", List.of("md"));
		put("particleType", List.of("particle"));
		put("color", List.of("c"));
		put("color2", List.of("c2"));
		put("model", List.of("m"));
	}};

	// Reverse mapping for quick alias lookup
	private static final Map<String, String> aliasToParameter = new HashMap<>();

	static {
		// Populate aliasToParameter for fast alias resolution
		for (Map.Entry<String, List<String>> entry : parameterAliases.entrySet()) {
			String mainParam = entry.getKey();
			for (String alias : entry.getValue()) {
				aliasToParameter.put(alias, mainParam);
			}
		}
	}

	// Method to get the main parameter name for a given alias or itself if no alias is found
	private static String resolveParameterName(String alias) {
		return aliasToParameter.getOrDefault(alias, alias);
	}

	public static List<SkillEntry> parseYamlSkills(Map<String, Object> yamlData) {
		List<SkillEntry> skillEntries = new ArrayList<>();

		for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
			String skillId = entry.getKey();
			Map<String, Object> skillData = (Map<String, Object>) entry.getValue();

			// Parse conditions if present
			List<String> conditionLines = (List<String>) skillData.getOrDefault("Conditions", List.of());
			List<Condition> conditions = ConditionParser.parseConditions(conditionLines, true);

			// Parse each skill line
			List<String> skillLines = (List<String>) skillData.get("Skills");
			for (String skillLine : skillLines) {
				SkillEntry skillEntry = parseSkillString(skillLine); // Automatically uses captured parameters
				skillEntry.setConditions(conditionLines); // Add conditions if needed
				skillEntries.add(skillEntry);
			}
		}
		return skillEntries;
	}

	public static SkillEntry parseSkillString(String skillStr) {
		if (skillStr == null) {
			System.err.println("Error: Skill string is null.");
			return null;
		}

		SkillEntry skillEntry = new SkillEntry();

		// Patterns
		Pattern skillPattern = Pattern.compile("(\\w+)(?:\\{(.*?)})?");
		Pattern targetPattern = Pattern.compile("@(\\w+)(?:\\{(.+?)})?");
		Pattern eventPattern = Pattern.compile("~(\\w+)(?::(\\w+))?");

		// Match the skill part
		Matcher skillMatcher = skillPattern.matcher(skillStr);
		if (skillMatcher.find()) {
			String rawSkillType = skillMatcher.group(1); // Extract skill type (e.g., "effect:particle")
			System.out.println("DEBUG: Raw skill type: " + rawSkillType);

			String resolvedSkillType = aliasMap.getOrDefault(rawSkillType.toLowerCase(), rawSkillType.toLowerCase()); // Resolve alias
			if (resolvedSkillType == null || resolvedSkillType.isEmpty()) {
				System.err.println("ERROR: Skill type is missing or null for skill ID: " + rawSkillType);
				return skillEntry;
			}

			// Parse parameters, combining with parent parameters for placeholders
			String paramsStr = skillMatcher.group(2);
			if (paramsStr != null) {
				parseParametersWithNesting(paramsStr, skillEntry);
				// Parse parameters using parseSkillParameters, allowing placeholder resolution
				Map<String, String> runtimeParams = skillEntry.getSkillParameters();
				skillEntry.setSkillParameters(parseSkillParameters(paramsStr, runtimeParams));

				// Check and set repeat and repeatInterval
				String repeatStr = skillEntry.getSkillParameters().get("repeat");
				if (repeatStr != null) {
					skillEntry.setRepeat(Integer.parseInt(repeatStr));
				}

				String repeatIntervalStr = skillEntry.getSkillParameters().get("repeatinterval");
				if (repeatIntervalStr != null) {
					skillEntry.setRepeatInterval(Integer.parseInt(repeatIntervalStr));
				}
			}

			System.out.println("DEBUG: Resolved skill type: " + resolvedSkillType);
			skillEntry.getSkillParameters().put("type", resolvedSkillType); // Add resolved type as a parameter
		} else {
			System.err.println("Error: Failed to match skill pattern in skill string: " + skillStr);
			return null;
		}

		// Match @target
		Matcher targetMatcher = targetPattern.matcher(skillStr);
		if (targetMatcher.find()) {
			String targetType = targetMatcher.group(1).toLowerCase();
			skillEntry.setTarget(targetType);

			// Extract target parameters if they exist
			String targetParamsStr = targetMatcher.group(2);
			if (targetParamsStr != null) {
				// Use parseTargetParameters here
				Map<String, String> targetParameters = parseTargetParameters(targetParamsStr);
				skillEntry.setTargetParameters(targetParameters);
			}
		} else {
			skillEntry.setTarget("trigger");
		}

		// Match ~event
		Matcher eventMatcher = eventPattern.matcher(skillStr);
		if (eventMatcher.find()) {
			skillEntry.setEventName(eventMatcher.group(1));
			skillEntry.setEventParameter(eventMatcher.group(2)); // may be null
		} else {
			// Default event to "onCombat" if none specified
			skillEntry.setEventName("onCombat");
		}

		return skillEntry;
	}

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<skill\\.(\\w+)>");

	private static Map<String, String> parseSkillParameters(String paramsStr, Map<String, String> runtimeParams) {
		Map<String, String> parameters = new HashMap<>();

		String[] paramPairs = paramsStr.split(";");
		for (String param : paramPairs) {
			String[] keyValue = param.split("=");
			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = resolveParameterName(keyValue[1].trim());

				// Check if the value is a placeholder and resolve if it is
				if (isPlaceholder(value)) {
					value = resolvePlaceholder(value, runtimeParams);
				}

				parameters.put(key, value);
				System.out.println("DEBUG: Parsed parameter " + key + " with value " + value);
			}
		}
		return parameters;
	}

	private static boolean isPlaceholder(String value) {
		return PLACEHOLDER_PATTERN.matcher(value).matches();
	}

	private static String resolvePlaceholder(String value, Map<String, String> runtimeParams) {
		if (value == null) {
			System.out.println("DEBUG: Attempted to resolve a null placeholder value.");
			return null;
		}

		Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
		if (matcher.find()) {
			String placeholderKey = matcher.group(1);
			String resolvedValue = runtimeParams.get(placeholderKey);

			if (resolvedValue != null) {
				System.out.println("DEBUG: Placeholder <skill." + placeholderKey + "> resolved to " + resolvedValue);
				return resolvedValue;
			} else {
				System.out.println("DEBUG: Placeholder <skill." + placeholderKey + "> not found in runtimeParams.");
				return value; // Return the original placeholder if not found
			}
		}
		System.out.println("DEBUG: No placeholder found in value: " + value);
		return value;
	}

	private static Map<String, String> parseTargetParameters(String paramsStr) {
		Map<String, String> parameters = new HashMap<>();
		String[] params = paramsStr.split(";");

		for (String param : params) {
			String[] keyValue = param.split("=");

			String mainParam = resolveParameterName(keyValue[0].trim());
			if (keyValue.length == 2) {
				// Resolve the main parameter name from the alias
				parameters.put(mainParam, keyValue[1].trim());
			} else {
				// Handle cases where there's no '=' sign, using alias resolution
				parameters.put(mainParam, null);
			}
		}
		return parameters;
	}


	private static void parseParametersWithNesting(String paramsStr, SkillEntry skillEntry) {
		Pattern listPattern = Pattern.compile("\\[(.*?)\\]");
		String[] params = paramsStr.split(";");

		for (String param : params) {
			String[] keyValue = param.split("=", 2);
			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();

				// Check if the parameter value is a list (indicating sub-skills or sub-entries)
				Matcher listMatcher = listPattern.matcher(value);
				if (listMatcher.find()) {
					List<SkillEntry> subSkills = new ArrayList<>();
					String[] nestedSkills = listMatcher.group(1).split(";");
					for (String nestedSkillStr : nestedSkills) {
						SkillEntry nestedSkillEntry = parseSkillString(nestedSkillStr.trim());
						subSkills.add(nestedSkillEntry);
					}
					skillEntry.getSubSkills().addAll(subSkills);
				} else {
					skillEntry.getSkillParameters().put(key, value);
				}
			} else {
				skillEntry.getSkillParameters().put(keyValue[0].trim(), null);
			}
		}
	}

	public static Skill createSkillFromEntry(SkillEntry entry) {
		if (entry == null) {
			System.err.println("ERROR: Skill entry is null.");
			return null;
		}

		String skillId = entry.getSkillParameters().get("s");
		String type = entry.getSkillParameters().get("type"); // Assuming 'type' key to determine skill type

		// Check if skill type is missing
		if (type == null) {
			System.err.println("ERROR: Skill type is missing or null for skill ID: " + skillId);
			System.err.println("DEBUG: Skill parameters were: " + entry.getSkillParameters());
			return null;
		}

		String targetType = entry.getTarget();
		Map<String, String> targetParameters = entry.getTargetParameters();

		SkillTarget<?> skillTarget = TargetFactory.getTarget(targetType, targetParameters);
		String triggerType = entry.getEventName();
		SkillTrigger trigger = SkillTrigger.findByName(triggerType);

		if (skillTarget == null) {
			System.err.println("Unknown target");
			return null;
		}

		if (trigger == null) {
			System.err.println("Unknown trigger");
			return null;
		}

		Skill skill;

		String resolvedEffect = resolvePlaceholder(entry.getSkillParameters().get("e"), entry.getSkillParameters());

		if ("effect".equals(type)) {
			if (EffectRegistry.hasEffect(resolvedEffect)) {
				Effect effect = EffectRegistry.getEffectById(resolvedEffect);
				skill = new EffectSkill(skillId, skillTarget, trigger, effect);
			} else {
				System.err.println("Unknown effect ID: " + resolvedEffect);
				return null;
			}
		} else {
			// Other skill types, e.g., sound, particles, etc.
			skill = handleOtherSkillTypes(entry, skillId, skillTarget, trigger);
		}

		if (skill == null) {
			System.err.println("ERROR: Failed to create skill for ID: " + skillId + ". Type was: " + type);
			return null;
		}

		skill.setRepeat(entry.getRepeat());
		skill.setRepeatInterval(entry.getRepeatInterval());

		// Parse Conditions (applied to caster)
		List<Condition> casterConditions = ConditionParser.parseConditions(entry.getConditions(), true);
		skill.setConditions(casterConditions);

		// Parse TargetConditions (applied to target)
		List<Condition> targetConditions = ConditionParser.parseConditions(entry.getTargetConditions(), false);
		skill.setTargetConditions(targetConditions);

		return skill;
	}

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
	}

	// Example method to handle other types if needed
	private static Skill handleOtherSkillTypes(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		String type = entry.getSkillParameters().get("type");

		Skill skill;
		switch (type) {
			case "sound":
				String sound = entry.getSkillParameters().get("s");
				String soundSource = entry.getSkillParameters().getOrDefault("ss", "master");
				float volume = Float.parseFloat(entry.getSkillParameters().getOrDefault("volume", "1.0"));
				float pitch = Float.parseFloat(entry.getSkillParameters().getOrDefault("pitch", "1.0"));
				skill = new SoundSkill(skillId, target, trigger, sound, soundSource, pitch, volume);
				break;

			case "setitemmodel":
				int customModelData = Integer.parseInt(entry.getSkillParameters().get("m"));
				skill = new SetCustomModelDataSkill(skillId, target, trigger, customModelData);
				break;

			case "skill":
				String triggeredSkillId = entry.getSkillParameters().get("s");
				Skill triggeredSkill = SkillManager.getInstance().getSkillById(Identifier.fromNamespaceAndPath(modId, triggeredSkillId.toLowerCase(Locale.ROOT)));

				if (triggeredSkill == null) {
					System.err.println("No valid skill found for SkillSkill: " + triggeredSkillId.toLowerCase(Locale.ROOT));
					return null;
				}

				skill = new SkillSkill(skillId, target, trigger, triggeredSkill);
				break;

			case "enderbeam": {
				int distance = Integer.parseInt(entry.getSkillParameters().getOrDefault("distance", "60"));
				float yOffset1 = Integer.parseInt(entry.getSkillParameters().getOrDefault("yOffset", "0"));
				skill = new EnderBeamSkill(skillId, target, trigger, distance, yOffset1);
				break;
			}

			case "aura":
				String auraId = entry.getSkillParameters().get("auraId");
				double radius = Double.parseDouble(entry.getSkillParameters().getOrDefault("radius", "5.0"));
				int duration = Integer.parseInt(entry.getSkillParameters().getOrDefault("duration", "100"));
				int tickInterval = Integer.parseInt(entry.getSkillParameters().getOrDefault("tickInterval", "20"));
				boolean affectsCaster = Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("affectsCaster", "true"));

				List<Condition> conditions = parseConditions(entry.getConditionLines());
				List<Effect> effects = parseEffects(entry.getEffectLines());

				skill = new AuraSkill(skillId, target, trigger, auraId, radius, duration, tickInterval, conditions, effects, affectsCaster);
				break;

			case "variableUnset":
				String variableName1 = entry.getSkillParameters().get("var");
				skill = new VariableUnsetSkill(skillId, target, trigger, variableName1);
				break;

			case "variableSet":
			case "setvariable":
				String variableName2 = entry.getSkillParameters().get("var");
				String valueExpression = entry.getSkillParameters().get("value");
				skill = new SetVariableSkill(skillId, target, trigger, variableName2, valueExpression);
				break;

			case "switch":
				Condition switchCondition = parseSwitchCondition(entry);
				Map<String, Skill> cases = parseCases(entry.getSkillParameters());
				Skill defaultCase = parseDefaultCase(entry.getSkillParameters());
				skill = new SwitchSkill(skillId, target, trigger, switchCondition, cases, defaultCase);
				break;

			case "heal":
				float healAmount = Float.parseFloat(entry.getSkillParameters().get("amount"));
				skill = new HealSkill(skillId, target, trigger, healAmount);
				break;

			case "potion":
				MobEffect effectType = parsePotionEffect(entry.getSkillParameters().get("type"));
				int duration1 = Integer.parseInt(entry.getSkillParameters().get("duration"));
				int amplifier = Integer.parseInt(entry.getSkillParameters().get("amplifier"));
				boolean showParticles = Boolean.parseBoolean(entry.getSkillParameters().get("showParticles"));
				skill = new PotionSkill(skillId, target, trigger, effectType, duration1, amplifier, showParticles);
				break;

			case "particles":
				skill = parseParticleSkill(entry, skillId, target, trigger);
				break;

			case "particleline":
				skill = parseParticleLineSkill(entry, skillId, target, trigger);
				break;

			case "beam":
				double distance = Double.parseDouble(entry.getSkillParameters().getOrDefault("distance", "50"));
				Optional<String> itemId = entry.getSkillParameters().containsKey("item")
						? Optional.of(entry.getSkillParameters().getOrDefault("item", "minecraft:leather_horse_armor"))
						: Optional.empty();
				Optional<Integer> beamColor = entry.getSkillParameters().containsKey("color")
						? Optional.of(parseColor(entry.getSkillParameters().get("color")))
						: Optional.empty();
				Optional<Integer> modelData = Optional.of(Integer.parseInt(entry.getSkillParameters().get("modelData")));
				Optional<String> action = Optional.ofNullable(entry.getSkillParameters().get("action"));
				Optional<String> reason = Optional.ofNullable(entry.getSkillParameters().get("reason"));
				Optional<Float> amount = entry.getSkillParameters().containsKey("amount")
						? Optional.of(Float.valueOf(entry.getSkillParameters().get("amount")))
						: Optional.empty();

				skill = new BeamSkill(skillId, target, trigger, distance, beamColor, itemId, modelData, action, reason, amount);
				break;
			default:
				System.err.println("Unknown skill type: " + type);
				return null;
		}

		return skill;
	}

	private static ParticleSkill parseParticleSkill(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		Map<String, Object> commonParams = parseParticleParameters(entry);

		return new ParticleSkill(
				skillId, target, trigger,
				(String) commonParams.get("particleType"),
				Optional.ofNullable((String) commonParams.get("mob")),
				(int) commonParams.get("amount"),
				(double) commonParams.get("spread"),
				(double) commonParams.get("hSpread"),
				(double) commonParams.get("vSpread"),
				(double) commonParams.get("xSpread"),
				(double) commonParams.get("ySpread"),
				(double) commonParams.get("zSpread"),
				(double) commonParams.get("speed"),
				(double) commonParams.get("yOffset"),
				(int) commonParams.get("viewDistance"),
				(boolean) commonParams.get("fromOrigin"),
				(boolean) commonParams.get("directional"),
				(boolean) commonParams.get("directionReversed"),
				(Vec3) commonParams.get("direction"),
				(int) commonParams.get("fixedYaw"),
				(int) commonParams.get("fixedPitch"),
				(Optional<Integer>) commonParams.get("color"),
				(Optional<Integer>) commonParams.get("color2"),
				(Optional<Integer>) commonParams.get("duration"),
				(Optional<String>) commonParams.get("block"),
				(Optional<String>) commonParams.get("item"),
				(Optional<Vec3>) commonParams.get("targetPos"),
				(boolean) commonParams.get("exactOffsets"),
				(Vec3) commonParams.get("forwardOffset"),
				(Vec3) commonParams.get("sideOffset")
		);
	}

	private static ParticleLineSkill parseParticleLineSkill(SkillEntry entry, String skillId, SkillTarget<?> target, SkillTrigger trigger) {
		Map<String, Object> commonParams = parseParticleParameters(entry);

		// Line-specific parameters
		double distanceBetween = Double.parseDouble(entry.getSkillParameters().getOrDefault("distanceBetween", "0.25"));
		double startYOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("startYOffset", "0"));
		double targetYOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("targetYOffset", "0"));
		boolean zigzag = Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("zigzag", "false"));
		int zigzags = Integer.parseInt(entry.getSkillParameters().getOrDefault("zigzags", "10"));
		double zigzagOffset = Double.parseDouble(entry.getSkillParameters().getOrDefault("zigzagOffset", "0.2"));
		double maxDistance = Double.parseDouble(entry.getSkillParameters().getOrDefault("maxDistance", "256"));

		return new ParticleLineSkill(
				skillId, target, trigger,
				(String) commonParams.get("particleType"),
				Optional.ofNullable((String) commonParams.get("mob")),
				(int) commonParams.get("amount"),
				(double) commonParams.get("spread"),
				(double) commonParams.get("hSpread"),
				(double) commonParams.get("vSpread"),
				(double) commonParams.get("xSpread"),
				(double) commonParams.get("ySpread"),
				(double) commonParams.get("zSpread"),
				(double) commonParams.get("speed"),
				(double) commonParams.get("yOffset"),
				(int) commonParams.get("viewDistance"),
				(boolean) commonParams.get("fromOrigin"),
				(boolean) commonParams.get("directional"),
				(boolean) commonParams.get("directionReversed"),
				(Vec3) commonParams.get("direction"),
				(int) commonParams.get("fixedYaw"),
				(int) commonParams.get("fixedPitch"),
				(Optional<Integer>) commonParams.get("color"),
				(Optional<Integer>) commonParams.get("color2"),
				(Optional<Integer>) commonParams.get("duration"),
				(Optional<String>) commonParams.get("block"),
				(Optional<String>) commonParams.get("item"),
				(Optional<Vec3>) commonParams.get("targetPos"),
				(boolean) commonParams.get("exactOffsets"),
				(Vec3) commonParams.get("forwardOffset"),
				(Vec3) commonParams.get("sideOffset"),
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

		// Common particle parameters
		params.put("particleType", entry.getSkillParameters().getOrDefault("particle", "reddust"));
		params.put("mob", entry.getSkillParameters().get("mob"));
		params.put("amount", Integer.parseInt(entry.getSkillParameters().getOrDefault("amount", "10")));
		params.put("spread", Double.parseDouble(entry.getSkillParameters().getOrDefault("spread", "0")));
		params.put("hSpread", Double.parseDouble(entry.getSkillParameters().getOrDefault("hSpread", params.get("spread").toString())));
		params.put("vSpread", Double.parseDouble(entry.getSkillParameters().getOrDefault("vSpread", params.get("spread").toString())));
		params.put("xSpread", Double.parseDouble(entry.getSkillParameters().getOrDefault("xSpread", params.get("hSpread").toString())));
		params.put("ySpread", Double.parseDouble(entry.getSkillParameters().getOrDefault("ySpread", params.get("vSpread").toString())));
		params.put("zSpread", Double.parseDouble(entry.getSkillParameters().getOrDefault("zSpread", params.get("hSpread").toString())));
		params.put("speed", Double.parseDouble(entry.getSkillParameters().getOrDefault("speed", "0")));
		params.put("yOffset", Double.parseDouble(entry.getSkillParameters().getOrDefault("yOffset", "0")));
		params.put("viewDistance", Integer.parseInt(entry.getSkillParameters().getOrDefault("viewDistance", "128")));
		params.put("fromOrigin", Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("fromorigin", "false")));
		params.put("directional", Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("directional", "false")));
		params.put("directionReversed", Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("directionReversed", "false")));
		params.put("direction", parseDirection(entry.getSkillParameters().getOrDefault("direction", "0,0,0")));
		params.put("fixedYaw", Integer.parseInt(entry.getSkillParameters().getOrDefault("fixedyaw", "-1111")));
		params.put("fixedPitch", Integer.parseInt(entry.getSkillParameters().getOrDefault("fixedpitch", "-1111")));
		params.put("color", entry.getSkillParameters().containsKey("color")
				? Optional.of(parseColor(entry.getSkillParameters().get("color")))
				: Optional.empty());
		params.put("duration", entry.getSkillParameters().containsKey("duration")
				? Optional.of(entry.getSkillParameters().get("duration"))
				: Optional.empty());
		params.put("color2", entry.getSkillParameters().containsKey("color2")
				? Optional.of(parseColor(entry.getSkillParameters().get("color2")))
				: Optional.empty());
		params.put("block", entry.getSkillParameters().containsKey("block")
				? Optional.ofNullable(entry.getSkillParameters().get("block"))
				: Optional.empty());
		params.put("item", entry.getSkillParameters().containsKey("item")
				? Optional.ofNullable(entry.getSkillParameters().get("item"))
				: Optional.empty());
		params.put("targetPos", entry.getSkillParameters().containsKey("targetPos")
				? Optional.of(parsePosition(entry.getSkillParameters().get("targetPos")))
				: Optional.empty());
		params.put("exactOffsets", Boolean.parseBoolean(entry.getSkillParameters().getOrDefault("exactOffsets", "false")));
		params.put("forwardOffset", parseOffset(entry.getSkillParameters().getOrDefault("forwardOffset", "0,0,0")));
		params.put("sideOffset", parseOffset(entry.getSkillParameters().getOrDefault("sideOffset", "0,0,0")));

		return params;
	}

	private static Condition parseSwitchCondition(SkillEntry entry) {
		String conditionStr = entry.getSkillParameters().get("condition");

		if (conditionStr == null) {
			throw new IllegalArgumentException("Switch skill requires a condition to evaluate cases.");
		}

		// Parse the condition string into a Condition object
		List<String> conditionList = List.of(conditionStr);
		List<Condition> parsedConditions = ConditionParser.parseConditions(conditionList, false);

		if (parsedConditions.isEmpty()) {
			throw new IllegalArgumentException("Failed to parse condition for switch skill: " + conditionStr);
		}

		// Use the first parsed condition for the switch condition
		return parsedConditions.get(0);
	}

	private static Map<String, Skill> parseCases(Map<String, String> skillParameters) {
		Map<String, Skill> cases = new HashMap<>();

		for (Map.Entry<String, String> entry : skillParameters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (key.startsWith("case ")) {
				// Extract case name from the key (e.g., "case CRIT" -> "CRIT")
				String caseName = key.substring("case ".length()).trim();

				// Parse the skill for the case
				SkillEntry caseSkillEntry = SkillParser.parseSkillString(value);
				Skill caseSkill = SkillParser.createSkillFromEntry(caseSkillEntry);

				cases.put(caseName, caseSkill);
			}
		}

		return cases;
	}

	private static Skill parseDefaultCase(Map<String, String> skillParameters) {
		String defaultCaseStr = skillParameters.get("default");

		if (defaultCaseStr == null) {
			return null; // No default case defined
		}

		// Parse the skill for the default case
		SkillEntry defaultSkillEntry = SkillParser.parseSkillString(defaultCaseStr);
		return SkillParser.createSkillFromEntry(defaultSkillEntry);
	}

	private static Vec3 parseDirection(String directionString) {
		String[] parts = directionString.split(",");
		double x = Double.parseDouble(parts[0].trim());
		double y = Double.parseDouble(parts[1].trim());
		double z = Double.parseDouble(parts[2].trim());
		return new Vec3(x, y, z);
	}

	private static Vec3 parseOffset(String offsetString) {
		String[] parts = offsetString.split(",");
		double x, y, z;
		if (parts.length == 1) {
			x = Double.parseDouble(parts[0].trim());
			y = Double.parseDouble(parts[0].trim());
			z = Double.parseDouble(parts[0].trim());
		} else {
			x = Double.parseDouble(parts[0].trim());
			y = Double.parseDouble(parts[1].trim());
			z = Double.parseDouble(parts[2].trim());
		}
		return new Vec3(x, y, z);
	}

	private static Vec3 parsePosition(String position) {
		try {
			String[] components = position.split(",");
			if (components.length != 3) {
				throw new IllegalArgumentException("Invalid position format: " + position);
			}

			double x = Double.parseDouble(components[0].trim());
			double y = Double.parseDouble(components[1].trim());
			double z = Double.parseDouble(components[2].trim());

			return new Vec3(x, y, z);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid position components in: " + position, e);
		}
	}

	public static List<Condition> parseConditions(List<String> conditionLines) {
		if (conditionLines == null || conditionLines.isEmpty()) return Collections.emptyList();

		List<Condition> conditions = new ArrayList<>();
		for (String line : conditionLines) {
			ConditionParser.ConditionType type = ConditionParser.parseConditionType(line);
			Map<String, Object> parameters = ConditionParser.parseParameters(line);

			Condition condition = ConditionFactory.createCondition(type.name().toLowerCase(Locale.ROOT), parameters);
			if (condition != null) conditions.add(condition);
		}
		return conditions;
	}

	public static List<Effect> parseEffects(List<String> effectLines) {
		List<Effect> effects = new ArrayList<>();
		for (String line : effectLines) {
			Effect effect = EffectFactory.createEffect(line);
			effects.add(effect);
		}
		return effects;
	}

	// Example usage
	public static void main(String[] args) {
		try {
			// Load the YAML file
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			Map<String, Map<String, Object>> yamlData = mapper.readValue(new File("skills.yml"), Map.class);

			for (Map.Entry<String, Map<String, Object>> skillEntry : yamlData.entrySet()) {
				String skillId = skillEntry.getKey();
				Map<String, Object> skillData = skillEntry.getValue();

				// Parse the cooldown
				double cooldown = (double) skillData.getOrDefault("Cooldown", 0.0);

				// Parse conditions if they exist
				List<Condition> conditions = skillData.containsKey("Conditions") ?
						ConditionParser.parseConditions((List<String>) skillData.get("Conditions"), true) : Collections.emptyList();
				List<Condition> targetConditions = skillData.containsKey("TargetConditions") ?
						ConditionParser.parseConditions((List<String>) skillData.get("TargetConditions"), false) : Collections.emptyList();
				List<Condition> triggerConditions = skillData.containsKey("TriggerConditions") ?
						ConditionParser.parseConditions((List<String>) skillData.get("TriggerConditions"), true) : Collections.emptyList();

				// Parse skills
				List<String> skillLines = (List<String>) skillData.get("Skills");
				List<SkillEntry> skillEntries = new ArrayList<>();
				for (String skillLine : skillLines) {
					skillEntries.add(SkillParser.parseSkillString(skillLine));
				}

				// Create Skill object with parsed data
				System.out.println("Skill ID: " + skillId);
				System.out.println("Cooldown: " + cooldown);
				if (conditions != null) {
					System.out.println("Conditions: " + conditions);
				}
				if (targetConditions != null) {
					System.out.println("TargetConditions: " + targetConditions);
				}
				if (triggerConditions != null) {
					System.out.println("TriggerConditions: " + triggerConditions);
				}
				System.out.println("Skills: " + skillEntries);

				// Here you would typically use this data to create a Skill instance
				// and add it to a skill manager or registry.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
