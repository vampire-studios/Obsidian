package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.client.CustomEntityRenderer;
import io.github.vampirestudios.obsidian.client.JsonEntityRenderer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;

public class EntityInitThread implements Runnable {

	private final Entity entity;

	public EntityInitThread(Entity entity_in) {
		entity = entity_in;
	}

	@Override
	public void run() {
		EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) Registry.ENTITY_TYPE.get(entity.information.identifier);
		if (entity.information.custom_model) {
			EntityRendererRegistry.INSTANCE.register(entityType, this::create);
		} else {
			EntityRendererRegistry.INSTANCE.register(entityType, this::create2);
		}
		Identifier identifier = entity.information.identifier;
		Artifice.registerAssetPack(String.format("%s:%s_entity_assets", entity.information.identifier.getNamespace(), entity.information.identifier.getPath()), clientResourcePackBuilder -> {
			clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, "en_us"), translationBuilder ->
					translationBuilder.entry(String.format("entity.%s.%s", identifier.getNamespace(), identifier.getPath()),
							entity.information.name));
			clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, "en_us"), translationBuilder ->
					translationBuilder.entry(String.format("item.%s.%s_spawn_egg", identifier.getNamespace(), identifier.getPath()),
							entity.information.name + " Spawn Egg"));
			clientResourcePackBuilder.addItemModel(new Identifier(entity.information.identifier.getNamespace(), entity.information.identifier.getPath() + "_spawn_egg"), modelBuilder ->
					modelBuilder.parent(new Identifier("item/template_spawn_egg")));
			try {
				if (FabricLoader.getInstance().isDevelopmentEnvironment())
					clientResourcePackBuilder.dumpResources("testing", "assets");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private EntityRenderer<EntityImpl> create(EntityRendererFactory.Context ctx) {
		return new JsonEntityRenderer(ctx, entity);
	}

	private EntityRenderer<EntityImpl> create2(EntityRendererFactory.Context ctx) {
		return new CustomEntityRenderer(ctx, entity);
	}
}
