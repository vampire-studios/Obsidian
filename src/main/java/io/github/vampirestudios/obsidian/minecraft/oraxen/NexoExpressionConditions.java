package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A deliberately bounded, fail-closed evaluator for common Nexo SpEL conditions. */
public final class NexoExpressionConditions {
	private static final Pattern PERMISSION = Pattern.compile(
			"(?i)(?:#player\\.)?hasPermission\\(\\s*(['\"])(.*?)\\1\\s*\\)");
	private static final Pattern PLAYER_COUNT = Pattern.compile(
			"(?i)#server\\.(?:getOnlinePlayers\\(\\)|onlinePlayers)\\.size\\(\\)\\s*(==|!=|>=|<=|>|<)\\s*(\\d+)");
	private static final Pattern STRING_COMPARISON = Pattern.compile("(.+?)\\s*(==|!=)\\s*(['\"])(.*?)\\3");
	private static final Pattern NUMBER_COMPARISON = Pattern.compile("(.+?)\\s*(==|!=|>=|<=|>|<)\\s*(-?\\d+(?:\\.\\d+)?)");
	private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

	private NexoExpressionConditions() {
	}

	public static boolean passAll(MinecraftServer server, Player player, List<String> conditions) {
		if (conditions == null || conditions.isEmpty()) return true;
		for (String condition : conditions) {
			if (condition == null || condition.isBlank() || !test(server, player, condition)) return false;
		}
		return true;
	}

	public static boolean test(MinecraftServer server, Player player, String expression) {
		String value = stripOuterParentheses(expression.trim());
		int split = topLevelOperator(value, "||");
		if (split >= 0) return test(server, player, value.substring(0, split))
				|| test(server, player, value.substring(split + 2));
		split = topLevelOperator(value, "&&");
		if (split >= 0) return test(server, player, value.substring(0, split))
				&& test(server, player, value.substring(split + 2));
		if (value.startsWith("!")) return !test(server, player, value.substring(1));
		if ("true".equalsIgnoreCase(value)) return true;
		if ("false".equalsIgnoreCase(value)) return false;

		Matcher permission = PERMISSION.matcher(value);
		if (permission.matches()) return hasPermission(player, permission.group(2));

		Matcher playerCount = PLAYER_COUNT.matcher(value);
		if (playerCount.matches()) {
			return compare(server.getPlayerCount(), Integer.parseInt(playerCount.group(2)), playerCount.group(1));
		}
		Boolean serverPredicate = serverPredicate(server, value);
		if (serverPredicate != null) return serverPredicate;

		Boolean predicate = playerPredicate(player, value);
		if (predicate != null) return predicate;

		Matcher strings = STRING_COMPARISON.matcher(value);
		if (strings.matches()) {
			String actual = stringValue(server, player, strings.group(1).trim());
			if (actual != null) {
				boolean equal = actual.equalsIgnoreCase(strings.group(4));
				return "==".equals(strings.group(2)) ? equal : !equal;
			}
		}

		Matcher numbers = NUMBER_COMPARISON.matcher(value);
		if (numbers.matches()) {
			Double actual = numberValue(player, numbers.group(1).trim());
			if (actual != null) return compare(actual, Double.parseDouble(numbers.group(3)), numbers.group(2));
		}

		if (WARNED.add(expression)) {
			Obsidian.LOGGER.warn("Unsupported Nexo condition '{}'; the condition fails closed", expression);
		}
		return false;
	}

	private static Boolean playerPredicate(Player player, String expression) {
		String value = expression.replace("#player.", "").replace("()", "")
				.toLowerCase(Locale.ROOT);
		return switch (value) {
			case "issneaking", "iscrouching", "sneaking" -> player.isShiftKeyDown();
			case "issprinting", "sprinting" -> player.isSprinting();
			case "isswimming", "swimming" -> player.isSwimming();
			case "isunderwater", "underwater" -> player.isUnderWater();
			case "isinwater", "inwater" -> player.isInWater();
			case "isinlava", "inlava" -> player.isInLava();
			case "isonground", "onground" -> player.onGround();
			case "isflying", "flying" -> player.getAbilities().flying;
			case "iscreative", "creative" -> player.isCreative();
			case "isspectator", "spectator" -> player.isSpectator();
			case "isalive", "alive" -> player.isAlive();
			case "issleeping", "sleeping" -> player.isSleeping();
			case "isusingitem", "usingitem" -> player.isUsingItem();
			case "isop", "op" -> player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
			default -> null;
		};
	}

	private static Boolean serverPredicate(MinecraftServer server, String expression) {
		String value = expression.replace("#server.", "").replace("()", "")
				.replace("get", "").toLowerCase(Locale.ROOT);
		return switch (value) {
			case "allowend" -> server.getLevel(Level.END) != null;
			case "allownether" -> server.getLevel(Level.NETHER) != null;
			case "defaultgamemode", "defaultgametype" -> server.getDefaultGameType() != null;
			default -> null;
		};
	}

