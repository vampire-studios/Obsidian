package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ParticleImpl extends SpriteBillboardParticle {

    private io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle;

    public ParticleImpl(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y - 0.125D, z, velocityX, velocityY, velocityZ);
        this.particle = particle;
        this.collidesWithWorld = particle.collides_with_world;
        this.colorRed = particle.red_color;
        this.colorGreen = particle.green_color;
        this.colorBlue = particle.blue_color;
        this.maxAge = particle.max_age;
        this.scale *= this.random.nextFloat() * 0.4F + 0.7F;
        this.setBoundingBoxSpacing(0.01F, 0.01F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return particle.getType();
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        private final io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle;

        public Factory(io.github.vampirestudios.obsidian.api.obsidian.particle.Particle particle, SpriteProvider spriteProvider) {
            this.particle = particle;
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ParticleImpl particle = new ParticleImpl(this.particle, clientWorld, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

}
