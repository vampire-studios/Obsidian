package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PlayersCommandHandler implements CommandHandler {
	private static final Logger LOGGER = LogManager.getLogger("PlayersCommandHandler");

	@Override
	public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
		if (segments.isEmpty()) {
			LOGGER.warn("No method specified for players call");
			return;
		}
		CallChain.Segment segment = segments.getFirst();
		try {
			switch (segment.name()) {
				case "broadcast" -> broadcast(server, ScriptUtils.getStringArg(segment, 0, vars));
				case "actionbar" -> actionBar(server, ScriptUtils.getStringArg(segment, 0, vars));
				case "title" -> sendTitle(server, segment, vars);
				case "kick" -> kick(server, ScriptUtils.getStringArg(segment, 0, vars));
				case "count" -> count(server, vars);
				case "named" -> {
					String who = ScriptUtils.getStringArg(segment, 0, vars);
					ServerPlayer p = findByName(server, who);
					vars.put("_last", p); // _last is a general “result” convention you already use
				}
				case "onlineNames" -> {
					String s = server.getPlayerList().getPlayers().stream()
							.map(p -> p.getGameProfile().name())
							.collect(java.util.stream.Collectors.joining(", "));
					vars.put("_last", s);
				}
				default -> LOGGER.warn("Unknown players method: {}", segment.name());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error executing players method {}: {}", segment.name(), e.getMessage());
		}
	}

	@Override
	public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
		if (segments.isEmpty()) {
			LOGGER.warn("No method specified for players call with block");
			return;
		}
		CallChain.Segment segment = segments.getFirst();
		if (segment.name().equals("each")) {
			String varName = segment.args().isEmpty() ? "p" : ScriptUtils.asStr(ScriptUtils.eval(segment.args().getFirst(), vars));
			String predicate = segment.args().size() >= 2 ? ScriptUtils.getStringArg(segment, 1, vars) : "";
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (predicate.isEmpty() || ObsInterpreter.testPredicate(player, predicate)) {
					var newVars = new ObsVars(vars).define(varName, player);
					ObsInterpreter.execScoped(server, body, newVars);
				}
			}
		} else if (segment.name().equals("withTag")) {
			String varName = segment.args().isEmpty() ? "p" : ScriptUtils.asStr(ScriptUtils.eval(segment.args().get(0), vars));
			String tag = segment.args().size() >= 2 ? ScriptUtils.getStringArg(segment, 1, vars) : "";
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				if (tag.isEmpty() || p.getTags().contains(tag)) {
					var newVars = new ObsVars(vars).define(varName, p);
					ObsInterpreter.execScoped(server, body, newVars);
				}
			}
			return;
		} else {
			LOGGER.warn("Unknown players method with block: {}", segment.name());
		}
	}

	private void broadcast(MinecraftServer server, String message) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.sendSystemMessage(Component.literal(message));
		}
	}

	private void actionBar(MinecraftServer server, String message) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.displayClientMessage(Component.literal(message), true);
		}
	}

	private void sendTitle(MinecraftServer server, CallChain.Segment segment, Map<String, Object> vars) {
		String title = ScriptUtils.getStringArg(segment, 0, vars);
		String subtitle = segment.args().size() >= 2 ? ScriptUtils.getStringArg(segment, 1, vars) : "";
		int fadeIn = segment.args().size() >= 3 ? (int) ScriptUtils.getNumberArg(segment, 2, vars) : 10;
		int stay = segment.args().size() >= 4 ? (int) ScriptUtils.getNumberArg(segment, 3, vars) : 60;
		int fadeOut = segment.args().size() >= 5 ? (int) ScriptUtils.getNumberArg(segment, 4, vars) : 10;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!title.isEmpty()) {
				player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
			}
			if (!subtitle.isEmpty()) {
				player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
			}
			player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
		}
	}

	private void kick(MinecraftServer server, String reason) {
		if (reason.isEmpty()) {
			reason = "Kicked by script";
		}
		Component kickReason = Component.literal(reason);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.disconnect(kickReason);
		}
	}

	private void count(MinecraftServer server, Map<String, Object> vars) {
		int playerCount = server.getPlayerList().getPlayers().size();
		vars.put("_last", playerCount);
		LOGGER.debug("players.count = {}", playerCount);
	}

	private ServerPlayer findByName(MinecraftServer server, String name) {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			if (p.getGameProfile().name().equalsIgnoreCase(name)) return p;
		}
		return null;
	}
}
