package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ObsInterpreter1 {
	private static final List<ScriptRule> RULES = new ArrayList<>();

	public static void installRules(List<ScriptRule> rules) {
		RULES.clear();
		RULES.addAll(rules);
	}

	public static void exec(MinecraftServer server, List<String> lines, ObsVars vars) {
		for (String raw : lines) {
			String line = interpolate(raw.trim(), vars);
			if (!line.endsWith(";")) line += ";";

			/* -------------------- PLAYER.* -------------------- */

			// p.msg("text");
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.msg\\(\"([^\"]*)\"\\);$")) {
				Matcher m = Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.msg\\(\"([^\"]*)\"\\);$")
						.matcher(line);
				if (m.matches()) {
					ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
					if (p != null) p.sendSystemMessage(Component.literal(m.group(2)));
					continue;
				}
				return;
			}

			// p.actionbar("text");
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.actionbar\\(\"([^\"]*)\"\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				if (p != null) p.sendSystemMessage(Component.literal(m.group(2)), true);
				continue;
			}

			// p.title("title", "subtitle", fi, stay, fo);
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.title\\(\"([^\"]*)\",\\s*\"([^\"]*)\"(?:,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+))?\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				String title = m.group(2), sub = m.group(3);
				int fi = gInt(m, 4, 10), stay = gInt(m, 5, 60), fo = gInt(m, 6, 10);
				if (p != null) {
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(sub)));
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(fi, stay, fo));
				}
				continue;
			}

			// p.give("id", count);
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.give\\(\"([^\"]*)\"\\);$")) {
				Matcher m = Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.give\\(\"([^\"]*)\"\\);$")
						.matcher(line);
				if (m.matches()) {
					ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
					Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(m.group(2)));
					int count = Integer.parseInt(m.group(3));
					if (p != null && item != null)
						p.getInventory().placeItemBackInInventory(new ItemStack(item, clamp(count, 1, 64)));
				}
				continue;
			}

			// p.take("id", count);
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.take\\(\"([^\"]+)\",\\s*(\\d+)\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				String id = m.group(2);
				int remaining = Integer.parseInt(m.group(3));
				if (p != null) {
					for (int i = 0; i < p.getInventory().getContainerSize() && remaining > 0; i++) {
						var stack = p.getInventory().getItem(i);
						if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id)) {
							int rm = Math.min(remaining, stack.getCount());
							stack.shrink(rm);
							remaining -= rm;
						}
					}
				}
				continue;
			}

			// p.effect("id", seconds[, amp]);
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.effect\\(\"([^\"]+)\",\\s*(\\d+)(?:,\\s*(\\d+))?\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				Holder<MobEffect> eff = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(m.group(2))).orElseThrow();
				int secs = Integer.parseInt(m.group(3));
				int amp = gInt(m, 4, 0);
				if (p != null && eff != null)
					p.addEffect(new MobEffectInstance(eff, secs * 20, Math.max(0, amp), false, true));
				continue;
			}

			// p.sound("id", vol, pitch);
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.sound\\(\"([^\"]+)\",\\s*([0-9.]+),\\s*([0-9.]+)\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				SoundEvent se = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(m.group(2)));
				float vol = Float.parseFloat(m.group(3)), pit = Float.parseFloat(m.group(4));
				if (p != null && se != null)
					p.level().playSound(null, p.blockPosition(), se, SoundSource.PLAYERS, vol, pit);
				continue;
			}

			// p.teleport(x, y, z[, yaw, pitch]);
			if (match(line, "^([A-Za-z_][A-Za-z0-9_]*)\\.teleport\\(([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+)(?:,\\s*([-0-9.]+),\\s*([-0-9.]+))?\\);$")) {
				var m = M(line);
				ServerPlayer p = (ServerPlayer) vars.get(m.group(1));
				double x = Double.parseDouble(m.group(2)), y = Double.parseDouble(m.group(3)), z = Double.parseDouble(m.group(4));
				Float yaw = m.group(5) != null ? Float.parseFloat(m.group(5)) : null;
				Float pitch = m.group(6) != null ? Float.parseFloat(m.group(6)) : null;
				if (p != null)
					p.teleportTo(p.level(), x, y, z, Set.of(), yaw != null ? yaw : p.getYRot(), pitch != null ? pitch : p.getXRot(), false);
				continue;
			}

			// player.heal(amount);
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.heal\\((\\d+)\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.heal\\((\\d+)\\);$").matcher(line);
				if (m.find()) { var p = (ServerPlayer) vars.get(m.group(1)); if (p!=null) p.heal(Float.parseFloat(m.group(2))); }
				continue;
			}

			// player.xp(amount); / player.level(amount);
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.xp\\((-?\\d+)\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.xp\\((-?\\d+)\\);$").matcher(line);
				if (m.find()) { var p = (ServerPlayer) vars.get(m.group(1)); if (p!=null) p.giveExperiencePoints(Integer.parseInt(m.group(2))); }
				continue;
			}
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.level\\((-?\\d+)\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.level\\((-?\\d+)\\);$").matcher(line);
				if (m.find()) { var p = (ServerPlayer) vars.get(m.group(1)); if (p!=null) p.giveExperienceLevels(Integer.parseInt(m.group(2))); }
				continue;
			}

			// player.gamemode("creative");
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.gamemode\\(\"([a-z_]+)\"\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.gamemode\\(\"([a-z_]+)\"\\);$").matcher(line);
				if (m.find()) {
					var p = (ServerPlayer) vars.get(m.group(1));
					if (p!=null) {
						var gm = switch(m.group(2)) {
							case "survival" -> net.minecraft.world.level.GameType.SURVIVAL;
							case "creative" -> net.minecraft.world.level.GameType.CREATIVE;
							case "adventure" -> net.minecraft.world.level.GameType.ADVENTURE;
							case "spectator" -> net.minecraft.world.level.GameType.SPECTATOR;
							default -> null;
						};
						if (gm!=null) p.setGameMode(gm);
					}
				}
				continue;
			}

			/* -------------------- INVENTORY.* -------------------- */

			// inventory.clear(); / inventory.has("id", count)
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.inventory\\.clear\\(\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.inventory\\.clear\\(\\);$").matcher(line);
				if (m.find()) { var p=(ServerPlayer)vars.get(m.group(1)); if (p!=null) p.getInventory().clearContent(); }
				continue;
			}
			if (line.matches("^([A-Za-z_][A-Za-z0-9_]*)\\.inventory\\.has\\(\"([^\"]+)\",\\s*(\\d+)\\);$")) {
				var m = java.util.regex.Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\.inventory\\.has\\(\"([^\"]+)\",\\s*(\\d+)\\);$").matcher(line);
				if (m.find()) {
					var p=(ServerPlayer)vars.get(m.group(1)); if (p!=null) {
						var item=BuiltInRegistries.ITEM.getValue(Identifier.parse(m.group(2)));
						int need=Integer.parseInt(m.group(3));
						int have=0; for (var s : p.getInventory().getNonEquipmentItems()) if (s.getItem()==item) have+=s.getCount();
						System.out.println("[OBS] inventory.has = " + (have>=need));
					}
				}
				continue;
			}

			/* -------------------- PLAYERS.* -------------------- */

			// players.broadcast("text");
			if (match(line, "^players\\.broadcast\\(\"([^\"]*)\"\\);$")) {
				var m = M(line);
				String msg = m.group(1);
				for (ServerPlayer p : server.getPlayerList().getPlayers()) p.sendSystemMessage(Component.literal(msg));
				continue;
			}

			// players.actionbar("text");
			if (match(line, "^players\\.actionbar\\(\"([^\"]*)\"\\);$")) {
				var m = M(line);
				String msg = m.group(1);
				for (ServerPlayer p : server.getPlayerList().getPlayers())
					p.sendSystemMessage(Component.literal(msg), true);
				continue;
			}

			// players.title("title", "subtitle", fi, stay, fo);
			if (match(line, "^players\\.title\\(\"([^\"]*)\",\\s*\"([^\"]*)\"(?:,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+))?\\);$")) {
				var m = M(line);
				String title = m.group(1), sub = m.group(2);
				int fi = gInt(m, 3, 10), stay = gInt(m, 4, 60), fo = gInt(m, 5, 10);
				for (ServerPlayer p : server.getPlayerList().getPlayers()) {
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(sub)));
					p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(fi, stay, fo));
				}
				continue;
			}

			/* -------------------- ENTITIES.* -------------------- */

			// entities.inWorld("id").ofType("id")[.within(n)].removeAll();
			if (match(line, "^entities\\.inWorld\\(\"([^\"]+)\"\\)\\.ofType\\(\"([^\"]+)\"\\)(?:\\.within\\((\\d+)\\))?\\.removeAll\\(\\);$")) {
				var m = M(line);
				ServerLevel lvl = world(server, m.group(1));
				String typeId = m.group(2);
				int radius = gInt(m, 3, 0);
				removeEntities(lvl, typeId, radius, vars);
				continue;
			}

			// entities.inWorld("id").ofType("id")[.within(n)].count();
			if (match(line, "^entities\\.inWorld\\(\"([^\"]+)\"\\)\\.ofType\\(\"([^\"]+)\"\\)(?:\\.within\\((\\d+)\\))?\\.count\\(\\);$")) {
				var m = M(line);
				ServerLevel lvl = world(server, m.group(1));
				String typeId = m.group(2);
				int radius = gInt(m, 3, 0);
				int count = countEntities(lvl, typeId, radius, vars);
				System.out.println("[OBS] entities.count = " + count);
				continue;
			}

			/* -------------------- WORLD.* -------------------- */

			// world.time();
			if (match(line, "^world\\.time\\(\\);$")) {
				long t = server.overworld().getOverworldClockTime() % 24000L;
				System.out.println("[OBS] world.time = " + t);
				continue;
			}

			// world.setTime(ticks);
			if (match(line, "^world\\.setTime\\((\\d+)\\);$")) {
				var m = M(line);
				long ticks = Long.parseLong(m.group(1));
				server.overworld().clockManager().setTotalTicks(server.overworld().dimensionType().defaultClock().orElseThrow(), ticks);
				continue;
			}

			// world.weather("clear|rain|thunder", seconds);
			if (match(line, "^world\\.weather\\(\"([a-z]+)\",\\s*(\\d+)\\);$")) {
				var m = M(line);
				String kind = m.group(1);
				int seconds = Integer.parseInt(m.group(2));
				setWeather(server.overworld(), kind, seconds);
				continue;
			}

			// world.spawn("entity_id", x, y, z);
			if (match(line, "^world\\.spawn\\(\"([^\"]+)\",\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+)\\);$")) {
				var m = M(line);
				String id = m.group(1);
				double x = Double.parseDouble(m.group(2));
				double y = Double.parseDouble(m.group(3));
				double z = Double.parseDouble(m.group(4));
				spawn(server.overworld(), id, x, y, z);
				continue;
			}

			// world.rule("doDaylightCycle", false);
