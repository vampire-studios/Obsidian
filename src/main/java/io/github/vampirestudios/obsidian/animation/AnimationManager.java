package io.github.vampirestudios.obsidian.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AnimationManager implements SimpleResourceReloadListener<AnimationManager.AnimationLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("Obsidian Animation Manager");
	private Map<Identifier, AnimationDefinition> animations;

	public AnimationDefinition getAnimation(Identifier id) {
		return animations.get(id);
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.fromNamespaceAndPath("obsidian", "animation_reloader");
	}

	@Override
	public CompletableFuture<AnimationLoader> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new AnimationLoader(manager), executor);
	}

	@Override
	public CompletableFuture<Void> apply(AnimationLoader data, ResourceManager manager, Executor executor) {
		this.animations = data.getAnimations();
		return CompletableFuture.runAsync(() -> {});
	}

	public static class AnimationLoader {
		private final ResourceManager manager;
		private final Map<Identifier, AnimationDefinition> animations = new HashMap<>();

		public AnimationLoader(ResourceManager manager) {
			this.manager = manager;
			loadAnimations();
		}

		private void loadAnimations() {
			Map<Identifier, Resource> resources = manager.listResources("animations", id -> id.getPath().endsWith(".json"));
			for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
				addAnimation(entry.getKey(), entry.getValue());
			}
		}

		private void addAnimation(Identifier id, Resource resource) {
			BufferedReader reader;
			try {
				reader = resource.openAsReader();
			} catch (IOException e) {
				LOGGER.error(String.format("Unable to open BufferedReader for id %s", id), e);
				return;
			}

			JsonObject json = GsonHelper.parse(reader);
			DataResult<Pair<AnimationDefinition, JsonElement>> result = Codecs.Animations.ANIMATION.decode(JsonOps.INSTANCE, json);

			if (result.error().isPresent()) {
				LOGGER.error(String.format("Unable to parse animation file %s.\nReason: %s", id, result.error().get().message()));
				return;
			}

			animations.put(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring("animations/".length())), result.result().get().getFirst());
		}

		public Map<Identifier, AnimationDefinition> getAnimations() {
			return animations;
		}
	}
}