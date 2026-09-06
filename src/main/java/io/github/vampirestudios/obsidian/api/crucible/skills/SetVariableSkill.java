package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.EntityVariableManager;
import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetVariableSkill extends Skill {
	private final String variableName;
	private final String valueExpression;

	public SetVariableSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, String variableName, String valueExpression) {
		super(skillId, target, trigger);
		this.variableName = variableName;
		this.valueExpression = valueExpression;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		// Resolve the value expression, which may contain variables and operators
		String resolvedValue = evaluateExpression(valueExpression, caster, target);
		EntityVariableManager.setEntityVariable(caster, variableName, resolvedValue);
	}

	private String evaluateExpression(String expression, LivingEntity caster, LivingEntity target) {
		// Parse and evaluate the expression. You may need to use a library or build a simple parser.
		// Replace variables with their values, then evaluate as needed.
		// For example: "<caster.var.moveset_index>+1" becomes "5+1", then evaluate to "6".
		String resolved = resolveVariables(expression, caster, target);
		return evaluateMathExpression(resolved);
	}


	private String resolveVariables(String expression, LivingEntity caster, LivingEntity target) {
		// Pattern to find placeholders like <caster.var.variable_name> or <target.var.variable_name>
		Pattern variablePattern = Pattern.compile("<(caster|target)\\.var\\.([a-zA-Z0-9_]+)>");
		Matcher matcher = variablePattern.matcher(expression);
		StringBuilder resolvedExpression = new StringBuilder();

		while (matcher.find()) {
			String entity = matcher.group(1); // "caster" or "target"
			String variableName = matcher.group(2);

			// Get the actual value of the variable
			String variableValue = getEntityVariableValue(entity.equals("caster") ? caster : target, variableName);

			// Replace the placeholder with the actual variable value
			matcher.appendReplacement(resolvedExpression, variableValue != null ? variableValue : "0");
		}
		matcher.appendTail(resolvedExpression);

		return resolvedExpression.toString();
	}

	private String getEntityVariableValue(LivingEntity entity, String variableName) {
		// Retrieve the variable value from the entity's variable manager (implement this method as needed)
		Object variableValue = EntityVariableManager.getEntityVariable(entity, variableName);
		return variableValue != null ? variableValue.toString() : "0";
	}


	private String evaluateMathExpression(String expression) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

		try {
			// Evaluate the expression and convert to string
			Object result = engine.eval(expression);
			return result != null ? result.toString() : "0";
		} catch (ScriptException e) {
			System.err.println("Error evaluating expression: " + expression);
			e.printStackTrace();
			return "0"; // Return a default value in case of error
		}
	}
}
