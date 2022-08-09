package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class CauldronTypes implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
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
            register(ContentRegistries.CAULDRON_TYPES, "cauldron_type", cauldronType.name, cauldronType);
        } catch (Exception e) {
            failedRegistering("cauldron_types", cauldronType.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "cauldron_types";
    }
}
