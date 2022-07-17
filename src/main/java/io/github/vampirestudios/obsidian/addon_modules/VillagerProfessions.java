package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.mixins.PointOfInterestTypesAccessor;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class VillagerProfessions implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        VillagerProfession villagerProfession = Obsidian.GSON.fromJson(new FileReader(file), VillagerProfession.class);
        try {
            if (villagerProfession == null) return;
            RegistryKey<PointOfInterestType> registryKey = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, villagerProfession.poi.id);
            PointOfInterestTypesAccessor.callRegister(
                    Registry.POINT_OF_INTEREST_TYPE,
                    registryKey,
                    villagerProfession.poi.getBlocks(), villagerProfession.poi.ticket_count,
                    villagerProfession.poi.search_distance
            );
            VillagerProfessionBuilder.create()
                    .id(villagerProfession.name.id)
                    .harvestableItems(villagerProfession.getHarvestableItems())
                    .workstation(registryKey)
                    .workSound(Registry.SOUND_EVENT.get(villagerProfession.work_sound))
                    .build();
            register(VILLAGER_PROFESSIONS, "villager_profession", villagerProfession.name.id, villagerProfession);
        } catch (Exception e) {
            failedRegistering("villager_profession", villagerProfession.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "villagers/professions";
    }
}
