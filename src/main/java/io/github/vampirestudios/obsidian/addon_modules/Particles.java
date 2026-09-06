package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.particle.Particle;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ParticleImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

@Environment(EnvType.CLIENT)
public class Particles implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Particle particle = AddonFormats.read(addon, file, Particle.class);
		try {
			if (particle == null) return;
			Identifier identifier = Objects.requireNonNullElseGet(
					particle.id,
					() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
			);
			if (particle.id == null)
				particle.id = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			SimpleParticleType particleType = Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier,
					FabricParticleTypes.simple(particle.always_spawn));

			// The sprite set is handed over when the particle engine loads its atlas, which is why the
			// definition is bound to a factory rather than to a finished provider.
			ParticleProviderRegistry.getInstance().register(particleType,
					provider -> new ParticleImpl.Provider(particle, provider));

			register(ContentRegistries.PARTICLES, "particle", identifier, particle);
		} catch (Exception e) {
			failedRegistering("particle", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "particle";
	}
}
