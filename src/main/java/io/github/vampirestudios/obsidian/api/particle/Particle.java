package io.github.vampirestudios.obsidian.api.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.Identifier;

public class Particle {

    public Identifier id;
    public Identifier texture;
    public String sheet_type = "NO_RENDER";
    public boolean always_spawn = true;
    public boolean collides_with_world = false;
    public float red_color = 1.0F;
    public float green_color = 1.0F;
    public float blue_color = 1.0F;
    public int max_age = 1;

    public ParticleTextureSheet getType() {
        switch (sheet_type) {
            case "TERRAIN_SHEET":
                return ParticleTextureSheet.TERRAIN_SHEET;
            case "PARTICLE_SHEET_OPAQUE":
                return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
            case "PARTICLE_SHEET_TRANSLUCENT":
                return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
            case "PARTICLE_SHEET_LIT":
                return ParticleTextureSheet.PARTICLE_SHEET_LIT;
            case "CUSTOM":
                return ParticleTextureSheet.CUSTOM;
            case "NO_RENDER":
            default:
                return ParticleTextureSheet.NO_RENDER;
        }
    }

}
