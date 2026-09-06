package io.github.vampirestudios.obsidian.client.renderer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

/** What {@link ProjectileArrowRenderer} needs off the entity: where it points, and what it looks like. */
public class ProjectileArrowRenderState extends EntityRenderState {

	public final ItemStackRenderState item = new ItemStackRenderState();

	public float xRot;
	public float yRot;

	/** Remaining wobble after landing, in ticks. */
	public float shake;
}
