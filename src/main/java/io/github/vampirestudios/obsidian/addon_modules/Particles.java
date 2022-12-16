package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.particle.Particle;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ParticleImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

@Environment(EnvType.CLIENT)
public class Particles implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Particle particle = Obsidian.GSON.fromJson(new FileReader(file), Particle.class);
        try {
            if (particle == null) return;
            Identifier identifier = Objects.requireNonNullElseGet(
                    particle.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (particle.id == null) particle.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
            DefaultParticleType particleType = Registry.register(Registries.PARTICLE_TYPE, identifier,
                    FabricParticleTypes.simple(false));
            ParticleFactoryRegistry.getInstance().register(particleType, provider -> new ParticleImpl.Factory(particle, provider));
            register(ContentRegistries.PARTICLES, "particle", identifier, particle);
        } catch (Exception e) {
            failedRegistering("particle", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "particles";
    }
}
