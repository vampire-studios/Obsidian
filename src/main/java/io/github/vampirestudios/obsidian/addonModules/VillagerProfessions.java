package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class VillagerProfessions implements AddonModule {
	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		VillagerProfession villagerProfession = Obsidian.GSON.fromJson(new FileReader(file), VillagerProfession.class);
		try {
			if(villagerProfession == null) return;
			PointOfInterestType pointOfInterestType = PointOfInterestHelper.register(villagerProfession.poi.id, villagerProfession.poi.ticket_count, villagerProfession.poi.search_distance, villagerProfession.poi.getBlocks().toArray(new Block[0]));
			VillagerProfessionBuilder.create()
					.id(villagerProfession.name.id)
					.harvestableItems(villagerProfession.getHarvestableItems())
					.workstation(pointOfInterestType)
					.workSound(Registry.SOUND_EVENT.get(villagerProfession.work_sound))
					.build();
			register(VILLAGER_PROFESSIONS, "villager_profession", villagerProfession.name.id.toString(), villagerProfession);
		} catch (Exception e) {
			failedRegistering("villager_profession", villagerProfession.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items/elytra";
	}
}
