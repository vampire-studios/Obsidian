package io.github.vampirestudios.obsidian.api.obsidian.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.Identifier;

public class Particle {

    public Identifier id;
    public String sheet_type = "NO_RENDER";
    public boolean always_spawn = true;
    public boolean collides_with_world = false;
    public float red_color = 1.0F;
    public float green_color = 1.0F;
    public float blue_color = 1.0F;
    public float size = 1.0F;
    public int max_age = 1;

    public ParticleTextureSheet getType() {
        return switch (sheet_type) {
            case "TERRAIN_SHEET" -> ParticleTextureSheet.TERRAIN_SHEET;
            case "PARTICLE_SHEET_OPAQUE" -> ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
            case "PARTICLE_SHEET_TRANSLUCENT" -> ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
            case "PARTICLE_SHEET_LIT" -> ParticleTextureSheet.PARTICLE_SHEET_LIT;
            case "CUSTOM" -> ParticleTextureSheet.CUSTOM;
            default -> ParticleTextureSheet.NO_RENDER;
        };
    }

}
