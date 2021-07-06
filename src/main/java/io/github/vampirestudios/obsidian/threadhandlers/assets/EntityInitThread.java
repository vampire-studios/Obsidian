package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.client.CustomEntityRenderer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityInitThread implements Runnable {

	private final Entity entity;
	private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

	public EntityInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder_in, Entity entity_in) {
		entity = entity_in;
		clientResourcePackBuilder = clientResourcePackBuilder_in;
	}

	@Override
	public void run() {
		EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) Registry.ENTITY_TYPE.get(entity.information.identifier);
        EntityRendererRegistry.INSTANCE.register(entityType, ctx -> new CustomEntityRenderer(ctx, entity));
		Identifier identifier = entity.information.identifier;
		clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, "en_us"), translationBuilder ->
				translationBuilder.entry(String.format("entity.%s.%s", identifier.getNamespace(), identifier.getPath()),
						entity.information.name).entry(String.format("item.%s.%s_spawn_egg", identifier.getNamespace(), identifier.getPath()),
						entity.information.name + " Spawn Egg"));
		clientResourcePackBuilder.addItemModel(new Identifier(entity.information.identifier.getNamespace(), entity.information.identifier.getPath() + "_spawn_egg"), modelBuilder ->
				modelBuilder.parent(new Identifier("item/template_spawn_egg")));
	}

}
