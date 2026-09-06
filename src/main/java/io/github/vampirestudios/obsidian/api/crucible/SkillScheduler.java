package io.github.vampirestudios.obsidian.api.crucible;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class SkillScheduler {
	private static final Map<Integer, Integer> taskDelays = new HashMap<>();

	public static void scheduleSkillExecution(int delayTicks, Runnable skillTask) {
		if (delayTicks <= 0) {
			skillTask.run(); // Run immediately if no delay
			return;
		}

		int targetTick = delayTicks;
		taskDelays.put(targetTick, delayTicks);

		ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
			taskDelays.entrySet().removeIf(entry -> {
				int ticksLeft = entry.getValue();
				if (ticksLeft <= 0) {
					skillTask.run();
					return true; // Remove from the map once executed
				}
				entry.setValue(ticksLeft - 1);
				return false;
			});
		});
	}
}
