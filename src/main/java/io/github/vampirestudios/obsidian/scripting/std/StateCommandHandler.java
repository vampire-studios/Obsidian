package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static io.github.vampirestudios.obsidian.scripting.std.ObsInterpreter.STORE;

public class StateCommandHandler implements CommandHandler {
	private static final Logger LOGGER = LogManager.getLogger("StateCommandHandler");

	@Override
	public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
		for (CallChain.Segment s : segments) {
			switch (s.name()) {
				case "get" -> {
					Object who = ScriptUtils.eval(s.args().get(0), vars);
					String key = ScriptUtils.asStr(ScriptUtils.eval(s.args().get(1), vars));
					var map = resolvePlayerState(who, vars); // by Player/name/uuid
					vars.put("_last", map.getOrDefault(key, null));
				}
				case "set" -> {
					Object who = ScriptUtils.eval(s.args().get(0), vars);
					String key = ScriptUtils.asStr(ScriptUtils.eval(s.args().get(1), vars));
					Object val = ScriptUtils.eval(s.args().get(2), vars);
					var map = resolvePlayerState(who, vars);
					map.put(key, val);
					STORE.markDirty();
				}
				default -> LOGGER.warn("Unknown entities method: {}", s.name());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String,Object> resolvePlayerState(Object who, Map<String,Object> vars) {
		if (who instanceof ServerPlayer sp) return STORE.player(sp.getUUID().toString());
		String s = String.valueOf(who);
		// try UUID
		try { java.util.UUID.fromString(s); return STORE.player(s); } catch (Exception ignored) {}
		// fallback by name (online only)
		MinecraftServer srv = ((ServerPlayer)vars.get("sender")).getServer();
		for (var p : srv.getPlayerList().getPlayers())
			if (p.getGameProfile().getName().equalsIgnoreCase(s))
				return STORE.player(p.getUUID().toString());
		// as last resort: sender
		var me = (ServerPlayer) vars.get("sender");
		return STORE.player(me.getUUID().toString());
	}

	@Override
	public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
		LOGGER.warn("State method with block not supported for segments: {}", segments);
	}
}