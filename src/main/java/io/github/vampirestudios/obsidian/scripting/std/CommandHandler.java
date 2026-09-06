package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;

public interface CommandHandler {
	void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars);

	void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars);
}