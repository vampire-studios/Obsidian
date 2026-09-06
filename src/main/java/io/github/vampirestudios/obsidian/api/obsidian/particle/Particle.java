package io.github.vampirestudios.obsidian.api.obsidian.particle;

import net.minecraft.resources.Identifier;

public class Particle {

	public Identifier id;

	/**
	 * Which layer the particle draws on. Accepts the current names — {@code TRANSLUCENT},
	 * {@code OPAQUE}, {@code OPAQUE_TERRAIN}, {@code TRANSLUCENT_TERRAIN}, {@code OPAQUE_ITEMS},
	 * {@code TRANSLUCENT_ITEMS} — and the older sheet names packs were written against. Resolved
	 * client-side, since the layers themselves are a client type.
	 */
	public String sheet_type = "TRANSLUCENT";

	public boolean always_spawn = true;
	public boolean collides_with_world = false;
	public float red_color = 1.0F;
	public float green_color = 1.0F;
	public float blue_color = 1.0F;
	public float size = 1.0F;
	public int max_age = 1;

}
