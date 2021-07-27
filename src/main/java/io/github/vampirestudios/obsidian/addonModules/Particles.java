package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.particle.Particle;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ParticleImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Particles implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Particle particle = Obsidian.GSON.fromJson(new FileReader(file), Particle.class);
        try {
            if (particle == null) return;
            DefaultParticleType particleType = Registry.register(Registry.PARTICLE_TYPE, particle.id,
                    FabricParticleTypes.simple(false));
            ParticleFactoryRegistry.getInstance().register(particleType, provider -> new ParticleImpl.Factory(particle, provider));
            register(PARTICLES, "particle", particle.id, particle);
        } catch (Exception e) {
            failedRegistering("particle", particle.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "particles";
    }
}
