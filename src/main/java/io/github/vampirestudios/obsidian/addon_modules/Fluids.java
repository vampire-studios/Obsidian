package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FluidImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.fluid.FluidState;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Fluids implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Fluid fluid = Obsidian.GSON.fromJson(new FileReader(file), Fluid.class);
        try {
            if (fluid == null) return;

            if (fluid.parent.equals(Fluid.ParentFluid.WATER)) fluid = fluid.waterLike();
            else if (fluid.parent.equals(Fluid.ParentFluid.LAVA)) fluid = fluid.lavaLike();

            new FluidImpl(fluid) {
                @Override
                public boolean isStill(FluidState state) {
                    return false;
                }

                @Override
                public int getLevel(FluidState state) {
                    return 0;
                }
            };
            register(ContentRegistries.FLUIDS, "fluid", fluid.name.id, fluid);
        } catch (Exception e) {
            failedRegistering("fluid", fluid.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "fluids";
    }
}
