package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WorldCommandHandler implements CommandHandler {
	private static final Logger LOGGER = LogManager.getLogger("WorldCommandHandler");

	@Override
	public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
		if (segments.isEmpty()) {
			LOGGER.warn("No method specified for world call");
			return;
		}
		ServerLevel level = server.overworld();
		CallChain.Segment segment = segments.getFirst();
		try {
			switch (segment.name()) {
				case "time" -> LOGGER.info("world.time = {}", level.getDayTime() % 24000L);
				case "setTime" -> setTime(level, segment, vars);
				case "weather" -> setWeather(level, segment, vars);
				case "spawn" -> spawn(level, segment, vars);
				case "difficulty" -> setDifficulty(server, segment, vars);
				case "explosion" -> createExplosion(level, segment, vars);
				case "setBlock" -> setBlock(level, segment, vars);
				case "fill" -> fill(level, segment, vars);
				case "particle" -> spawnParticle(level, segment, vars);
				default -> LOGGER.warn("Unknown world method: {}", segment.name());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error executing world method {}: {}", segment.name(), e.getMessage());
		}
	}

	@Override
	public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
		LOGGER.warn("World method with block not supported for segments: {}", segments);
	}

	private void setTime(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 1) {
			LOGGER.warn("setTime requires time value");
			return;
		}
		level.setDayTime((long) ScriptUtils.getNumberArg(segment, 0, vars));
	}

	private void setWeather(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 2) {
			LOGGER.warn("weather requires kind and duration");
			return;
		}
		String kind = ScriptUtils.getStringArg(segment, 0, vars);
		int seconds = (int) ScriptUtils.getNumberArg(segment, 1, vars);
		switch (kind) {
			case "clear" -> level.setWeatherParameters(seconds * 20, 0, false, false);
			case "rain" -> level.setWeatherParameters(0, seconds * 20, true, false);
			case "thunder" -> level.setWeatherParameters(0, seconds * 20, true, true);
			default -> LOGGER.warn("Unknown weather type: {}", kind);
		}
	}

	private void spawn(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 4) {
			LOGGER.warn("spawn requires entity ID and x, y, z coordinates");
			return;
		}
		String id = ScriptUtils.getStringArg(segment, 0, vars);
		double x = ScriptUtils.getNumberArg(segment, 1, vars);
		double y = ScriptUtils.getNumberArg(segment, 2, vars);
		double z = ScriptUtils.getNumberArg(segment, 3, vars);
		EntityType<?> type = EntityType.byString(id).orElse(null);
		if (type == null) {
			LOGGER.warn("Unknown entity type: {}", id);
			return;
		}
		Entity entity = type.create(level, EntitySpawnReason.COMMAND);
		if (entity == null) {
			LOGGER.warn("Failed to create entity: {}", id);
			return;
		}
		entity.moveOrInterpolateTo(new Vec3(x, y, z), 0, 0);
		level.addFreshEntity(entity);
	}

	private void setDifficulty(MinecraftServer server, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 1) {
			LOGGER.warn("difficulty requires difficulty level");
			return;
		}
		String diff = ScriptUtils.getStringArg(segment, 0, vars);
		Difficulty difficulty = switch (diff) {
			case "peaceful" -> Difficulty.PEACEFUL;
			case "easy" -> Difficulty.EASY;
			case "normal" -> Difficulty.NORMAL;
			default -> Difficulty.HARD;
		};
		server.setDifficulty(difficulty, true);
	}

	private void createExplosion(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 4) {
			LOGGER.warn("explosion requires x, y, z, and power");
			return;
		}
		double x = ScriptUtils.getNumberArg(segment, 0, vars);
		double y = ScriptUtils.getNumberArg(segment, 1, vars);
		double z = ScriptUtils.getNumberArg(segment, 2, vars);
		float power = (float) ScriptUtils.getNumberArg(segment, 3, vars);
		level.explode(null, x, y, z, power, Level.ExplosionInteraction.BLOCK);
	}

	private void setBlock(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 4) {
			LOGGER.warn("setBlock requires x, y, z, and block ID");
			return;
		}
		int x = (int) ScriptUtils.getNumberArg(segment, 0, vars);
		int y = (int) ScriptUtils.getNumberArg(segment, 1, vars);
		int z = (int) ScriptUtils.getNumberArg(segment, 2, vars);
		String blockId = ScriptUtils.getStringArg(segment, 3, vars);
		Block block = ScriptUtils.getBlock(blockId);
		level.setBlockAndUpdate(new BlockPos(x, y, z), block.defaultBlockState());
	}

	private void fill(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 7) {
			LOGGER.warn("fill requires x1, y1, z1, x2, y2, z2, and block ID");
			return;
		}
		int x1 = (int) ScriptUtils.getNumberArg(segment, 0, vars);
		int y1 = (int) ScriptUtils.getNumberArg(segment, 1, vars);
		int z1 = (int) ScriptUtils.getNumberArg(segment, 2, vars);
		int x2 = (int) ScriptUtils.getNumberArg(segment, 3, vars);
		int y2 = (int) ScriptUtils.getNumberArg(segment, 4, vars);
		int z2 = (int) ScriptUtils.getNumberArg(segment, 5, vars);
		String blockId = ScriptUtils.getStringArg(segment, 6, vars);
		Block block = ScriptUtils.getBlock(blockId);
		BlockState state = block.defaultBlockState();
		for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
			for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
				for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
					level.setBlockAndUpdate(new BlockPos(x, y, z), state);
				}
			}
		}
	}

	private void spawnParticle(ServerLevel level, CallChain.Segment segment, Map<String, Object> vars) {
		if (segment.args().size() < 8) {
			LOGGER.warn("particle requires particle ID, x, y, z, count, dx, dy, dz, and optionally block/item ID");
			return;
		}
		String particleId = ScriptUtils.getStringArg(segment, 0, vars);
		double x = ScriptUtils.getNumberArg(segment, 1, vars);
		double y = ScriptUtils.getNumberArg(segment, 2, vars);
		double z = ScriptUtils.getNumberArg(segment, 3, vars);
		int count = (int) ScriptUtils.getNumberArg(segment, 4, vars);
		double dx = ScriptUtils.getNumberArg(segment, 5, vars);
		double dy = ScriptUtils.getNumberArg(segment, 6, vars);
		double dz = ScriptUtils.getNumberArg(segment, 7, vars);
		double speed = segment.args().size() >= 9 ? ScriptUtils.getNumberArg(segment, 8, vars) : 0.0;

		try {
			ParticleOptions particle = ScriptUtils.getParticleOptions(particleId, segment, vars);
			level.sendParticles(particle, x, y, z, count, dx, dy, dz, speed);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error spawning particle {}: {}", particleId, e.getMessage());
		}
	}
}