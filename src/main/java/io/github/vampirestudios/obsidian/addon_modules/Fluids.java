package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FluidImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.fluid.FluidState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Fluids implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Fluid fluid = Obsidian.GSON.fromJson(new FileReader(file), Fluid.class);
        try {
            if (fluid == null) return;

            if (fluid.parent.equals(Fluid.ParentFluid.WATER)) fluid = fluid.waterLike();
            else if (fluid.parent.equals(Fluid.ParentFluid.LAVA)) fluid = fluid.lavaLike();

            new FluidImpl(fluid) {
                @Override
                public boolean isSource(FluidState state) {
                    return false;
                }

                @Override
                public int getLevel(FluidState state) {
                    return 0;
                }
            };
            register(FLUIDS, "fluid", fluid.name.id, fluid);
        } catch (Exception e) {
            failedRegistering("fluid", fluid.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "fluids";
    }
}
