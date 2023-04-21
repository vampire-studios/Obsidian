package io.github.vampirestudios.obsidian.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
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
	private Map<ResourceLocation, AnimationDefinition> animations;

	public AnimationDefinition getAnimation(ResourceLocation id) {
		return animations.get(id);
	}

	@Override
	public CompletableFuture<AnimationLoader> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new AnimationLoader(manager, profiler), executor);
	}

	@Override
	public CompletableFuture<Void> apply(AnimationLoader prepared, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		this.animations = prepared.getAnimations();
		return CompletableFuture.runAsync(() -> {
		});
	}

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation("obsidian", "animation_reloader");
	}

	public static class AnimationLoader {
		private final ResourceManager manager;
		private final ProfilerFiller profiler;
		private final Map<ResourceLocation, AnimationDefinition> animations = new HashMap<>();

		public AnimationLoader(ResourceManager manager, ProfilerFiller profiler) {
			this.manager = manager;
			this.profiler = profiler;
			loadAnimations();
		}

		private void loadAnimations() {
			profiler.push("Load Animations");
			Map<ResourceLocation, Resource> resources = manager.listResources("animations", id -> id.getPath().endsWith(".json"));
			for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
				addAnimation(entry.getKey(), entry.getValue());
			}
			profiler.pop();
		}

		private void addAnimation(ResourceLocation id, Resource resource) {
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

			animations.put(new ResourceLocation(id.getNamespace(), id.getPath().substring("animations/".length())), result.result().get().getFirst());
		}

		public Map<ResourceLocation, AnimationDefinition> getAnimations() {
			return animations;
		}
	}
}