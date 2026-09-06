package io.github.vampirestudios.obsidian.api.scripting;

import io.github.vampirestudios.obsidian.api.events.PlayerPickupItemCallback;
import io.github.vampirestudios.obsidian.api.events.PlayerTickCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptManager {
	public final Map<String, ScriptParser.CommandDef> commandDefs = new HashMap<>();
	private final Map<String, List<Script>> byEvent = new HashMap<>();
	private final Map<String, String> vars = new HashMap<>();
	private final List<DelayedTask> delayedTasks = new ArrayList<>();
	private final List<ScriptParser.RecurringTaskDef> recurringDefs = new ArrayList<>();
	private final Map<ScriptParser.RecurringTaskDef, Integer> ticksLeft = new HashMap<>();
	private final Map<UUID, Boolean> lastSneakState = new HashMap<>();
	private final Map<UUID, Integer> lastHeldSlot = new HashMap<>();
	private final Map<UUID, Integer> lastXpLevel = new HashMap<>();
	private final Map<String, String> config = new HashMap<>();
	private final CommandExecutor executor = new CommandExecutor();
	private final Path scriptsDir;

	public ScriptManager(Path contentPackDir) {
		this.scriptsDir = contentPackDir.resolve("scripts1");

		// Register our tick handler to decrement & run delayed tasks
		ServerTickEvents.END_SERVER_TICK.register(this::onEndTick);
	}

	private void onEndTick(MinecraftServer server) {
		Iterator<DelayedTask> it = delayedTasks.iterator();
		while (it.hasNext()) {
			DelayedTask dt = it.next();
			if (--dt.ticks <= 0) {
				it.remove();
				dt.task.run();
			}
		}
		ServerLevel world = server.getLevel(Level.OVERWORLD);
		for (var def : List.copyOf(recurringDefs)) {
			int left = ticksLeft.merge(def, 0, (old, zero) -> old - 1);
			if (left <= 0) {
				// reset
				ticksLeft.put(def, def.intervalTicks());
				// run its commands (no player, world-based only)
				executeCommands(def.commands(), null, world, null, Collections.emptyMap());
			}
		}
	}

	public void loadAll() throws IOException {
		byEvent.clear();
		vars.clear();
		delayedTasks.clear();
		recurringDefs.clear();
		ticksLeft.clear();
		commandDefs.clear();
		try (var stream = Files.walk(scriptsDir)) {
			stream.filter(Files::isRegularFile)
					.filter(p -> p.toString().endsWith(".obs"))
					.forEach(this::loadFile);
		}
		// initialize counters
		for (var def : recurringDefs) {
			ticksLeft.put(def, def.intervalTicks());
		}
	}

	private void loadFile(Path file) {
		try {
			var result = ScriptParser.parseAll(file);
			// register event‐scripts as before:
			for (Script s : result.scripts)
				byEvent.computeIfAbsent(s.event(), k -> new ArrayList<>()).add(s);
			// collect recurring
			recurringDefs.addAll(result.recurring);
			// register commands
			for (var cmd : result.commands) {
				commandDefs.put(cmd.name(), cmd);
			}
			config.putAll(result.options);
		} catch (IOException e) {
			System.err.println("Failed to load script " + file + ": " + e.getMessage());
		}
	}

	/**
	 * Invoked by your Brigadier .executes(...) lambda.
	 *
	 * @param name      the command literal, e.g. "/warp"
	 * @param player    the player who ran it
	 * @param world     the world it ran in
	 * @param eventVars map of paramName -> stringified value
	 */
	public void runCommand(String name,
	                       Player player,
	                       ServerLevel world,
	                       Map<String, String> eventVars) {
		var def = commandDefs.get(name);
		if (def == null) return;
		// now just execute the script body,
		// your engine will interpolate %paramName% from eventVars
		executeCommands(def.body(), player, world, null, eventVars);
	}

	public void registerFabricEvents() {
		// PLAYER JOIN
		if (byEvent.containsKey("player join")) {
			ServerPlayConnectionEvents.JOIN.register((impl, _, server) -> {
				runScripts("player join", impl.player, server.overworld(), impl.player.blockPosition());
			});
		}

		// BLOCK BREAK
		if (byEvent.containsKey("block break")) {
			PlayerBlockBreakEvents.BEFORE.register((lvl, player, pos, state, _) -> {
				if (lvl instanceof ServerLevel world) {
					String blockId = BuiltInRegistries.BLOCK
							.getKey(state.getBlock()).toString();
					String toolId = player.getMainHandItem().getItem()
							.builtInRegistryHolder().key().identifier().toString();
					runScripts("block break", player, world, pos, Map.of(
							"block", blockId,
							"tool", toolId,
							"x", String.valueOf(pos.getX()),
							"y", String.valueOf(pos.getY()),
							"z", String.valueOf(pos.getZ())
					));
				}
				return true;
			});
		}

		if (byEvent.containsKey("block place")) {
			UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
				if (!world.isClientSide() && world instanceof ServerLevel sw) {
					runScripts("block place", player, sw, hit.getBlockPos());
				}
				return InteractionResult.PASS;
			});
		}

		if (byEvent.containsKey("right click")) {
			UseItemCallback.EVENT.register((player, world, hand) -> {
				if (!world.isClientSide() && world instanceof ServerLevel sw) {
					runScripts("right click", player, sw, null);
				}
				return InteractionResult.PASS;
			});
		}

		byEvent.keySet().stream()
				.filter(k -> k.startsWith("right click with "))
				.forEach(evt -> {
					String itemId = evt.substring("right click with ".length());
					UseItemCallback.EVENT.register((player, world, hand) -> {
						if (!world.isClientSide() && world instanceof ServerLevel w) {
							if (player.getItemInHand(hand).getItem()
									== BuiltInRegistries.ITEM.getValue(Identifier.tryParse(itemId))) {
								runScripts(evt, player, w, null);
							}
						}
						return InteractionResult.PASS;
					});
				});

		if (byEvent.containsKey("attack entity")) {
			AttackEntityCallback.EVENT.register((player, world, hand, target, hit) -> {
				if (!world.isClientSide() && world instanceof ServerLevel sw) {
					runScripts("attack entity", player, sw, null);
				}
				return InteractionResult.PASS;
			});
		}

		if (byEvent.containsKey("server tick")) {
			ServerTickEvents.END_SERVER_TICK.register(server -> {
				ServerLevel overworld = server.getLevel(Level.OVERWORLD);
				runScripts("server tick", null, overworld, null);
			});
		}

		if (byEvent.containsKey("chat")) {
			ServerMessageEvents.CHAT_MESSAGE.register((message, serverPlayer, _) -> {
				runScripts("chat", serverPlayer, serverPlayer.level(), null, Map.of("message", message.signedContent()));
			});
		}

		if (byEvent.containsKey("player death")) {
			ServerPlayerEvents.COPY_FROM.register((oldP, newP, alive) -> {
				if (!alive) runScripts("player death", oldP, oldP.level(), null);
			});
		}

		if (byEvent.containsKey("player respawn")) {
			ServerPlayerEvents.COPY_FROM.register((oldP, newP, alive) -> {
				if (alive) runScripts("player respawn", newP, newP.level(), null);
			});
		}

		if (byEvent.containsKey("entity spawn")) {
			ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
				runScripts("entity spawn", null, world, null);
			});
		}

		if (byEvent.containsKey("entity despawn")) {
			ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
				runScripts("entity despawn", null, world, entity.blockPosition());
			});
		}

		if (byEvent.containsKey("world load")) {
			ServerLifecycleEvents.SERVER_STARTED.register(server ->
					runScripts("world load", null, server.overworld(), null)
			);
		}

		if (byEvent.containsKey("world unload")) {
			ServerLifecycleEvents.SERVER_STOPPING.register(server ->
					runScripts("world unload", null, server.overworld(), null)
			);
		}

		if (byEvent.containsKey("pickup")) {
			PlayerPickupItemCallback.EVENT.register((player, item) -> {
				runScripts("pickup", player, player.level(), player.blockPosition(), Map.of(
						"item", BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString()
				));
				return InteractionResult.PASS;
			});
		}


		if (byEvent.containsKey("sneak toggle")) {
			PlayerTickCallback.EVENT.register(player -> {
				if (player.level().isClientSide()) return;
				UUID id = player.getUUID();
				boolean cur = player.isCrouching();
				boolean prev = lastSneakState.getOrDefault(id, false);
				if (cur != prev) {
					lastSneakState.put(id, cur);
					runScripts("sneak toggle", player, player.level(), null, Map.of("state", cur ? "down" : "up"));
				}
			});
		}

		// ENTITY KILL
		if (byEvent.containsKey("entity kill")) {
			ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killed, source) -> {
				if (killer instanceof Player player) {
					String entityType = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
					runScripts("entity kill", player, world, killed.blockPosition(), Map.of(
							"entity", entityType,
							"x", String.valueOf(killed.blockPosition().getX()),
							"y", String.valueOf(killed.blockPosition().getY()),
							"z", String.valueOf(killed.blockPosition().getZ())
					));
				}
			});
		}

		// PLAYER DAMAGE
		if (byEvent.containsKey("player damage")) {
			ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
				if (entity instanceof Player player) {
					runScripts("player damage", player, player.level(), null, Map.of(
							"damage", String.format("%.1f", damageTaken),
							"source", source.getMsgId(),
							"blocked", String.valueOf(blocked)
					));
				}
			});
		}

		// ITEM CHANGE (hotbar slot change)
		if (byEvent.containsKey("item change")) {
			PlayerTickCallback.EVENT.register(player -> {
				if (player.level().isClientSide()) return;
				UUID id = player.getUUID();
				int cur = player.getInventory().getSelectedSlot();
				int prev = lastHeldSlot.getOrDefault(id, -1);
				if (cur != prev) {
					lastHeldSlot.put(id, cur);
					String itemId = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
					runScripts("item change", player, player.level(), null, Map.of(
							"slot", String.valueOf(cur),
							"item", itemId
					));
				}
			});
		}

		// PLAYER LEVEL UP
		if (byEvent.containsKey("player level up")) {
			PlayerTickCallback.EVENT.register(player -> {
				if (player.level().isClientSide()) return;
				UUID id = player.getUUID();
				int cur = player.experienceLevel;
				Integer prev = lastXpLevel.get(id);
				if (prev != null && cur > prev) {
					runScripts("player level up", player, player.level(), null, Map.of(
							"level", String.valueOf(cur),
							"prev_level", String.valueOf(prev)
					));
				}
				lastXpLevel.put(id, cur);
			});
		}

		// PLAYER TICK (fires every server tick per player — use sparingly)
		if (byEvent.containsKey("player tick")) {
			PlayerTickCallback.EVENT.register(player -> {
				if (player.level().isClientSide()) return;
				runScripts("player tick", player, player.level(), player.blockPosition());
			});
		}
	}

	private void runScripts(String event,
	                        Player player,
	                        Level world,
	                        BlockPos pos,
	                        Map<String, String> eventVars) {
		List<Script> scripts = byEvent.get(event);
		if (scripts == null) return;
		for (Script s : scripts) {
			executeCommands(s.commands(), player, world, pos, eventVars);
		}
	}

	// keep the old signature for backwards-compat
	public void runScripts(String event,
	                       Player player,
	                       Level world,
	                       BlockPos pos) {
		runScripts(event, player, world, pos, Collections.emptyMap());
	}

	public int reload() throws IOException {
		loadAll();
		return byEvent.values().stream()
				.mapToInt(List::size)
				.sum();
	}

	private void executeCommands(List<String> cmds,
	                             Player player,
	                             Level world,
	                             BlockPos pos,
	                             Map<String, String> eventVars) {
		for (int i = 0; i < cmds.size(); i++) {
			String line = cmds.get(i).trim();

			if (line.isEmpty() || line.equals("{") || line.equals("}")) {
				continue;
			}

			// define FlowController for a potential `wait`
			int finalI = i;
			FlowController flow = ticks -> {
				var rest = new ArrayList<>(cmds.subList(finalI + 1, cmds.size()));
				delayedTasks.add(new DelayedTask(ticks, () ->
						executeCommands(rest, player, world, pos, eventVars)
				));
			};


			Map<String, String> vars = new HashMap<>();
			// 1) interpolate eventVars like %message%, %player%…
			for (var entry : eventVars.entrySet()) {
				vars.put("%" + entry.getKey() + "%", entry.getValue());
			}
			if (player != null) {
				vars.put("%player%", player.getName().getString());
				vars.put("%player's main item%", BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString());
				vars.put("%player's offhand item%", BuiltInRegistries.ITEM.getKey(player.getOffhandItem().getItem()).toString());
				vars.put("%player's health%", String.format("%.1f", player.getHealth()));
				vars.put("%player's max health%", String.format("%.1f", player.getMaxHealth()));
				vars.put("%player's level%", String.valueOf(player.experienceLevel));
				vars.put("%player's hunger%", String.valueOf(player.getFoodData().getFoodLevel()));
				vars.put("%player's x%", String.format("%.1f", player.getX()));
				vars.put("%player's y%", String.format("%.1f", player.getY()));
				vars.put("%player's z%", String.format("%.1f", player.getZ()));
				vars.put("%player's uuid%", player.getUUID().toString());
				vars.put("%player's name%", player.getName().getString());
			}

			// sort keys by length descending
			var keys = new ArrayList<>(vars.keySet());
			keys.sort((a, b) -> Integer.compare(b.length(), a.length()));

			// do the replacements
			for (String key : keys) {
				line = line.replace(key, vars.get(key));
			}

			if (line.startsWith("loop ") && line.contains(" in ")) {
				String[] parts = line.substring(5).split("\\s+in\\s+", 2);
				String varName = parts[0].trim();         // e.g. "blocks"
				String spec = parts[1].trim();         // e.g. "radius 3 around player"
				List<String> inner = collectBlock(cmds, ++i);

				if (spec.startsWith("radius")) {
					int r = Integer.parseInt(evalExpr(
							spec.substring("radius".length(),
									spec.indexOf("around player")).trim()));
					BlockPos center = player.blockPosition();
					for (int dx = -r; dx <= r; dx++) {
						for (int dz = -r; dz <= r; dz++) {
							BlockPos bp = center.offset(dx, 0, dz);
							String blockId = BuiltInRegistries.BLOCK
									.getKey(world.getBlockState(bp).getBlock())
									.toString();
							// bind both block‐id _and_ pos in one helper call
//							eventVars.put("")
							runLoopIteration(varName, bp, inner, player, world, bp, eventVars);
						}
					}
				} else if (spec.startsWith("{") && spec.endsWith("}")) {
					String list = spec.substring(1, spec.length() - 1);
					for (String elem : list.split("\\s*,\\s*")) {
						runLoopIteration(varName, elem, inner, player, world, pos, eventVars);
					}
				}
				continue;
			}

			// ── WHILE ────────────────────────────────────────────────────
			if (line.startsWith("while ") && line.endsWith("{")) {
				String condRaw = line.substring(6, line.length() - 1).trim();
				List<String> inner = collectBlock(cmds, ++i);
				// repeat as long as condition is true
				while (evalCondition(condRaw, player, world, pos, eventVars)) {
					executeCommands(inner, player, world, pos, eventVars);
				}
				continue;
			}

			// ── if ──
			if (line.startsWith("if ") || line.startsWith("elif ") || line.equals("else {")) {
				// gather all branches
				List<Branch> branches = new ArrayList<>();
				int j = i;
				do {
					String header = cmds.get(j).trim();
					String condRaw = null;
					if (header.startsWith("if ")) condRaw = header.substring(3, header.length() - 1).trim();
					else if (header.startsWith("elif ")) condRaw = header.substring(5, header.length() - 1).trim();
					boolean isElse = header.equals("else {");

					List<String> block = collectBlock(cmds, ++j);
					branches.add(new Branch(condRaw, block));
					j += block.size();  // skip the lines of the block

					// peek next
				} while (j < cmds.size() && (cmds.get(j).trim().startsWith("elif ")
						|| cmds.get(j).trim().equals("else {")));

				// advance i to after the entire chain
				i = j;

				// execute first matching branch
				for (Branch br : branches) {
					if (br.condRaw == null         // else branch
							|| evalCondition(br.condRaw, player, world, pos, eventVars)) {
						executeCommands(br.block, player, world, pos, eventVars);
						break;
					}
				}
				continue;
			}

			// ── set / add ──
			// "set X to Y" assigns a script variable; "set time/weather/..." flows to CommandExecutor
			if (line.startsWith("set ") && line.contains(" to ")) {
				var p = line.substring(4).split(" to ", 2);
				vars.put(p[0].trim(), evalExpr(p[1].trim()));
				continue;
			}
			if (line.startsWith("add ")) {
				var p = line.substring(4).split(" to ");
				int delta = Integer.parseInt(evalExpr(p[0].trim()));
				String cur = vars.getOrDefault(p[1].trim(), "0");
				vars.put(p[1].trim(), String.valueOf(Integer.parseInt(cur) + delta));
				continue;
			}

			// ── plain command (send, give, drop, broadcast, console, wait) ──
			boolean cont = executor.execute(line, player, world, pos, flow);
			if (!cont) return;  // `wait` hit: remainder scheduled by FlowController
		}
	}

	private void runLoopIteration(String varName,
	                              Object item,
	                              List<String> inner,
	                              Player player,
	                              Level world,
	                              BlockPos fallbackPos,
	                              Map<String, String> eventVars) {
		// bind the loop variable
		vars.put(varName, item.toString());

		// if it’s a BlockPos, also bind x/y/z and recurse at that pos
		if (item instanceof BlockPos bp) {
			vars.put(varName + "-x", String.valueOf(bp.getX()));
			vars.put(varName + "-y", String.valueOf(bp.getY()));
			vars.put(varName + "-z", String.valueOf(bp.getZ()));
			executeCommands(inner, player, world, bp, eventVars);
		} else {
			// non‐pos items (e.g. strings, numbers)
			executeCommands(inner, player, world, fallbackPos, eventVars);
		}
	}

	private String resolveToken(String token,
	                            Player player,
	                            Level world,
	                            BlockPos pos,
	                            Map<String, String> eventVars) {
		// strip quotes if present
		token = executor.stripQuotes(token);

		// config lookup: {@key}
		if (token.startsWith("@")) {
			String k = token.substring(1);
			return config.getOrDefault(k, "");
		}

		// 1) event-<key> → ev.get(key)
		if (token.startsWith("event-")) {
			String key = token.substring(6);            // strip "event-"
			return eventVars.getOrDefault(key, "");            // e.g. ev.get("message")
		}

		if (player != null && token.startsWith("player’s ")) {
			String prop = token.substring("player's ".length());
			return switch (prop) {
				case "x-coordinate", "x coordinate", "x" -> String.valueOf(player.getX());
				case "y-coordinate", "y coordinate", "y" -> String.valueOf(player.getY());
				case "z-coordinate", "z coordinate", "z" -> String.valueOf(player.getZ());
				case "health" -> String.valueOf(player.getHealth());
				case "max health", "max-health" -> String.valueOf(player.getMaxHealth());
				case "absorption" -> String.valueOf(player.getAbsorptionAmount());
				case "max absorption", "max-absorption" -> String.valueOf(player.getMaxAbsorption());
				case "air supply", "air-supply" -> String.valueOf(player.getAirSupply());
				case "max air supply", "max-air-supply" -> String.valueOf(player.getMaxAirSupply());
				case "hunger" -> String.valueOf(player.getFoodData().getFoodLevel());
				case "saturation" -> String.valueOf(player.getFoodData().getSaturationLevel());
				case "dimension" -> player.level().dimension().identifier().toString();
				case "world" -> player.level().getServer().getWorldData().getLevelName();
				case "level", "xp-level" -> String.valueOf(player.experienceLevel);
				case "xp", "experience" -> String.valueOf(player.totalExperience);
				case "game_mode", "gamemode", "game-mode" -> {
					if (player instanceof net.minecraft.server.level.ServerPlayer sp)
						yield sp.gameMode.getGameModeForPlayer().getName();
					yield "unknown";
				}
				case "uuid" -> player.getUUID().toString();
				case "name" -> player.getName().getString();
				case "sneaking", "is-sneaking", "crouching" -> String.valueOf(player.isCrouching());
				case "sprinting", "is-sprinting" -> String.valueOf(player.isSprinting());
				case "on_ground", "on-ground" -> String.valueOf(player.onGround());
				case "flying", "is-flying" -> String.valueOf(player.getAbilities().flying);
				default -> token;
			};
		}
		if (world != null && token.startsWith("world’s ")) {
			String prop = token.substring("world's ".length());
			return switch (prop) {
				case "time" -> String.valueOf((int) (world.getOverworldClockTime() % 24000));
				case "day", "is day" -> String.valueOf(world.getOverworldClockTime() % 24000 < 12000);
				case "weather" -> {
					if (world.isThundering()) yield "thunder";
					if (world.isRaining()) yield "rain";
					yield "clear";
				}
				case "seed" -> String.valueOf(world.getServer().overworld().getSeed());
				case "biome" -> {
					var bm = world.getBiome(player.blockPosition()).value();
					yield world.registryAccess().getOrThrow(Registries.BIOME).value().getKey(bm).toString();
				}
				default -> token;
			};
		}

		// 1) block lookup
		if (token.equals("block") && world != null && pos != null) {
			return BuiltInRegistries.BLOCK
					.getKey(world.getBlockState(pos).getBlock())
					.toString();  // “minecraft:oak_log”
		}
		// 2) eventVars
		if (eventVars.containsKey(token)) {
			return eventVars.get(token);
		}
		// 3) DSL vars (keys stored without %)
		if (vars.containsKey(token)) {
			return vars.get(token);
		}
		// 4) fallback to your expression evaluator
		return evalExpr(token);
	}

	private List<String> collectBlock(List<String> cmds, int startIndex) {
		List<String> block = new ArrayList<>();
		int depth = 1;
		for (int j = startIndex; j < cmds.size(); j++) {
			String line = cmds.get(j).trim();
			if (line.endsWith("{")) {
				depth++;
			} else if (line.equals("}")) {
				depth--;
				if (depth == 0) {
					break;
				}
			}
			block.add(line);
		}
		return block;
	}

	private String evalExpr(String expr) {
		Matcher m = Pattern.compile("\\{([^}]+)}").matcher(expr);
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			String v = vars.getOrDefault("{" + m.group(1) + "}", "0");
			m.appendReplacement(sb, v);
		}
		m.appendTail(sb);
		String s = sb.toString();

		// simple arithmetic: a<op>b
		if (s.matches("\\d+\\s*[+\\-*/]\\s*\\d+")) {
			String[] parts = s.split("\\s*([+\\-*/])\\s*");
			int a = Integer.parseInt(parts[0]), b = Integer.parseInt(parts[1]);
			char op = s.replaceAll("\\d+", "").trim().charAt(0);
			int r = switch (op) {
				case '+' -> a + b;
				case '-' -> a - b;
				case '*' -> a * b;
				case '/' -> a / b;
				default -> 0;
			};
			return String.valueOf(r);
		}
		return s;
	}

	// helper to evaluate any boolean expression (block, vars, numeric ops)
	private boolean evalCondition(String condRaw,
	                              Player player,
	                              Level world,
	                              BlockPos pos,
	                              Map<String, String> eventVars) {
		// reuse your resolveToken + comparison logic
		String[] parts = condRaw.split("\\s+");
		String lhs = resolveToken(parts[0], player, world, pos, eventVars);
		String op = parts[1];
		String rhs = executor.stripQuotes(resolveToken(parts[2], player, world, pos, eventVars));

		return switch (op) {
			case "==" -> lhs.equals(rhs);
			case "!=" -> !lhs.equals(rhs);
			case ">" -> Integer.parseInt(lhs) > Integer.parseInt(rhs);
			case "<" -> Integer.parseInt(lhs) < Integer.parseInt(rhs);
			case ">=" -> Integer.parseInt(lhs) >= Integer.parseInt(rhs);
			case "<=" -> Integer.parseInt(lhs) <= Integer.parseInt(rhs);
			default -> false;
		};
	}

	@FunctionalInterface
	public interface FlowController {
		void delay(int ticks);
	}

	// small data class for branches
	private record Branch(String condRaw, List<String> block) {
	}

	private static class DelayedTask {
		final Runnable task;
		int ticks;

		DelayedTask(int ticks, Runnable task) {
			this.ticks = ticks;
			this.task = task;
		}
	}
}
