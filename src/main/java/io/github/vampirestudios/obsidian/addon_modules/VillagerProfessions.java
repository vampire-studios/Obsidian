package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.villager.PointOfInterest;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.mixins.PointOfInterestTypesAccessor;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.villager.AddonJobSites;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class VillagerProfessions implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		VillagerProfession villagerProfession = AddonFormats.read(addon, file, VillagerProfession.class);
		try {
			if (villagerProfession == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

			ResourceKey<PoiType> jobSiteKey = registerJobSite(villagerProfession, identifier);
			registerProfession(villagerProfession, identifier, jobSiteKey);

			// without this the profession exists but no villager can ever take it: unemployed villagers
			// only ever walk to a point of interest inside minecraft:acquirable_job_site
			AddonJobSites.markAcquirable(jobSiteKey.identifier());

			register(ContentRegistries.VILLAGER_PROFESSIONS, "villager_profession", identifier, villagerProfession);
		} catch (Exception e) {
			failedRegistering("villager_profession", file.getName(), e);
		}
	}

	/**
	 * Registers the profession's job site, or reuses an existing one when {@code poi.id} points at a
	 * point of interest vanilla or another addon already registered.
	 */
	private ResourceKey<PoiType> registerJobSite(VillagerProfession profession, Identifier identifier) {
		PointOfInterest poi = profession.poi;
		Identifier poiId = poi != null && poi.id != null ? poi.id : identifier;
		ResourceKey<PoiType> key = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, poiId);

		if (BuiltInRegistries.POINT_OF_INTEREST_TYPE.containsKey(poiId)) {
			return key;
		}

		Set<BlockState> states = poi != null ? poi.getBlocks() : Set.of();
		if (states.isEmpty()) {
			throw new IllegalArgumentException("Villager profession " + identifier + " has no job site: give it"
					+ " a 'poi.blocks' list, or point 'poi.id' at a point of interest type that already exists");
		}

		// PoiTypes.register, not a plain registry write: the point of interest is only found in the
		// world through the block state lookup that method also fills in
		PointOfInterestTypesAccessor.callRegister(
				BuiltInRegistries.POINT_OF_INTEREST_TYPE, key, states, poi.getTicketCount(), poi.getSearchDistance()
		);
		Obsidian.LOGGER.info("Registered point of interest type {}.", poiId);
		return key;
	}

	private void registerProfession(VillagerProfession profession, Identifier identifier, ResourceKey<PoiType> jobSiteKey) {
		if (BuiltInRegistries.VILLAGER_PROFESSION.containsKey(identifier)) return;

		Predicate<Holder<PoiType>> jobSite = poiType -> poiType.is(jobSiteKey);
		Registry.register(
				BuiltInRegistries.VILLAGER_PROFESSION,
				ResourceKey.create(Registries.VILLAGER_PROFESSION, identifier),
				new net.minecraft.world.entity.npc.villager.VillagerProfession(
						displayName(profession, identifier),
						jobSite,
						jobSite,
						profession.getRequestedItems(),
						profession.getSecondaryPoi(),
						profession.getWorkSound(),
						profession.getTradeSets()
				)
		);
	}

	/**
	 * The declared name when the pack gives one, otherwise the key vanilla would use for a profession
	 * of this id, so a pack can ship the name as a translation instead.
	 */
	private Component displayName(VillagerProfession profession, Identifier identifier) {
		if (profession.name != null && profession.name.text != null && !profession.name.text.isEmpty()) {
			return profession.name.getName();
		}
		return Component.translatable("entity." + identifier.getNamespace() + ".villager." + identifier.getPath());
	}

	@Override
	public String getType() {
		return "villager/profession";
	}
}
