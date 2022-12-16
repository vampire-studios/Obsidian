package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ParticleImpl extends SpriteBillboardParticle {

    private final io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle;

    public ParticleImpl(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y - 0.125D, z, velocityX, velocityY, velocityZ);
        this.particle = particle;
        this.collidesWithWorld = particle.collides_with_world;
        this.red = particle.red_color;
        this.green = particle.green_color;
        this.blue = particle.blue_color;
        this.maxAge = particle.max_age;
        this.scale *= /*this.random.nextFloat() * 0.4F + 0.7F*/particle.size;
        this.setBoundingBoxSpacing(0.01F, 0.01F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return particle.getType();
    }

    public record Factory(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle,
                          SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ParticleImpl particle = new ParticleImpl(this.particle, clientWorld, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

}
