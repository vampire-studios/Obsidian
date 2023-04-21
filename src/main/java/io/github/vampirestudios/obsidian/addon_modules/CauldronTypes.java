package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class CauldronTypes implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        CauldronType cauldronType = Obsidian.GSON.fromJson(new FileReader(file), CauldronType.class);
        try {
            if (cauldronType == null) return;

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    cauldronType.name,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (cauldronType.name == null) cauldronType.name = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            CauldronInteraction cauldronBehavior = (state, world, pos, player, hand, stack) -> {
                BlockState blockState = getState(BuiltInRegistries.BLOCK.get(cauldronType.blockstate.block), cauldronType.blockstate.properties);
                return CauldronInteraction.emptyBucket(world, pos, player, hand, stack, blockState, BuiltInRegistries.SOUND_EVENT.get(cauldronType.sound_event));
            };
            CauldronInteraction.EMPTY.put(BuiltInRegistries.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronInteraction.WATER.put(BuiltInRegistries.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronInteraction.LAVA.put(BuiltInRegistries.ITEM.get(cauldronType.item), cauldronBehavior);
            CauldronInteraction.POWDER_SNOW.put(BuiltInRegistries.ITEM.get(cauldronType.item), cauldronBehavior);
            register(ContentRegistries.CAULDRON_TYPES, "cauldron_type", identifier, cauldronType);
        } catch (Exception e) {
            failedRegistering("cauldron_types", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "cauldron_types";
    }
}
