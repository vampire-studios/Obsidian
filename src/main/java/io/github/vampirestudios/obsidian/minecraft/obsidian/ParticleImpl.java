package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import java.util.Locale;

/**
 * A particle described by a {@code particle} definition.
 *
 * <p>Built on {@link SimpleAnimatedParticle} so a definition's texture can be a strip and animate over
 * the particle's life, the way vanilla's own sprite particles do.
 */
public class ParticleImpl extends SimpleAnimatedParticle {

	private final SingleQuadParticle.Layer layer;

	public ParticleImpl(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle,
	                    ClientLevel level, double x, double y, double z,
	                    double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
		super(level, x, y, z, sprites, 0.0F);

		this.layer = layerOf(particle.sheet_type);
		this.hasPhysics = particle.collides_with_world;

		this.rCol = particle.red_color;
		this.gCol = particle.green_color;
		this.bCol = particle.blue_color;

		// A lifetime of zero would be removed before it is ever drawn.
		this.lifetime = Math.max(1, particle.max_age);
		this.quadSize *= particle.size;
		this.setSize(0.01F, 0.01F);

		// SimpleAnimatedParticle takes no velocity, so it is applied after construction.
		this.xd = velocityX;
		this.yd = velocityY;
		this.zd = velocityZ;

		this.setSpriteFromAge(sprites);
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return this.layer;
	}

	/**
	 * The layer a definition's {@code sheet_type} names.
	 *
	 * <p>Render sheets were replaced by layers, so the names packs used to write no longer exist
	 * one-for-one. The old ones map to their closest equivalent rather than being dropped:
	 * {@code TERRAIN_SHEET} to the terrain layer, the particle sheets to the plain ones, and
	 * {@code NO_RENDER} — which is what a definition got by saying nothing — to translucent, since a
	 * particle nobody can see is never what the pack meant.
	 */
	private static SingleQuadParticle.Layer layerOf(String sheetType) {
		if (sheetType == null || sheetType.isBlank()) return SingleQuadParticle.Layer.TRANSLUCENT;

		return switch (sheetType.toUpperCase(Locale.ROOT)) {
			case "OPAQUE" -> SingleQuadParticle.Layer.OPAQUE;
			case "OPAQUE_TERRAIN", "TERRAIN_SHEET" -> SingleQuadParticle.Layer.OPAQUE_TERRAIN;
			case "TRANSLUCENT_TERRAIN" -> SingleQuadParticle.Layer.TRANSLUCENT_TERRAIN;
			case "OPAQUE_ITEMS", "PARTICLE_SHEET_OPAQUE" -> SingleQuadParticle.Layer.OPAQUE_ITEMS;
			case "TRANSLUCENT_ITEMS" -> SingleQuadParticle.Layer.TRANSLUCENT_ITEMS;
			default -> SingleQuadParticle.Layer.TRANSLUCENT;
		};
	}

	/**
	 * Binds a definition to the sprite set the particle engine loaded for it, which is what the client
	 * registration hands over.
	 */
	public record Provider(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle,
	                       SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

		@Override
		public Particle createParticle(SimpleParticleType options, ClientLevel level,
		                               double x, double y, double z,
		                               double velocityX, double velocityY, double velocityZ,
		                               RandomSource random) {
			return new ParticleImpl(this.particle, level, x, y, z, velocityX, velocityY, velocityZ,
					this.spriteProvider);
		}
	}

}
