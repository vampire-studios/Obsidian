package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.renderer.CustomEntityRenderer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityInitThread implements Runnable {

	private final Entity entity;
	private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

	public EntityInitThread(ArtificeResourcePack.ClientResourcePackBuilder builder, Entity entity) {
		this.entity = entity;
		clientResourcePackBuilder = builder;
	}

	@Override
	public void run() {
		EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) Registry.ENTITY_TYPE.get(entity.information.identifier);
		EntityRendererRegistry.register(entityType, ctx -> new CustomEntityRenderer(ctx, entity));
		Identifier identifier = entity.information.identifier;
		ClientInit.addTranslation(
				identifier.getNamespace(), "en_us",
				"entity." + identifier.getNamespace() + "." + identifier.getPath(),
				entity.information.name
		);
		ClientInit.addTranslation(
				identifier.getNamespace(), "en_us",
				"item." + identifier.getNamespace() + "." + identifier.getPath() + "_spawn_egg",
				entity.information.name + " Spawn Egg"
		);
		ModelBuilder modelBuilder = new ModelBuilder().parent(new Identifier("item/template_spawn_egg"));
		clientResourcePackBuilder.addItemModel(new Identifier(entity.information.identifier.getNamespace(),
				entity.information.identifier.getPath() + "_spawn_egg"), modelBuilder);
	}

}
