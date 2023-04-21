package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class ParticleImpl extends TextureSheetParticle {

    private final io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle;

    public ParticleImpl(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y - 0.125D, z, velocityX, velocityY, velocityZ);
        this.particle = particle;
        this.hasPhysics = particle.collides_with_world;
        this.rCol = particle.red_color;
        this.gCol = particle.green_color;
        this.bCol = particle.blue_color;
        this.lifetime = particle.max_age;
        this.quadSize *= /*this.random.nextFloat() * 0.4F + 0.7F*/particle.size;
        this.setSize(0.01F, 0.01F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return particle.getType();
    }

    public record Factory(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle,
                          SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ParticleImpl particle = new ParticleImpl(this.particle, clientWorld, x, y, z, velocityX, velocityY, velocityZ);
            particle.pickSprite(this.spriteProvider);
            return particle;
        }
    }

}
