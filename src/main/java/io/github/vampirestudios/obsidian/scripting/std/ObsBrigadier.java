package io.github.vampirestudios.obsidian.scripting.std;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public final class ObsBrigadier {
	// cooldowns: commandName -> playerUUID -> lastTick
	private static final Map<String, Map<UUID, Integer>> LAST_USE = new HashMap<>();

	private ObsBrigadier() {
	}

	/** Registers one script command: args, suggests, executes, cooldowns, aliases. */
	public static void registerOne(CommandDispatcher<CommandSourceStack> d, ScriptCommand c) {
		String rootName = c.name().startsWith("/") ? c.name().substring(1) : c.name();
		var root = Commands.literal(rootName);

		// validate: required-first
		boolean seenOptional = false;
		for (ScriptCommand.Param p : c.params()) {
			if (!p.required()) seenOptional = true;
			else if (seenOptional) {
				throw new IllegalStateException("Required param '" + p.name() + "' cannot follow an optional in " + c.name());
			}
		}

		// build chain
		List<ArgumentBuilder<CommandSourceStack, ?>> chain = new ArrayList<>();
		ArgumentBuilder<CommandSourceStack, ?> prev = root;

		for (int idx = 0; idx < c.params().size(); idx++) {
			ScriptCommand.Param p = c.params().get(idx);
			ArgumentBuilder<CommandSourceStack, ?> arg = toBrigArg(p);

			// suggests
			if (arg instanceof RequiredArgumentBuilder<?, ?> req) {
				applySuggests(c, p, (RequiredArgumentBuilder<CommandSourceStack, ?>) req);
			}

			prev.then(arg);         // <— chain to previous
			chain.add(arg);
			prev = arg;             // move forward
		}

		// 3) min required args
		int minArgs = 0;
		for (ScriptCommand.Param p : c.params()) {
			if (p.required()) minArgs++;
			else break;
		}

		// 4) attach executes on root (if allowed) and on EACH prefix node
		if (minArgs == 0) {
			root.executes(ctx -> runScriptCommand(ctx, c, 0));
		}
		for (int count = Math.max(1, minArgs); count <= chain.size(); count++) {
			final int finalCount = count;
			chain.get(count - 1).executes(ctx -> runScriptCommand(ctx, c, finalCount));
		}

		d.register(root);

		// aliases
		for (String alias : c.aliases()) {
			if (alias != null && !alias.isBlank()) {
				d.register(Commands.literal(alias).redirect(d.getRoot().getChild(rootName)));
			}
		}
	}

	private static void applySuggests(ScriptCommand c, ScriptCommand.Param p,
									  RequiredArgumentBuilder<CommandSourceStack, ?> req) {
		String provId = (c.suggests() != null) ? c.suggests().get(p.name()) : null;

		if (p.kind() == ScriptCommand.Kind.ENUM) {
			var spec = parseEnumSpec(p.defaultLiteral()); // "[a,b]||default"
			req.suggests((ctx, b) -> {
				// enum choices first
				spec.choices().forEach(b::suggest);
				// then provider, if any
				if (provId != null) {
					ObsSuggest.get(provId).ifPresent(sugg -> {
						try {
							sugg.getSuggestions(ctx, b);
						} catch (Exception ignored) {
						}
					});
				}
				return b.buildFuture();
			});
		} else if (provId != null) {
			ObsSuggest.get(provId).ifPresent(req::suggests);
		}
	}

	/* ===== runtime ===== */

	private static int runScriptCommand(CommandContext<CommandSourceStack> ctx, ScriptCommand c, int argCount) {
		var server = ctx.getSource().getServer();

		// cooldown (unchanged) — but don't force getPlayerOrException()
		ServerPlayer sender = ctx.getSource().getEntity() instanceof ServerPlayer sp ? sp : null;

		try {
			if (c.cooldownTicks() > 0 && sender != null) {
				var perCmd = LAST_USE.computeIfAbsent(c.name(), k -> new HashMap<>());
				int now = server.getTickCount();
				int last = perCmd.getOrDefault(sender.getUUID(), Integer.MIN_VALUE / 2);
				int left = c.cooldownTicks() - (now - last);
				if (left > 0) {
					sender.sendSystemMessage(Component.literal("⏳ " + Math.ceil(left / 20.0) + "s cooldown"), true);
					return 0;
				}
				perCmd.put(sender.getUUID(), now);
			}

			// collect provided args
			Map<String, Object> eventVars = new LinkedHashMap<>();
			for (int i = 0; i < argCount; i++) {
				ScriptCommand.Param pd = c.params().get(i);
				eventVars.put(pd.name(), readArg(ctx, pd, pd.name()));  // now Object
			}

			// fill defaults for omitted optional args (typed)
			for (int i = argCount; i < c.params().size(); i++) {
				ScriptCommand.Param pd = c.params().get(i);
				Object def = defaultValue(pd);
				if (def != null) eventVars.put(pd.name(), def);
			}
			var vars = new ObsVars();
			vars.put("source", ctx.getSource());
			if (sender != null) {
				vars.put("sender", sender);
				vars.put("player", sender);
				if (!eventVars.containsKey("target")) {
					eventVars.put("target", sender); // default target
				}
			} else {
				// Console: require explicit target if the script needs it
				if (!eventVars.containsKey("target")) {
					ctx.getSource().sendFailure(Component.literal("No player context. Specify [target]."));
					return 0;
				}
			}

			ObsInterpreter.execA(server, c.actions(), vars);
			return 1;
		} catch (Throwable t) {
			// show cause in chat and full stack in log
			String msg = (t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName());
			ctx.getSource().sendFailure(Component.literal("[obs] " + c.name() + ": " + msg));
			t.printStackTrace(); // or LOGGER.error("cmd {} failed", c.name(), t);
			return 0;
		}
	}

	/* ===== args & defaults ===== */

	private static ArgumentBuilder<CommandSourceStack, ?> toBrigArg(ScriptCommand.Param p) {
		return switch (p.kind()) {
			case INTEGER, TIME -> Commands.argument(p.name(), IntegerArgumentType.integer());
			case FLOAT -> Commands.argument(p.name(), FloatArgumentType.floatArg());
			case BOOL -> Commands.argument(p.name(), BoolArgumentType.bool());
			case WORD -> Commands.argument(p.name(), StringArgumentType.word());
			case TEXT -> Commands.argument(p.name(), StringArgumentType.greedyString());
			case DURATION -> Commands.argument(p.name(), StringArgumentType.word()); // parse to ticks later
			case COLOR -> Commands.argument(p.name(), ColorArgument.color());
			case ENTITY -> Commands.argument(p.name(), EntityArgument.entity());
			case ENTITIES -> Commands.argument(p.name(), EntityArgument.entities());
			case PLAYER -> Commands.argument(p.name(), EntityArgument.player());
			case PLAYERS -> Commands.argument(p.name(), EntityArgument.players());
			case GAME_MODE -> Commands.argument(p.name(), GameModeArgument.gameMode());
			case BLOCK_POS -> Commands.argument(p.name(), BlockPosArgument.blockPos());
			case UUID -> Commands.argument(p.name(), UuidArgument.uuid());
			case ROTATION -> Commands.argument(p.name(), RotationArgument.rotation());
			case ANGLE -> Commands.argument(p.name(), AngleArgument.angle());
			case SWIZZLE -> Commands.argument(p.name(), SwizzleArgument.swizzle());
			case VEC2 -> Commands.argument(p.name(), Vec2Argument.vec2());
			case VEC3 -> Commands.argument(p.name(), Vec3Argument.vec3());
			case ENUM -> {
				var spec = parseEnumSpec(p.defaultLiteral());
				yield Commands.argument(p.name(), StringArgumentType.word())
						.suggests((_, b) -> {
							spec.choices.forEach(b::suggest);
							return b.buildFuture();
						});
			}
		};
	}

	private static Object readArg(CommandContext<CommandSourceStack> ctx, ScriptCommand.Param pd, String name) throws CommandSyntaxException {
		return switch (pd.kind()) {
			case INTEGER, TIME -> IntegerArgumentType.getInteger(ctx, name);
			case FLOAT -> FloatArgumentType.getFloat(ctx, name);
			case BOOL -> BoolArgumentType.getBool(ctx, name);
			case WORD, TEXT, ENUM -> StringArgumentType.getString(ctx, name);
			case DURATION -> Util.parseDurTicks(StringArgumentType.getString(ctx, name)); // int
			case COLOR -> ColorArgument.getColor(ctx, name);                           // ChatFormatting / Color
			case ENTITY -> EntityArgument.getEntity(ctx, name);                         // Entity
			case ENTITIES ->
					EntityArgument.getEntities(ctx, name);                       // Collection<? extends Entity>
			case PLAYER -> EntityArgument.getPlayer(ctx, name);                         // ServerPlayer
			case PLAYERS -> EntityArgument.getPlayers(ctx, name);                        // Collection<ServerPlayer>
			case GAME_MODE -> GameModeArgument.getGameMode(ctx, name);
			case BLOCK_POS -> BlockPosArgument.getLoadedBlockPos(ctx, name);
			case UUID -> UuidArgument.getUuid(ctx, name);
			case ROTATION -> RotationArgument.getRotation(ctx, name).getRotation(ctx.getSource());
			case ANGLE -> AngleArgument.getAngle(ctx, name);
			case SWIZZLE -> SwizzleArgument.getSwizzle(ctx, name);
			case VEC2 -> Vec2Argument.getVec2(ctx, name);
			case VEC3 -> Vec3Argument.getVec3(ctx, name);
		};
	}

	private static Object defaultValue(ScriptCommand.Param pd) {
		String lit = pd.defaultLiteral();
		if (lit == null || lit.isBlank()) return null;
		return switch (pd.kind()) {
			case DURATION -> Util.parseDurTicks(lit);           // Integer
			case INTEGER -> Integer.parseInt(lit);
			case FLOAT -> Float.parseFloat(lit);
			case BOOL -> Boolean.parseBoolean(lit);
			case ENUM -> {
				var spec = parseEnumSpec(lit);
				yield spec.defaultValue != null ? spec.defaultValue
						: (spec.choices().isEmpty() ? null : spec.choices().getFirst());
			}
			default -> trimQuotes(lit);                   // String
		};
	}

	private static EnumSpec parseEnumSpec(String s) {
		if (s == null) return new EnumSpec(List.of(), null);
		String[] parts = s.split("\\|\\|", 2);
		String list = parts[0].trim();
		String def = (parts.length > 1) ? trimQuotes(parts[1].trim()) : null;
		if (list.startsWith("[") && list.endsWith("]")) list = list.substring(1, list.length() - 1);
		List<String> choices = Arrays.stream(list.split(","))
				.map(x -> trimQuotes(x.trim())).filter(x -> !x.isEmpty()).toList();
		return new EnumSpec(choices, def);
	}

	private static String trimQuotes(String s) {
		if (s == null) return null;
		String t = s.trim();
		if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) t = t.substring(1, t.length() - 1);
		return t;
	}

	private record EnumSpec(List<String> choices, String defaultValue) {
	}
}
