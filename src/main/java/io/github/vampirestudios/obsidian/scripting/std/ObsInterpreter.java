package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public final class ObsInterpreter {
	private static final Logger LOGGER = LogManager.getLogger("ObsInterpreter");
	private static final PriorityQueue<Cont> CONT = new PriorityQueue<>(Comparator.comparingInt(Cont::tick));
	private static final Map<String, ScriptFunc> FUNCS = new HashMap<>();
	private static final Map<String, CommandHandler> HANDLERS = new HashMap<>();
	private static final Map<String, List<Stmt>> BODY_CACHE = new LinkedHashMap<>(128, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, List<Stmt>> e) {
			return this.size() > 512;
		}
	};
	static StateStore STORE; // set from runtime after load
	private static ConfigIO CFG;

	static {
		HANDLERS.put("players", new PlayersCommandHandler());
		HANDLERS.put("world", new WorldCommandHandler());
		HANDLERS.put("entities", new EntitiesCommandHandler());
		HANDLERS.put("player", new PlayerCommandHandler());
		HANDLERS.put("state", new StateCommandHandler());
		HANDLERS.put("gui", new GuiCommandHandler());
		HANDLERS.put("observables", new ObservablesCommandHandler());
	}

	public static void attachConfigIO(ConfigIO io) {
		CFG = io;
	}

	public static void attachStateStore(StateStore s) {
		STORE = s;
	}

	public static void tick(MinecraftServer srv) {
		int now = srv.getTickCount();
		int size = CONT.size();
		for (int k = 0; k < size; k++) {
			var c = CONT.peek();
			if (c == null || c.tick > now) break;
			CONT.poll();
			exec(srv, c.rest, new ObsVars(c.vars));
		}
	}

	public static void installFunctions(Collection<ScriptFunc> defs) {
		FUNCS.clear();
		for (var f : defs) FUNCS.put(f.name(), f);
	}


	public static void clearBodyCache() {
		BODY_CACHE.clear();
	}

	public static ObsVars wrap(Map<String, Object> base) {
		return (base instanceof ObsVars ov) ? ov : new ObsVars(base == null ? Map.of() : base);
	}

	/* ---------- entry ---------- */
	public static void execA(MinecraftServer srv, List<String> sourceLines, ObsVars vars) {
		var stmts = compileBody(sourceLines);
		exec(srv, stmts, vars);
	}

	private static List<Stmt> compileBody(List<String> lines) {
		// Key by full source to avoid identity issues & hash collisions
		String src = String.join("\n", lines);
		List<Stmt> cached = BODY_CACHE.get(src);
		if (cached != null) return cached;
		var stmts = Parser.parse(src).stmts();
		BODY_CACHE.put(src, stmts);
		return stmts;
	}

	static void exec(MinecraftServer srv, List<Stmt> stmts, ObsVars vars) {
		for (int i = 0; i < stmts.size(); i++) {
			Stmt s = stmts.get(i);
			switch (s) {
				case Stmt.FuncCall(String name, List<Expr> args) -> {
					var f = FUNCS.get(name);
					if (f == null) throw new RuntimeException("Unknown function: " + name);
					var child = new ObsVars(vars);
					var ps = f.params();
					for (int a = 0; a < ps.size(); a++) {
						Object val = (a < args.size()) ? ScriptUtils.eval(args.get(a), vars) : null;
						child.put(ps.get(a), val);
					}
					exec(srv, f.bodyStmts(), child);
					continue;
				}
				case Stmt.Call(CallChain chain) -> {
					String name = chain.base();
					if ("__run__".equals(name)) {
						Runnable r = (Runnable) vars.get("__r__");
						if (r != null) r.run();
						continue;
					}
					if ("__again__".equals(name)) {
						int period = ((Number) vars.get("__period__")).intValue();
						scheduleRecurring(srv, period, (Runnable) vars.get("__r__"));
						continue;
					}
					evalCall(srv, chain, vars);
					continue;
				}
				case Stmt.CallWithBlock(CallChain chain, Stmt.Block body) -> {
					// method-with-block -> give the block a scope
					evalCallWithBlock(srv, chain, body, vars);
					continue;
				}
				case Stmt.Wait(int ticks) -> {
					int resume = srv.getTickCount() + ticks;
					var rest = new ArrayList<>(stmts.subList(i + 1, stmts.size()));
					CONT.add(new Cont(resume, rest, new ObsVars(vars)));
					return; // suspend
				}
				case Stmt.Repeat(int times, Stmt.Block body) -> {
					for (int n = 0; n < times; n++) {
						vars.pushScope();
						try {
							exec(srv, body.stmts(), vars);
						} finally {
							vars.popScope();
						}
					}
					continue;
				}
				case Stmt.WhileEvery(String predicate, int periodTicks, Stmt.Block body) -> {
					scheduleRecurring(srv, periodTicks, () -> {
						var p = contextPlayer(vars);
						if (p != null && testPredicate(p, predicate)) exec(srv, body.stmts(), vars);
					});
				}
				case Stmt.Let(String name, Expr value) -> {
					Object v = ScriptUtils.eval(value, vars);
					vars.set(name, v);
					continue;
				}
				case Stmt.Const(String name, Expr value) -> {
					Object v = ScriptUtils.eval(value, vars);
					vars.put(name, v);
					continue;
				}
				case Stmt.IfElse(Expr condition, Stmt.Block thenBlock, Stmt.Block elseBlock) -> {
					if (ScriptUtils.truthy(ScriptUtils.eval(condition, vars))) {
						vars.pushScope();
						try {
							exec(srv, thenBlock.stmts(), vars);
						} finally {
							vars.popScope();
						}
					} else {
						vars.pushScope();
						try {
							exec(srv, elseBlock.stmts(), vars);
						} finally {
							vars.popScope();
						}
					}
					continue;
				}
				case Stmt.Return(Expr value) -> {
					Object val = (value == null) ? null : ScriptUtils.eval(value, vars);
					throw new ReturnJump(val);
				}
				case Stmt.TryCatch(Stmt.Block body, String name, Stmt.Block handler) -> {
					try {
						exec(srv, body.stmts(), vars);
					} catch (Throwable ex) {
						var child = new ObsVars(vars);
						child.put(name, ex.getMessage() == null ? ex.toString() : ex.getMessage());
						exec(srv, handler.stmts(), child);
					}
					continue;
				}
				case Stmt.Persist ignored -> {
					STORE.markDirty();
					STORE.flush();
					continue;
				}
				case Stmt.StateAssign(Stmt.Target target, String key, Expr value) -> {
					Object v = ScriptUtils.eval(value, vars);
					if (target == Stmt.StateAssign.Target.GLOBAL) globals().put(key, v);
					else playerState(vars).put(key, v);
					STORE.markDirty();
					continue;
				}
				case Stmt.ConfigRead(String varName, String file) -> {
					try {
						vars.put(varName, CFG.read(file));
					} catch (IOException e) {
						throw new RuntimeException("config.read: " + e.getMessage());
					}
					continue;
				}
				case Stmt.ConfigWrite(String file, Expr expr) -> {
					Object v = ScriptUtils.eval(expr, vars);
					if (!(v instanceof Map<?, ?> m)) throw new RuntimeException("config.write expects object");
					try {
						@SuppressWarnings("unchecked") Map<String, Object> obj = (Map<String, Object>) m;
						CFG.write(file, obj);
					} catch (IOException e) {
						throw new RuntimeException("config.write: " + e.getMessage());
					}
					continue;
				}

				case Stmt.For(String var, Expr start, Expr end, Expr step, Stmt.Block body) -> {
					double startVal = ScriptUtils.asNum(ScriptUtils.eval(start, vars));
					double endVal = ScriptUtils.asNum(ScriptUtils.eval(end, vars));
					double stepVal = step != null ? ScriptUtils.asNum(ScriptUtils.eval(step, vars)) : 1.0;
					var child = new ObsVars(vars); // Scope loop variable
					for (double j = startVal; stepVal > 0 ? j <= endVal : j >= endVal; j += stepVal) {
						child.put(var, j);
						exec(srv, body.stmts(), child);
					}
				}

				case Stmt.Switch(
						Expr expr, List<Expr> caseValues, List<Stmt.Block> caseBlocks, Stmt.Block defaultBlock
				) -> {
					Object value = ScriptUtils.eval(expr, vars);
					for (int j = 0; j < caseValues.size(); j++) {
						if (ScriptUtils.eq(value, ScriptUtils.eval(caseValues.get(j), vars))) {
							exec(srv, caseBlocks.get(j).stmts(), vars);
							return;
						}
					}
					if (defaultBlock != null) {
						exec(srv, defaultBlock.stmts(), vars);
					}
				}
				case Stmt.Observable(String name, Expr init) -> {
					Object init1 = ScriptUtils.eval(init, vars);
					ObsObservables.define(srv, name, init1);
					continue;
				}
				case Stmt.Watch(String name, Stmt.Block body) -> {
					// Fire the script block whenever the GLOBAL observable 'name' changes
					ObsObservables.watch(name, (srv2, nm, oldV, newV) -> {
						var child = new ObsVars(vars);  // inherit current env
						child.define("name", nm);
						child.define("old", oldV);
						child.define("new", newV);
						try {
							exec(srv2, body.stmts(), child);
						} catch (Throwable t) {
							LOGGER.warn("watch '{}' handler failed: {}", nm, t.getMessage());
						}
					});
					continue;
				}
				case Stmt.Assign(String name, Expr value) -> {
					Object v = ScriptUtils.eval(value, vars);

					// Prefer per-player observable when present, else global observable, else local var
					var p = contextPlayer(vars);
					if (p != null && ObsObservables.hasFor(p.getUUID(), name)) {
						ObsObservables.set(srv, name, v);             // or setFor? see note below
						// If you intend per-player semantics:
						// ObsObservables.setFor(srv, p.getUUID(), name, v);
					} else if (ObsObservables.isObservable(name)) {
						ObsObservables.set(srv, name, v);
					} else {
						vars.put(name, v);
					}
					continue;
				}
				default -> throw new IllegalStateException("Unexpected value: " + s);
			}
		}
	}

	/* ---------- dispatch ---------- */
	private static void evalCall(MinecraftServer srv, CallChain cc, ObsVars vars) {
		String base = cc.base();
		CommandHandler handler = HANDLERS.get(base);
		if (handler != null) {
			handler.handleCall(srv, cc.segments(), vars);
		} else {
			// Assume base is a player variable
			handler = HANDLERS.get("player");
			handler.handleCall(srv, cc.segments(), new HashMap<>(vars) {{
				put("sender", vars.get(base));
			}});
		}
	}

	static void execScoped(MinecraftServer srv, Stmt.Block body, ObsVars vars) {
		vars.pushScope();
		try {
			exec(srv, body.stmts(), vars);
		} finally {
			vars.popScope();
		}
	}

	private static void evalCallWithBlock(
			MinecraftServer srv, CallChain cc, Stmt.Block body, ObsVars vars) {
		String base = cc.base();
		CommandHandler handler = HANDLERS.get(base);
		if (handler != null) {
			handler.handleCallWithBlock(srv, cc.segments(), body, vars);
		} else {
			// Assume base is a player variable
			handler = HANDLERS.get("player");
			handler.handleCallWithBlock(srv, cc.segments(), body, new HashMap<>(vars) {{
				put("sender", vars.get(base));
			}});
		}
	}

	/* ===== world/entity helpers ===== */
	private static void scheduleRecurring(MinecraftServer srv, int period, Runnable run) {
		// use CONT as a simple recurring queue
		int next = srv.getTickCount() + period;
		// Stmt that runs the runnable, then re-enqueues itself
		List<Stmt> payload = List.of(
				new Stmt.Call(new CallChain("__run__", List.of())),
				new Stmt.Wait(period),
				new Stmt.Call(new CallChain("__again__", List.of()))
		);
		ObsVars v = new ObsVars()
				.define("__period__", period)
				.define("__r__", run);

		CONT.add(new Cont(next, payload, v));
	}

	public static ServerPlayer contextPlayer(Map<String, Object> vars) {
		Object s = vars.get("sender");
		if (s instanceof ServerPlayer sp) return sp;
		Object p = vars.get("player");
		if (p instanceof ServerPlayer sp2) return sp2;
		return null;
	}

	/* ===== simple predicates (as before) ===== */
	public static boolean testPredicate(ServerPlayer p, String pred) {
		String s = pred.trim();
		s = ScriptUtils.stripOuterParens(s);
		if (s.startsWith("!")) return !testPredicate(p, s.substring(1).trim());

		int i = ScriptUtils.indexOfTopLevel(s, "&&");
		if (i >= 0) return testPredicate(p, s.substring(0, i)) && testPredicate(p, s.substring(i + 2));
		i = ScriptUtils.indexOfTopLevel(s, "||");
		if (i >= 0) return testPredicate(p, s.substring(0, i)) || testPredicate(p, s.substring(i + 2));

		/* === NEW: TAG PREDICATES === */

		// biome.tag("#namespace:biome_tag")
		if (s.startsWith("biome.tag(")) {
			String raw = insideString(s);
			TagKey<Biome> tag = TagKey.create(Registries.BIOME, ScriptUtils.rlFromHash(raw));
			var holder = p.level().getBiome(p.blockPosition());
			return holder.is(tag);
		}

		// player.itemInHand.tag("#namespace:item_tag")
		if (s.startsWith("player.itemInHand.tag(")) {
			String raw = insideString(s);
			TagKey<Item> tag = TagKey.create(Registries.ITEM, ScriptUtils.rlFromHash(raw));
			return p.getMainHandItem().is(tag);
		}

		// nearEntityTag("#namespace:entity_tag", radius)
//		if (s.startsWith("nearEntityTag(")) {
//			var a = ScriptUtils.insideArgs(s);
//			if (a.size() < 2) return false;
//			TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, ScriptUtils.rlFromHash(a.get(0)));
//			double r = ScriptUtils.parseDouble(a.get(1));
//			var pos = p.position();
//			var aabb = new net.minecraft.world.phys.AABB(
//					pos.x - r, pos.y - r, pos.z - r,
//					pos.x + r, pos.y + r, pos.z + r
//			);
//			return !p.level().getEntities(p, aabb, e -> e.getType().is(tag)).isEmpty();
//		}

		// dimension.tag("#namespace:dimension_type_tag")
		// checks the DIMENSION_TYPE tag of the current level
		if (s.startsWith("dimension.tag(")) {
			String raw = insideString(s);
			TagKey<DimensionType> tag = TagKey.create(Registries.DIMENSION_TYPE, ScriptUtils.rlFromHash(raw));
			var dimHolder = p.level().dimensionTypeRegistration(); // Holder<DimensionType>
			return dimHolder.is(tag);
		}

		// 3) function-style checks (existing + new)
		if (s.startsWith("biome.is(")) {
			String id = insideString(s);
			var key = p.level().getBiome(p.blockPosition()).unwrapKey().orElse(null);
			return key != null && key.identifier().toString().equals(id);
		}
		if (s.startsWith("dimension.is(")) {
			String id = insideString(s);
			var dim = p.level().dimension().identifier().toString();
			return dim.equals(id);
		}
		if (s.startsWith("player.itemInHand.is(")) {
			String id = insideString(s);
			var key = BuiltInRegistries.ITEM.getKey(p.getMainHandItem().getItem());
			return key != null && key.toString().equals(id);
		}
		if (s.startsWith("player.hasItem(")) {
			// "mod:thing"[, count]
			var args = ScriptUtils.insideArgs(s);
			String id = args.get(0);
			int need = (args.size() >= 2) ? (int) ScriptUtils.parseDouble(args.get(1)) : 1;
			int have = 0;
			var wanted = BuiltInRegistries.ITEM.get(Identifier.parse(id)).orElse(null);
			if (wanted == null) return false;
			for (var stack : p.getInventory().getNonEquipmentItems())
				if (stack.getItem() == wanted.value()) have += stack.getCount();
			return have >= need;
		}
//		if (s.startsWith("player.hasTag(")) {
//			String tag = insideString(s);
//			return p.getTags().contains(tag);
//		}
		if (s.startsWith("near(")) {
			// near(x,y,z,r)
			var a = ScriptUtils.insideArgs(s);
			double x = ScriptUtils.parseDouble(a.get(0)), y = ScriptUtils.parseDouble(a.get(1)), z = ScriptUtils.parseDouble(a.get(2));
			double r = ScriptUtils.parseDouble(a.get(3));
			return p.position().distanceTo(new net.minecraft.world.phys.Vec3(x, y, z)) <= r;
		}
//		if (s.startsWith("nearEntity(")) {
//			// nearEntity("minecraft:zombie", r)
//			var a = ScriptUtils.insideArgs(s);
//			String typeId = a.get(0);
//			double r = ScriptUtils.parseDouble(a.get(1));
//			var type = net.minecraft.world.entity.EntityType.(typeId).orElse(null);
//			if (type == null) return false;
//			var pos = p.position();
//			var aabb = new net.minecraft.world.phys.AABB(pos.x - r, pos.y - r, pos.z - r, pos.x + r, pos.y + r, pos.z + r);
//			return !p.level().getEntities(p, aabb, e -> e.getType() == type).isEmpty();
//		}
		if (s.startsWith("light.between(")) {
			// block light level at player’s feet
			var a = ScriptUtils.insideArgs(s);
			int lo = (int) ScriptUtils.parseDouble(a.get(0)), hi = (int) ScriptUtils.parseDouble(a.get(1));
			int L = p.level().getBrightness(net.minecraft.world.level.LightLayer.BLOCK, p.blockPosition());
			return (L >= lo && L <= hi);
		}
		if (s.startsWith("time.between(")) {
			// vanilla day time 0..23999
			var a = ScriptUtils.insideArgs(s);
			long t = p.level().getOverworldClockTime() % 24000L;
			long lo = (long) ScriptUtils.parseDouble(a.get(0)), hi = (long) ScriptUtils.parseDouble(a.get(1));
			return (lo <= hi) ? (t >= lo && t <= hi) : (t >= lo || t <= hi);
		}
		if (s.startsWith("weather.is(")) {
			String k = insideString(s);
			String w = p.level().isThundering() ? "thunder" : (p.level().isRaining() ? "rain" : "clear");
			return w.equals(k);
		}
		if (s.startsWith("team.is(")) {
			String name = insideString(s);
			var team = p.getTeam();
			return team != null && team.getName().equals(name);
		}
		if (s.startsWith("random.chance(")) {
			// accepts "25" or "25%"
			String lit = insideString(s).trim();
			if (lit.endsWith("%")) lit = lit.substring(0, lit.length() - 1).trim();
			double pct = ScriptUtils.parseDouble(lit) / 100.0;
			return p.getRandom().nextDouble() < Math.max(0.0, Math.min(1.0, pct));
		}
		if (s.startsWith("score(")) {
			// score(objective) <op> value   e.g., score(kills) >= 10
			// written as function: score("kills",">=",10)
			var a = ScriptUtils.insideArgs(s);
			String obj = a.get(0);
			String op = a.get(1);
			double rhs = ScriptUtils.parseDouble(a.get(2));
			var sb = p.level().getScoreboard();
			var objective = sb.getObjective(obj);
			if (objective == null) return false;
			int val = sb.getOrCreatePlayerScore(p, objective).get();
			return ScriptUtils.cmp(val, op, rhs);
		}

		// 4) numeric comparisons on common fields: player.y, player.health, player.level, player.food, player.saturation, player.x/z
		// e.g. "player.y >= 64", "player.health < 10"
		var m = ScriptUtils.CMP.matcher(s);
		if (m.matches()) {
			String lhs = m.group(1), op = m.group(2);
			double rhs = ScriptUtils.parseDouble(m.group(3));
			double lv = switch (lhs) {
				case "player.y" -> p.getY();
				case "player.x" -> p.getX();
				case "player.z" -> p.getZ();
				case "player.health" -> p.getHealth();
				case "player.level" -> p.experienceLevel;
				case "player.food" -> p.getFoodData().getFoodLevel();
				case "player.saturation" -> p.getFoodData().getSaturationLevel();
				default -> Double.NaN;
			};
			return !Double.isNaN(lv) && ScriptUtils.cmp(lv, op, rhs);
		}
		return false;
	}

	private static String insideString(String call) {
		int a = call.indexOf('"'), b = call.lastIndexOf('"');
		return (a >= 0 && b > a) ? call.substring(a + 1, b) : "";
	}

	public static Map<String, Object> globals() {
		return STORE.global();
	}

	public static Map<String, Object> playerState(Map<String, Object> vars) {
		var p = contextPlayer(vars);
		return (p == null) ? Map.of() : STORE.player(p.getUUID().toString());
	}

	private record Cont(int tick, List<Stmt> rest, ObsVars vars) {
	}

	private static final class ReturnJump extends RuntimeException {
		final Object value;

		ReturnJump(Object v) {
			this.value = v;
		}
	}
}
