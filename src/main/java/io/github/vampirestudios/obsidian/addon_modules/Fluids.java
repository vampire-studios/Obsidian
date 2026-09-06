package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FluidImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.alreadyRegistered;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Fluids implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Fluid fluid = AddonFormats.read(addon, file, Fluid.class);
		if (fluid == null) return;

		Identifier fluidId = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		try {
			fluid.id = fluidId;

			// A fluid that cannot be built leaves half a registry entry behind, which stops the game
			// finishing startup at all — so this is checked before anything is registered.
			if (alreadyRegistered(BuiltInRegistries.FLUID, fluidId, "fluid", file.getName())) return;

			if (fluid.parent == Fluid.ParentFluid.WATER) fluid = fluid.waterLike();
			else if (fluid.parent == Fluid.ParentFluid.LAVA) fluid = fluid.lavaLike();
			fluid.id = fluidId;

			FluidImpl.register(fluid);
			register(ContentRegistries.FLUIDS, "fluid", fluidId, fluid);
		} catch (Exception e) {
			failedRegistering("fluid", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "fluid";
	}
}
