/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vampirestudios.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.animation.Codecs;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicEntityModelLoader implements SimpleResourceReloadListener<DynamicEntityModelLoader.ModelLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("Quilt Entity Model Manager");
	private Map<ModelLayerLocation, LayerDefinition> modelData;

	public LayerDefinition getModelData(ModelLayerLocation layer) {
		return modelData.get(layer);
	}

	@Override
	public CompletableFuture<ModelLoader> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new ModelLoader(manager, profiler), executor);
	}

	@Override
	public CompletableFuture<Void> apply(ModelLoader prepared, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		this.modelData = prepared.getModelData();
		return CompletableFuture.runAsync(() -> {
		});
	}

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation("quilt_entity_models", "entity_model_reloader");
	}

	public static class ModelLoader {
		private static final Pattern PATH_AND_NAME_PATTERN = Pattern.compile("entity/model/(\\w*)/(\\w*)\\.json");

		private final ResourceManager manager;
		private final ProfilerFiller profiler;
		private final Map<ModelLayerLocation, LayerDefinition> modelData = new HashMap<>();

		public ModelLoader(ResourceManager manager, ProfilerFiller profiler) {
			this.manager = manager;
			this.profiler = profiler;
			loadAnimations();
		}

		private void loadAnimations() {
			profiler.push("Load Entity Models");
			Map<ResourceLocation, Resource> resources = manager.listResources("model/entity", id -> id.getPath().endsWith(".json"));
			for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
				addModel(entry.getKey(), entry.getValue());
			}
			profiler.pop();
		}

		private void addModel(ResourceLocation id, Resource resource) {
			BufferedReader reader;
			try {
				reader = resource.openAsReader();
			} catch (IOException e) {
				LOGGER.error(String.format("Unable to open BufferedReader for id %s", id), e);
				return;
			}

			JsonObject json = GsonHelper.parse(reader);
			DataResult<Pair<LayerDefinition, JsonElement>> result = Codecs.Model.TEXTURED_MODEL_DATA.decode(JsonOps.INSTANCE, json);

			if (result.error().isPresent()) {
				LOGGER.error(String.format("Unable to parse entity model file %s.\nReason: %s", id, result.error().get().message()));
				return;
			}

			Matcher matcher = PATH_AND_NAME_PATTERN.matcher(id.getPath());
			if (!matcher.find()) {
				LOGGER.error(String.format("Unable create model layer for entity model file %s.", id));
				return;
			}

			String path = matcher.group(1);
			String name = matcher.group(2);

			ResourceLocation modelID = new ResourceLocation(id.getNamespace(), path);
			modelData.put(new ModelLayerLocation(modelID, name), result.result().get().getFirst());
		}

		public Map<ModelLayerLocation, LayerDefinition> getModelData() {
			return modelData;
		}
	}
}