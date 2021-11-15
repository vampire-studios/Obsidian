package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class CauldronTypes implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        CauldronType cauldronType = Obsidian.GSON.fromJson(new FileReader(file), CauldronType.class);
        try {
            if (cauldronType == null) return;
            CauldronBehavior cauldronBehavior = (state, world, pos, player, hand, stack) -> {
                BlockState blockState = getState(Registry.BLOCK.get(cauldronType.blockstate.block), cauldronType.blockstate.properties);
                return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, blockState, Registry.SOUND_EVENT.get(cauldronType.sound_event));
            };
            CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
            register(CAULDRON_TYPES, "cauldron_type", cauldronType.name, cauldronType);
        } catch (Exception e) {
            failedRegistering("cauldron_types", cauldronType.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "cauldron_types";
    }
}
