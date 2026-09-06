package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.conditions.ConditionFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParser {

	// Main parsing function
	public static List<Condition> parseConditions(List<String> conditionLines, boolean isCasterCondition) {
		List<Condition> conditions = new ArrayList<>();
		Pattern pattern = Pattern.compile("([a-zA-Z_]+)\\{(.+?)\\}(?: (true|false))?");

		for (String line : conditionLines) {
			Matcher matcher = pattern.matcher(line.trim());

			if (matcher.matches()) {
				String typeString = matcher.group(1).toLowerCase();
				String paramsString = matcher.group(2);
				// Set expected outcome to `true` by default for conditions like variableEquals, variableIsSet
				boolean expectedOutcome = determineExpectedOutcome(matcher.group(3), typeString);

				Map<String, Object> parameters = parseParameters(paramsString);
				parameters.put("expectedOutcome", expectedOutcome); // Pass expected outcome as a parameter

				// Pass `isCasterCondition` as part of the parameters for later use
				parameters.put("applyToCaster", isCasterCondition);

				// The written name goes straight to the factory, which owns the alias table. Mapping it
				// through an enum here first threw away every alias but the enum's own name.
				Condition condition = ConditionFactory.createCondition(typeString, parameters);
				if (condition != null) {
					conditions.add(condition);
				} else {
					System.err.println("Unknown condition type: " + typeString);
				}
			}
		}
		return conditions;
	}

	// Helper to determine expected outcome, defaulting to `true` for certain conditions if not specified
	private static boolean determineExpectedOutcome(String outcomeGroup, String conditionType) {
		if (outcomeGroup != null) {
			return Boolean.parseBoolean(outcomeGroup);
		}
		// Default to true for conditions that naturally return true if set (like variableEquals or variableIsSet)
		return conditionType.equals("variableequals") || conditionType.equals("variableisset");
	}

	// Helper function to parse parameters
	static Map<String, Object> parseParameters(String paramsString) {
		if (paramsString.isBlank() || paramsString.isEmpty()) return Map.of();
		Map<String, Object> parameters = new HashMap<>();
		String[] paramPairs = paramsString.split(";");

		for (String param : paramPairs) {
			String[] keyValue = param.split("=");
			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();

				// Here you can parse values further based on the expected types
				if (value.matches("-?\\d+(\\.\\d+)?")) {
					parameters.put(key, Double.parseDouble(value));
				} else {
					parameters.put(key, value);
				}
			}
		}

		return parameters;
	}

}