//			if (line.matches("^world\\.rule\\(\"([A-Za-z0-9_]+)\",\\s*(true|false)\\);$")) {
//				var m = java.util.regex.Pattern.compile("^world\\.rule\\(\"([A-Za-z0-9_]+)\",\\s*(true|false)\\);$").matcher(line);
//				if (m.find()) {
//					var lvl = server.overworld();
//					var key = net.minecraft.world.level.GameRules.Key.create(m.group(1));
//					var gr = lvl.getGameRules().getRule(key);
//					if (gr != null) gr.setFrom(Boolean.parseBoolean(m.group(2)), server);
//				}
//				continue;
//			}

			// world.difficulty("hard");
			if (line.matches("^world\\.difficulty\\(\"(peaceful|easy|normal|hard)\"\\);$")) {
				var m = java.util.regex.Pattern.compile("^world\\.difficulty\\(\"(peaceful|easy|normal|hard)\"\\);$").matcher(line);
				if (m.find()) {
					var diff = switch (m.group(1)) {
						case "peaceful" -> net.minecraft.world.Difficulty.PEACEFUL;
						case "easy" -> net.minecraft.world.Difficulty.EASY;
						case "normal" -> net.minecraft.world.Difficulty.NORMAL;
						default -> net.minecraft.world.Difficulty.HARD;
					};
					server.setDifficulty(diff, true);
				}
				continue;
			}

			// world.particle("minecraft:happy_villager", x,y,z, dx,dy,dz, speed, count);
			if (line.matches("^world\\.particle\\(\"[^\"]+\",\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*[-0-9.]+,\\s*\\d+\\);$")) {
				var m = java.util.regex.Pattern.compile("^world\\.particle\\(\"([^\"]+)\",\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*(\\d+)\\);$").matcher(line);
//				if (m.find()) {
//					var lvl = server.overworld();
//					var type = BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.parse(m.group(1)));
//					if (type != null) lvl.sendParticles(type,
//							Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)),
//							Integer.parseInt(m.group(9)),
//							Double.parseDouble(m.group(5)), Double.parseDouble(m.group(6)), Double.parseDouble(m.group(7)),
//							Double.parseDouble(m.group(8)));
//				}
				continue;
			}

			// world.explosion(x,y,z, power);
			if (line.matches("^world\\.explosion\\(([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([0-9.]+)\\);$")) {
				var m = java.util.regex.Pattern.compile("^world\\.explosion\\(([-0-9.]+),\\s*([-0-9.]+),\\s*([-0-9.]+),\\s*([0-9.]+)\\);$").matcher(line);
				if (m.find()) {
					var lvl = server.overworld();
					lvl.explode(null,
							Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)),
							Float.parseFloat(m.group(4)),
							net.minecraft.world.level.Level.ExplosionInteraction.BLOCK);
				}
				continue;
			}

			/* -------------------- ADAPTERS .run(...) -------------------- */

			if (line.endsWith(".run();") || line.matches("^.+\\.run\\([^)]+\\);$")) {
				System.out.println("[OBS] run-adapter: " + line.replace(";", ""));
				continue;
			}

			System.out.println("[OBS] (skip) " + line);
		}
	}

	// Optional: evaluate simple rules when you choose to check them
	public static boolean testRule(MinecraftServer server, ServerPlayer ctxPlayer, String predicate) {
		if (predicate.contains("&&")) {
			for (String part : predicate.split("&&")) if (!testRule(server, ctxPlayer, part.trim())) return false;
			return true;
		}
		if (predicate.startsWith("biome.is(")) {
			String id = insideString(predicate);
			var key = ctxPlayer.level().getBiome(ctxPlayer.blockPosition()).unwrapKey().orElse(null);
			return key != null && key.identifier().toString().equals(id);
		}
		if (predicate.startsWith("player.itemInHand.is(")) {
			String id = insideString(predicate);
			var stack = ctxPlayer.getMainHandItem();
			var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
			return key != null && key.toString().equals(id);
		}
		return false;
	}

	/* -------------------- helpers -------------------- */

	private static boolean match(String s, String regex) {
		return s.matches(regex);
	}

	private static Matcher M(String s) {
		return Pattern.compile(".*").matcher(s);
	} // placeholder; we recompile per use

	private static int gInt(Matcher m, int group, int def) {
		try {
			return m.group(group) != null ? Integer.parseInt(m.group(group)) : def;
		} catch (Exception e) {
			return def;
		}
	}

	private static int clamp(int v, int lo, int hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	private static String interpolate(String s, ObsVars vars) {
		Matcher m = Pattern.compile("\\$\\{([^}]+)}").matcher(s);
		StringBuilder out = new StringBuilder();
		while (m.find()) {
			String key = m.group(1).trim();
			Object v = vars.get(key);
			if (v == null && key.endsWith(".name")) {
				Object base = vars.get(key.substring(0, key.length() - 5));
				if (base instanceof ServerPlayer sp) v = sp.getGameProfile().name();
			}
			m.appendReplacement(out, Matcher.quoteReplacement(String.valueOf(v)));
		}
		m.appendTail(out);
		return out.toString();
	}

	private static String insideString(String call) {
		var m = Pattern.compile("\\(\"([^\"]*)\"\\)").matcher(call);
		return m.find() ? m.group(1) : "";
	}

	private static ServerLevel world(MinecraftServer srv, String id) {
		var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(id));
		var lvl = srv.getLevel(key);
		return lvl != null ? lvl : srv.overworld();
	}

	private static void setWeather(ServerLevel serverWorld, String kind, int duration) {
		// vanilla-style: set rain/thunder timers; “seconds” isn’t a perfect 1:1 with these, but ok for MVP
		switch (kind) {
			case "clear" -> serverWorld.getWeatherData().setClearWeatherTime(duration * 20);
			case "rain" -> {
				serverWorld.getWeatherData().setRaining(true);
				serverWorld.getWeatherData().setRainTime(duration * 20);
			}
			case "thunder" -> {
				serverWorld.getWeatherData().setThundering(true);
				serverWorld.getWeatherData().setThunderTime(duration * 20);
			}
			default -> throw new IllegalStateException("Unexpected value: " + kind.toLowerCase());
		}
	}

	private static void spawn(ServerLevel lvl, String entityId, double x, double y, double z) {
		EntityType<?> type = EntityType.byString(entityId).orElse(null);
		if (type == null) return;
		Entity e = type.create(lvl, EntitySpawnReason.COMMAND);
		if (e == null) return;
		e.moveOrInterpolateTo(new Vec3(x, y, z), 0, 0);
		lvl.addFreshEntity(e);
	}

	private static void removeEntities(ServerLevel lvl, String typeId, int radius, ObsVars vars) {
		EntityType<?> type = EntityType.byString(typeId).orElse(null);
		if (lvl == null || type == null) return;

		BlockPos c = center(lvl, vars);
		int r = (radius <= 0 ? 32 : radius);

		var aabb = new net.minecraft.world.phys.AABB(
				c.getX()-r, c.getY()-r, c.getZ()-r,
				c.getX()+r, c.getY()+r, c.getZ()+r
		);

		Entity source = extractSource(vars);
		lvl.getEntities(source, aabb, e -> e.getType() == type).forEach(Entity::discard);
	}

	private static int countEntities(ServerLevel lvl, String typeId, int radius, ObsVars vars) {
		EntityType<?> type = EntityType.byString(typeId).orElse(null);
		if (lvl == null || type == null) return 0;

		BlockPos c = center(lvl, vars);
		int r = (radius <= 0 ? 32 : radius);

		var aabb = new net.minecraft.world.phys.AABB(
				c.getX()-r, c.getY()-r, c.getZ()-r,
				c.getX()+r, c.getY()+r, c.getZ()+r
		);

		Entity source = extractSource(vars);
		return lvl.getEntities(source, aabb, e -> e.getType() == type).size();
	}

	private static Entity extractSource(ObsVars vars) {
		Object s = vars.get("sender");
		if (s instanceof ServerPlayer sp) return sp;
		Object p = vars.get("player");
		if (p instanceof ServerPlayer sp2) return sp2;
		return null; // schedules etc, fall back to null
	}

	/** Center priority: sender (if present) → player (if present) → world spawn. */
	private static BlockPos center(ServerLevel lvl, ObsVars vars) {
		Object s = vars.get("sender");
		if (s instanceof ServerPlayer sp) return sp.blockPosition();
		Object p = vars.get("player");
		if (p instanceof ServerPlayer sp2) return sp2.blockPosition();
		return lvl.getRespawnData().pos();
	}
}
