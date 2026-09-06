package io.github.vampirestudios.obsidian.api.crucible.aura;

import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Every live aura in the world. {@link #tick()} has to run once a server tick — without it auras are
 * created, never expire, and never apply anything.
 */
public class AuraManager {
	private static final List<Aura> activeAuras = new ArrayList<>();

	/** Auras by the entity they are attached to, so a condition can ask what an entity is carrying. */
	private static final Map<LivingEntity, List<Aura>> entityAuras = new HashMap<>();

	public static void addAura(Aura aura) {
		if (aura == null || aura.getCaster() == null) return;

		activeAuras.add(aura);
		entityAuras.computeIfAbsent(aura.getCaster(), k -> new ArrayList<>()).add(aura);
	}

	public static void tick() {
		Iterator<Aura> iterator = activeAuras.iterator();
		while (iterator.hasNext()) {
			Aura aura = iterator.next();

			// An entity that died or unloaded takes its auras with it, otherwise the map holds a strong
			// reference to it forever.
			if (aura.isOrphaned()) {
				iterator.remove();
				detach(aura);
				continue;
			}

			aura.tick();
			if (aura.isExpired()) {
				iterator.remove();
				detach(aura);
			}
		}
	}

	private static void detach(Aura aura) {
		List<Aura> auras = entityAuras.get(aura.getCaster());
		if (auras == null) return;

		auras.remove(aura);
		if (auras.isEmpty()) entityAuras.remove(aura.getCaster());
	}

	public static boolean hasAura(LivingEntity entity, String auraId) {
		return countAura(entity, auraId) > 0;
	}

	/** How many auras of one id an entity is carrying — the stack count. */
	public static int countAura(LivingEntity entity, String auraId) {
		if (entity == null || auraId == null) return 0;

		int count = 0;
		for (Aura aura : aurasOn(entity)) {
			if (auraId.equals(aura.getId())) count++;
		}
		return count;
	}

	public static List<Aura> aurasOn(LivingEntity entity) {
		return Collections.unmodifiableList(entityAuras.getOrDefault(entity, List.of()));
	}

	/** Drops everything, for a server shutdown or world change. */
	public static void clear() {
		activeAuras.clear();
		entityAuras.clear();
	}
}
