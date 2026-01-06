package io.github.vampirestudios.obsidian.api.obsidian.particle;

import net.minecraft.resources.Identifier;

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

//    public ParticleRenderType getType() {
//        return switch (sheet_type) {
//            case "TERRAIN_SHEET" -> ParticleRenderType;
//            case "PARTICLE_SHEET_OPAQUE" -> ParticleRenderType.PARTICLE_SHEET_OPAQUE;
//            case "PARTICLE_SHEET_TRANSLUCENT" -> ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
//            case "CUSTOM" -> ParticleRenderType.CUSTOM;
//            default -> ParticleRenderType.NO_RENDER;
//        };
//    }

}
