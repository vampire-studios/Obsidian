package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.renderer.CustomEntityRenderer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class EntityInitThread implements Runnable {

	private final Entity entity;

	public EntityInitThread(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void run() {
		Identifier id = entity.description.id;
		if (id == null) return;

		@SuppressWarnings("unchecked")
		EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) BuiltInRegistries.ENTITY_TYPE.getValue(id);
		if (entityType == null) return;

		EntityRendererRegistry.register(entityType, ctx -> new CustomEntityRenderer(ctx, entity));

		if (entity.description.name != null) {
			ClientInit.addTranslation(
					id.getNamespace(), "en_us",
					"entity." + id.getNamespace() + "." + id.getPath(),
					entity.description.name
			);
			if (entity.description.spawnable) {
				ClientInit.addTranslation(
						id.getNamespace(), "en_us",
						"item." + id.getNamespace() + "." + id.getPath() + "_spawn_egg",
						entity.description.name + " Spawn Egg"
				);
			}
		}
	}
}
