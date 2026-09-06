package io.github.vampirestudios.obsidian.villager;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.api.SidedPackwrightCallback;
import net.vampirestudios.packwright.data.tags.Tag;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Serves the point of interest tag that makes addon job sites reachable.
 * <p>
 * An unemployed villager only looks for job sites in {@code minecraft:acquirable_job_site}; the
 * profession's own job site predicate is consulted afterwards, once the villager has claimed the block.
 * A profession whose point of interest is outside that tag is therefore registered but unreachable, so
 * every point of interest an addon profession registers is appended to the tag through a small runtime
 * data pack.
 */
public final class AddonJobSites {

	private static final Identifier ACQUIRABLE_JOB_SITE =
			Identifier.withDefaultNamespace("point_of_interest_type/acquirable_job_site");
	private static final Set<Identifier> JOB_SITES = new LinkedHashSet<>();

	private AddonJobSites() {
	}

	public static void markAcquirable(Identifier pointOfInterest) {
		JOB_SITES.add(pointOfInterest);
	}

	public static void register() {
		SidedPackwrightCallback.BETWEEN_MODS_AND_USER.register((type, resources) -> {
			if (type != PackType.SERVER_DATA || JOB_SITES.isEmpty()) return;

			// not replacing: the pack sits after vanilla and mods, so the entries are appended to
			// whatever the rest of the tag already holds
			Tag tag = Tag.tag();
			JOB_SITES.forEach(tag::add);

			RuntimeResourcePack pack = RuntimeResourcePack.create(Obsidian.id("villager_job_sites"));
			pack.addTag(ACQUIRABLE_JOB_SITE, tag);
			resources.add(pack);

			Obsidian.LOGGER.debug("Added {} addon job site(s) to {}", JOB_SITES.size(), ACQUIRABLE_JOB_SITE);
		});
	}
}
