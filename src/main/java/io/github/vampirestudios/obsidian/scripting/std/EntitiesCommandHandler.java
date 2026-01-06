package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static io.github.vampirestudios.obsidian.scripting.std.ObsInterpreter.contextPlayer;

public class EntitiesCommandHandler implements CommandHandler {
	private static final Logger LOGGER = LogManager.getLogger("EntitiesCommandHandler");

	@Override
	public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
		String dim = "minecraft:overworld";
		String typeId = "minecraft:item";
		int radius = 32;
		for (CallChain.Segment s : segments) {
			switch (s.name()) {
				case "inWorld" -> dim = ScriptUtils.getStringArg(s, 0, vars);
				case "ofType" -> typeId = ScriptUtils.getStringArg(s, 0, vars);
				case "within" -> radius = (int) ScriptUtils.getNumberArg(s, 0, vars);
				case "removeAll" -> {
					doRemove(server, dim, typeId, radius, vars);
					return;
				}
				case "count" -> {
					int c = doCount(server, dim, typeId, radius, vars);
					LOGGER.info("entities.count = {}", c);
					return;
				}
				default -> LOGGER.warn("Unknown entities method: {}", s.name());
			}
		}
	}

	@Override
	public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
		LOGGER.warn("Entities method with block not supported for segments: {}", segments);
	}

	private void doRemove(MinecraftServer srv, String dim, String typeId, int radius, Map<String, Object> vars) {
		ServerLevel lvl = world(srv, dim);
		EntityType<?> type = EntityType.byString(typeId).orElse(null);
		if (lvl == null || type == null) return;
		BlockPos c = center(lvl, vars);
		int r = Math.max(1, radius);
		var aabb = new net.minecraft.world.phys.AABB(c.getX() - r, c.getY() - r, c.getZ() - r, c.getX() + r, c.getY() + r, c.getZ() + r);
		lvl.getEntities(source(vars), aabb, e -> e.getType() == type).forEach(Entity::discard);
	}

	private int doCount(MinecraftServer srv, String dim, String typeId, int radius, Map<String, Object> vars) {
		ServerLevel lvl = world(srv, dim);
		EntityType<?> type = EntityType.byString(typeId).orElse(null);
		if (lvl == null || type == null) return 0;
		BlockPos c = center(lvl, vars);
		int r = Math.max(1, radius);
		var aabb = new net.minecraft.world.phys.AABB(c.getX() - r, c.getY() - r, c.getZ() - r, c.getX() + r, c.getY() + r, c.getZ() + r);
		return lvl.getEntities(source(vars), aabb, e -> e.getType() == type).size();
	}

	private BlockPos center(ServerLevel lvl, Map<String, Object> vars) {
		ServerPlayer s = contextPlayer(vars);
		return s != null ? s.blockPosition() : lvl.getRespawnData().pos();
	}

	private Entity source(Map<String, Object> vars) {
		return contextPlayer(vars);
	}

	private ServerLevel world(MinecraftServer srv, String id) {
		var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, Identifier.parse(id));
		var lvl = srv.getLevel(key);
		return lvl != null ? lvl : srv.overworld();
	}
}