package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.mixins.PointOfInterestTypesAccessor;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class VillagerProfessions implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        VillagerProfession villagerProfession = Obsidian.GSON.fromJson(new FileReader(file), VillagerProfession.class);
        try {
            if (villagerProfession == null) return;
            Identifier identifier = Objects.requireNonNullElseGet(
                    villagerProfession.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (villagerProfession.name.id == null) villagerProfession.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
            RegistryKey<PointOfInterestType> registryKey = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, villagerProfession.poi.id);
            PointOfInterestTypesAccessor.callRegister(
                    Registry.POINT_OF_INTEREST_TYPE,
                    registryKey,
                    villagerProfession.poi.getBlocks(), villagerProfession.poi.ticket_count,
                    villagerProfession.poi.search_distance
            );
            VillagerProfessionBuilder.create()
                    .id(identifier)
                    .harvestableItems(villagerProfession.getHarvestableItems())
                    .workstation(registryKey)
                    .workSound(Registry.SOUND_EVENT.get(villagerProfession.work_sound))
                    .build();
            register(ContentRegistries.VILLAGER_PROFESSIONS, "villager_profession", identifier, villagerProfession);
        } catch (Exception e) {
            failedRegistering("villager_profession", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "villagers/professions";
    }
}
