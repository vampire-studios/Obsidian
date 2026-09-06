package io.github.vampirestudios.obsidian.scripting.std;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ObsPackRuntime {
	private static final int RULE_COOLDOWN_TICKS = 20; // 1 s; tweak

	private final Path scriptsRoot;
	private static final Map<String, ScriptEvent> events = new HashMap<>();
	// per-player, per-rule state (rising-edge + cooldown)
	private final Map<UUID, Map<Integer, RuleState>> ruleStates = new HashMap<>();
	private final List<ScriptCommand> commands = new ArrayList<>();
	private final List<ScriptSchedule> schedules = new ArrayList<>();
	private final List<ScriptRule> rules = new ArrayList<>();
	private final List<Task> tasks = new ArrayList<>();
	private final List<ScriptObservable> observables = new ArrayList<>();
	private final List<ScriptWatch> watches = new ArrayList<>();
	private StateStore state;
	private ConfigIO config;
	private MinecraftServer server;

	// 🔽 add: instance handle for external event fires (GUI, etc.)
	private static volatile ObsPackRuntime INSTANCE;

	public ObsPackRuntime(Path scriptsRoot) {
		this.scriptsRoot = scriptsRoot;
	}

	public int scriptCount() {
		return events.size() + commands.size() + schedules.size() + rules.size()
				+ observables.size() + watches.size();
	}

	public void loadAll() throws IOException {
		events.clear();
		commands.clear();
		schedules.clear();
		rules.clear();
		observables.clear();
		watches.clear();
		tasks.clear();
		ObsInterpreter.clearBodyCache();
		if (!Files.isDirectory(scriptsRoot)) return;

		try (var stream = Files.walk(scriptsRoot)) {
			stream.filter(p -> p.toString().endsWith(".obs")).forEach(p -> {
				try {
					var unit = ObsParser.parse(p, Files.readString(p));
					unit.events().forEach(e -> events.put(e.name(), e));
					commands.addAll(unit.commands());
					schedules.addAll(unit.schedules());
					rules.addAll(unit.rules());
					observables.addAll(unit.observables());
					watches.addAll(unit.watches());
					ObsInterpreter.installFunctions(new ArrayList<>(unit.functions()));
				} catch (Exception ex) {
					System.err.println("[OBS] " + p.getFileName() + ": " + ex.getMessage());
				}
			});
		}
		Path packRoot = scriptsRoot.getParent();        // <pack>/
		this.state = new StateStore(packRoot);
		this.config = new ConfigIO(packRoot);
		ObsInterpreter.attachStateStore(state);
		ObsInterpreter.attachConfigIO(config);
	}

	public void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(srv -> {
			this.server = srv;
			INSTANCE = this; // 🔽 expose active runtime instance
			ObsSuggest.ensureDefaults();        // if you use this
			initObservablesAndWatches();           // <— add this
			armSchedules(); // arm on startup
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(srv -> {
			tasks.clear();
		});

		ServerPlayConnectionEvents.INIT.register((handler, srv) -> {
			var ev = events.get("player.init");
			if (ev != null) {
				ObsInterpreter.execA(srv, ev.body(), vars(handler, ev));
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, srv) -> {
			var ev = events.get("player.join");
			if (ev != null) {
				ObsInterpreter.execA(srv, ev.body(), vars(handler, ev));
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, srv) -> {
			var ev = events.get("player.disconnect");
			if (ev != null) {
				ObsInterpreter.execA(srv, ev.body(), vars(handler, ev));
			}
		});
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (world.isClientSide()) return InteractionResult.PASS;
			return fireCancelResult("player.use.item",
					new ObsVars().define("player", player).define("hand", hand.name()));
		});

		AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
			if (world.isClientSide()) return InteractionResult.PASS;
			return fireCancelResult("player.attack.entity",
					new ObsVars().define("player", player).define("target", target));
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
			fire("player.respawn", new ObsVars().define("player", newP).define("old", oldP));
		});

		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((msg, sender, params) -> {
			return fireCancelable("player.chat", new ObsVars().define("player", sender)
					.define("message", msg.decoratedContent().getString()));
		});
		ServerMessageEvents.CHAT_MESSAGE.register((msg, sender, params) -> {
			fire("player.chat.post", new ObsVars().define("player", sender)
					.define("message", msg.decoratedContent().getString()));
		});
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClientSide()) return InteractionResult.PASS;
			var pos = hit.getBlockPos();
			return fireCancelResult("player.use.block",
					new ObsVars().define("player", player).define("pos", pos).define("face", hit.getDirection().name()));
		});
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, be) -> {
			return fireCancelable("player.break.block.before",
					new ObsVars().define("player", player).define("pos", pos).define("state", state));
		});
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, be) -> {
			fire("player.break.block.after",
					new ObsVars().define("player", player).define("pos", pos).define("state", state));
		});
		ServerTickEvents.END_SERVER_TICK.register(this::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, srv) -> {
//			if (handler.player != null) GuiBridge.closeAllFor(handler.player);
		});
	}

	private void fire(String name, ObsVars v) {
		var ev = events.get(name);
		if (ev != null) ObsInterpreter.execA(server, ev.body(), v);
	}

	private boolean fireCancelable(String name, ObsVars v) {
		var ev = events.get(name);
		if (ev == null) return true;
		v.put("__cancel__", false);
		ObsInterpreter.execA(server, ev.body(), v);
		Object c = v.get("__cancel__");
		return !(c instanceof Boolean b && b); // true = allow; false = cancel
	}

	// For Fabric callbacks that expect an ActionResult:
	private InteractionResult fireCancelResult(String name, ObsVars v) {
		return fireCancelable(name, v) ? InteractionResult.PASS : InteractionResult.FAIL;
	}

	/** Fire: gui.click(player, id, slot, click) */
	public static void fireGuiClick(ServerPlayer player, String id, int slot, String click) {
		var rt = INSTANCE;
		if (rt == null || rt.server == null) return;
		var ev = events.get("gui.click");
		if (ev == null) return;
		var v = vars(player, ev)
				.define("sender", player)
				.define("id", id)
				.define("slot", slot)
				.define("click", click);
		ObsInterpreter.execA(rt.server, ev.body(), v);
	}

	/** Fire: gui.open(player, id, rows) */
	public static void fireGuiOpen(ServerPlayer player, String id, int rows) {
		var rt = INSTANCE;
		if (rt == null || rt.server == null) return;
		var ev = events.get("gui.open");
		if (ev == null) return;
		var v = vars(player, ev)
				.define("sender", player)
				.define("id", id)
				.define("rows", rows);
		ObsInterpreter.execA(rt.server, ev.body(), v);
	}

	/** Fire: gui.close(player, id) */
	public static void fireGuiClose(ServerPlayer player, String id) {
		var rt = INSTANCE;
		if (rt == null || rt.server == null) return;
		var ev = events.get("gui.close");
		if (ev == null) return;
		var v = vars(player, ev)
				.define("sender", player)
				.define("id", id);
		ObsInterpreter.execA(rt.server, ev.body(), v);
	}

	public static ObsVars vars(Player player, ScriptEvent ev) {
		return new ObsVars().define(ev.playerVar(), player).define("player", player);
	}

	public static ObsVars vars(ServerGamePacketListenerImpl handler, ScriptEvent ev) {
		return vars(handler.player, ev);
	}

	public void armSchedules() {
		if (server == null) return;
		tasks.clear();
		for (var s : schedules) {
			int period = Math.max(1, (int) (s.every().toMillis() / 50L)); // ≥1 tick
			tasks.add(new Task(server.getTickCount() + period, period, () ->
					ObsInterpreter.execA(server, s.body(), new ObsVars())
			));
		}
	}

	public void tick(MinecraftServer srv) {
		// Resume any waits/continuations
		ObsInterpreter.tick(srv);

		int now = srv.getTickCount();
		for (var t : tasks) {
			if (now >= t.next) {
				try {
					t.run.run();
				} catch (Throwable ignored) {
				}
				t.next = now + t.period;
			}
		}
		if (state != null && now % (20 * 5) == 0) state.flush(); // every 5s
		if (config != null && now % 20 == 0) config.pollReload(); // every 5s

		// rules
		evalRules(srv, now);
	}

	public int reload() throws IOException {
		loadAll();
		if (server != null) {
			initObservablesAndWatches();
			armSchedules();
		}
		return scriptCount();
	}

	private void initObservablesAndWatches() {
		if (server == null) return;

		// 1) Observables (define + set initial value)
		for (var ob : observables) {
			try {
				var expr = Parser.parseExpr(ob.initExpr());
				Object init = ScriptUtils.eval(expr, Map.of());
				if (ob.scope() == ScriptObservable.Scope.GLOBAL) {
					ObsObservables.define(server, ob.name(), init);
				} else {
					// initialize for online players; late-joiners will lazily get defaults on first set
					for (ServerPlayer p : server.getPlayerList().getPlayers()) {
						ObsObservables.defineFor(server, p.getUUID(), ob.name(), init);
					}
				}
			} catch (Throwable t) {
				System.err.println("[OBS] observable " + ob.name() + ": " + t.getMessage());
			}
		}

		// 2) Watches (parse body → Stmt.Block and register)
		for (var w : watches) {
			try {
				var block = Parser.parse(String.join("\n", w.body()));

				if (w.scope() == ScriptObservable.Scope.GLOBAL) {
					// No player context; just fire on any change
					ObsObservables.watch(w.name(), (srv, name, oldV, newV) -> {
						var v = new ObsVars();
						v.put("name", name);
						v.put("old", oldV);
						v.put("value", newV);
						ObsInterpreter.exec(srv, block.stmts(), v);
					});
				} else {
					// PER-PLAYER with optional predicate
					final String pred = (w.when() == null || w.when().isBlank()) ? null : w.when();
					ObsObservables.watchPlayer(w.name(), (srv, who, name, oldV, newV) -> {
						var ply = srv.getPlayerList().getPlayer(who);
						if (ply == null) return;
						if (pred != null && !ObsInterpreter.testPredicate(ply, pred)) return;

						var v = new ObsVars();
						v.put("player", ply);
						v.put("sender", ply);
						v.put("name", name);
						v.put("old", oldV);
						v.put("value", newV);
						ObsInterpreter.exec(srv, block.stmts(), v);
					});
				}
			} catch (Throwable t) {
				System.err.println("[OBS] watch(" + w.name() + "): " + t.getMessage());
			}
		}
	}


	private void evalRules(MinecraftServer srv, int now) {
		if (rules.isEmpty()) return;
		var players = srv.getPlayerList().getPlayers();

		for (var p : players) {
			var per = ruleStates.computeIfAbsent(p.getUUID(), __ -> new HashMap<>());
			for (int idx = 0; idx < rules.size(); idx++) {
				var rule = rules.get(idx);
				boolean cur = ObsInterpreter.testPredicate(p, rule.when());
				var st = per.computeIfAbsent(idx, __ -> new RuleState());

				boolean shouldFire;
				if (rule.cadenceTicks() > 0) {
					// paced repeat while predicate true
					shouldFire = cur && (now - st.lastFire) >= Math.max(rule.cadenceTicks(), rule.cooldownTicks());
				} else {
					// rising edge with debounce
					shouldFire = cur && !st.last && (now - st.lastFire) >= rule.cooldownTicks();
				}

				if (shouldFire) {
					var vars = new ObsVars();
					vars.put("player", p);
					vars.put("sender", p);
					try {
						ObsInterpreter.execA(srv, rule.body(), vars);
					} catch (Throwable ex) {
						System.err.println("[OBS][rule] " + rule.when() + " -> " + ex.getMessage());
					}
					st.lastFire = now;
				}
				st.last = cur;
			}
		}
	}

	public static ScriptEvent getEvent(String event) {
		return events.get(event);
	}

	public void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			ObsSuggest.ensureDefaults();
			for (ScriptCommand cmd : commands) {
				ObsBrigadier.registerOne(dispatcher, cmd);
			}
		});
	}

	private static final class RuleState {
		boolean last;     // predicate last tick
		int lastFire;     // tick of last execute
	}

	private static final class Task {
		final Runnable run;
		int next, period;

		Task(int n, int p, Runnable r) {
			next = n;
			period = p;
			run = r;
		}
	}
}