	private static String stringValue(MinecraftServer server, Player player, String accessor) {
		String key = accessor.replace("()", "").replace("get", "").toLowerCase(Locale.ROOT);
		if (key.equals("#player.world.name") || key.equals("#player.level.name")) {
			String levelName = server.getWorldData().getLevelName();
			if (player.level().dimension().equals(Level.OVERWORLD)) return levelName;
			if (player.level().dimension().equals(Level.NETHER)) return levelName + "_nether";
			if (player.level().dimension().equals(Level.END)) return levelName + "_the_end";
			return player.level().dimension().identifier().toString();
		}
		if (key.equals("#player.name") || key.equals("#player.scoreboardname")) {
			return player.getScoreboardName();
		}
		if (key.equals("#player.gamemode.name") && player instanceof ServerPlayer serverPlayer) {
			return serverPlayer.gameMode.getGameModeForPlayer().getName();
		}
		if (key.equals("#server.defaultgamemode") || key.equals("#server.defaultgametype")) {
			return server.getDefaultGameType().getName();
		}
		return null;
	}

	private static Double numberValue(Player player, String accessor) {
		String key = accessor.replace("#player.", "").replace("()", "")
				.replace("get", "").toLowerCase(Locale.ROOT);
		return switch (key) {
			case "health" -> (double) player.getHealth();
			case "foodlevel", "fooddata.foodlevel" -> (double) player.getFoodData().getFoodLevel();
			case "experiencelevel", "level" -> (double) player.experienceLevel;
			case "y", "location.y" -> player.getY();
			default -> null;
		};
	}

	private static boolean hasPermission(Player player, String permission) {
		Boolean apiResult = fabricPermission(player, permission);
		return apiResult != null ? apiResult : player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}

	/** Uses Fabric Permissions API when installed, without making it a hard dependency. */
	private static Boolean fabricPermission(Player player, String permission) {
		try {
			Class<?> permissions = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
			for (Method method : permissions.getMethods()) {
				if (!method.getName().equals("check") || !Modifier.isStatic(method.getModifiers())
						|| method.getParameterCount() != 3 || method.getParameterTypes()[1] != String.class) continue;
				Class<?> sourceType = method.getParameterTypes()[0];
				Object source = sourceType.isInstance(player) ? player
						: sourceType.isInstance(player.createCommandSourceStackForNameResolution((ServerLevel) player.level()))
						  ? player.createCommandSourceStackForNameResolution((ServerLevel) player.level())
						  : null;
				if (source == null) continue;
				Class<?> fallbackType = method.getParameterTypes()[2];
				Object fallback;
				if (fallbackType == boolean.class || fallbackType == Boolean.class) fallback = false;
				else if (fallbackType == int.class || fallbackType == Integer.class) fallback = 2;
				else continue;
				Object result = method.invoke(null, source, permission, fallback);
				if (result instanceof Boolean bool) return bool;
			}
		} catch (ClassNotFoundException ignored) {
			return null;
		} catch (ReflectiveOperationException | RuntimeException exception) {
			if (WARNED.add("fabric-permissions-api")) {
				Obsidian.LOGGER.warn("Could not query Fabric Permissions API: {}", exception.getMessage());
			}
		}
		return null;
	}

	private static boolean compare(double left, double right, String operator) {
		return switch (operator) {
			case "==" -> Double.compare(left, right) == 0;
			case "!=" -> Double.compare(left, right) != 0;
			case ">=" -> left >= right;
			case "<=" -> left <= right;
			case ">" -> left > right;
			case "<" -> left < right;
			default -> false;
		};
	}

	private static int topLevelOperator(String expression, String operator) {
		int depth = 0;
		char quote = 0;
		for (int index = 0; index <= expression.length() - operator.length(); index++) {
			char current = expression.charAt(index);
			if (quote != 0) {
				if (current == quote && (index == 0 || expression.charAt(index - 1) != '\\')) quote = 0;
				continue;
			}
			if (current == '\'' || current == '"') quote = current;
			else if (current == '(') depth++;
			else if (current == ')') depth--;
			else if (depth == 0 && expression.startsWith(operator, index)) return index;
		}
		return -1;
	}

	private static String stripOuterParentheses(String value) {
		while (value.startsWith("(") && value.endsWith(")")
				&& topLevelClosingParenthesis(value) == value.length() - 1) {
			value = value.substring(1, value.length() - 1).trim();
		}
		return value;
	}

	private static int topLevelClosingParenthesis(String value) {
		int depth = 0;
		char quote = 0;
		for (int index = 0; index < value.length(); index++) {
			char current = value.charAt(index);
			if (quote != 0) {
				if (current == quote && value.charAt(index - 1) != '\\') quote = 0;
				continue;
			}
			if (current == '\'' || current == '"') quote = current;
			else if (current == '(') depth++;
			else if (current == ')' && --depth == 0) return index;
		}
		return -1;
	}
}
