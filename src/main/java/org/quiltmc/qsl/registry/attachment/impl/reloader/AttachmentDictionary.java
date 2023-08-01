/*
 * Copyright 2021 The Quilt Project
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

package org.quiltmc.qsl.registry.attachment.impl.reloader;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

import java.io.InputStreamReader;
import java.util.Map;

import static org.quiltmc.qsl.registry.attachment.impl.reloader.RegistryEntryAttachmentReloader.LOGGER;

final class AttachmentDictionary<R, V> {
	private final Registry<R> registry;
	private final RegistryEntryAttachment<R, V> attachment;
	private final Map<ValueTarget, Object> map;
	private final Map<ResourceLocation, ResourceLocation> mirrors, tagMirrors;

	AttachmentDictionary(Registry<R> registry, RegistryEntryAttachment<R, V> attachment) {
		this.registry = registry;
		this.attachment = attachment;
		this.map = new Object2ReferenceOpenHashMap<>();
		this.mirrors = new Object2ObjectOpenHashMap<>();
		this.tagMirrors = new Object2ObjectOpenHashMap<>();
	}

	public void put(ResourceLocation id, Object value) {
		this.map.put(new ValueTarget(id, ValueTarget.Type.ENTRY), value);
	}

	public void putTag(ResourceLocation id, Object value) {
		this.map.put(new ValueTarget(id, ValueTarget.Type.TAG), value);
	}

	public Registry<?> getRegistry() {
		return this.registry;
	}

	public RegistryEntryAttachment<?, ?> getAttachment() {
		return this.attachment;
	}

	public Map<ValueTarget, Object> getMap() {
		return this.map;
	}

	public Map<ResourceLocation, ResourceLocation> getMirrors() {
		return mirrors;
	}

	public Map<ResourceLocation, ResourceLocation> getTagMirrors() {
		return tagMirrors;
	}

	public void processResource(ResourceLocation resourceId, Resource resource) {
		try {
			boolean replace;
			JsonElement values;
			boolean replaceMirrors;
			JsonObject mirrors;
			boolean replaceTagMirrors;
			JsonObject tagMirrors;

			try (var reader = new InputStreamReader(resource.open())) {
				JsonObject obj = GsonHelper.parse(reader);
				replace = GsonHelper.getAsBoolean(obj, "replace", false);
				values = obj.get("values");

				if (values == null) {
					throw new JsonSyntaxException("Missing values, expected to find an array or object");
				} else if (!values.isJsonArray() && !values.isJsonObject()) {
					throw new JsonSyntaxException("Expected values to be an array or object, was "
							+ GsonHelper.getType(values));
				}

				replaceMirrors = GsonHelper.getAsBoolean(obj, "replace_mirrors", false);
				mirrors = GsonHelper.getAsJsonObject(obj, "mirrors", null);
				replaceTagMirrors = GsonHelper.getAsBoolean(obj, "replace_tag_mirrors", false);
				tagMirrors = GsonHelper.getAsJsonObject(obj, "tag_mirrors", null);
			} catch (JsonSyntaxException e) {
				LOGGER.error("Invalid JSON file '" + resourceId + "', ignoring", e);
				return;
			}

			// if "replace" is true, the data file wants us to clear all entries from other files before it
			if (replace) {
				this.map.clear();
			}

			if (values.isJsonArray()) {
				this.handleArray(resourceId, values.getAsJsonArray());
			} else if (values.isJsonObject()) {
				this.handleObject(resourceId, values.getAsJsonObject());
			}

			if (mirrors != null) {
				if (replaceMirrors) {
					this.mirrors.clear();
				}

				this.handleMirrors(this.mirrors, resourceId, mirrors, true);
			}

			if (tagMirrors != null) {
				if (replaceTagMirrors) {
					this.tagMirrors.clear();
				}

				this.handleMirrors(this.tagMirrors, resourceId, tagMirrors, false);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while parsing '" + resourceId + "'!", e);
		}
	}

	private void handleArray(ResourceLocation resourceId, JsonArray values) {
		for (int i = 0; i < values.size(); i++) {
			JsonElement entry = values.get(i);

			if (!entry.isJsonObject()) {
				LOGGER.error("Invalid element at index {} in values of '{}': expected an object, was {}",
						i, resourceId, GsonHelper.getType(entry));
				continue;
			}

			JsonObject entryO = entry.getAsJsonObject();
			ResourceLocation id;
			boolean isTag = false;
			JsonElement value;
			final boolean required = GsonHelper.getAsBoolean(entryO, "required", true); // For arrays the ? syntax is not handled.

			try {
				String idStr;

				if (entryO.has("id")) {
					idStr = GsonHelper.getAsString(entryO, "id");
				} else if (entryO.has("tag")) {
					isTag = true;
					idStr = GsonHelper.getAsString(entryO, "tag");
				} else {
					throw new JsonSyntaxException("Expected id or tag, got neither");
				}

				id = new ResourceLocation(idStr);
			} catch (JsonSyntaxException e) {
				LOGGER.error("Invalid element at index {} in values of '{}': syntax error",
						i, resourceId);
				LOGGER.error("", e);
				continue;
			} catch (ResourceLocationException e) {
				LOGGER.error("Invalid element at index {} in values of '{}': invalid identifier",
						i, resourceId);
				LOGGER.error("", e);
				continue;
			}

			try {
				value = entryO.get("value");
				if (value == null) {
					throw new JsonSyntaxException("Missing value");
				}
			} catch (JsonSyntaxException e) {
				LOGGER.error("Failed to parse value for registry entry {} in values of '{}': syntax error",
						id, resourceId);
				LOGGER.error("", e);
				continue;
			}

			this.handleEntry(resourceId, id, isTag, required, value);
		}
	}

	private void handleObject(ResourceLocation resourceId, JsonObject values) {
		for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
			ResourceLocation id;
			boolean isTag = false;
			boolean required = true;

			try {
				String idStr = entry.getKey();

				if (idStr.startsWith("#")) {
					isTag = true;
					idStr = idStr.substring(1);
				}

				if (idStr.endsWith("?")) {
					required = false;
					idStr = idStr.substring(0, idStr.length() - 1);
				}

				id = new ResourceLocation(idStr);
			} catch (ResourceLocationException e) {
				LOGGER.error("Invalid identifier in values of '{}': '{}', ignoring",
						resourceId, entry.getKey());
				LOGGER.error("", e);
				continue;
			}

			this.handleEntry(resourceId, id, isTag, required, entry.getValue());
		}
	}

	private void handleEntry(ResourceLocation resourceId, ResourceLocation keyId, boolean isTag, boolean required, JsonElement value) {
		if (isTag) {
			if (!required) {
				LOGGER.warn("Tag entry {} in '{}' is redundantly marked as optional (all tag entries are optional)",
						keyId, resourceId);
			}
		} else if (!this.registry.containsKey(keyId)) {
			if (required) {
				// log an error
				// vanilla tags throw but that causes way more breakage
				LOGGER.error("Unregistered identifier in values of '{}': '{}', ignoring", resourceId, keyId);
			}

			// either way, drop the entry
			return;
		}

		DataResult<?> parseResult = this.attachment.codec().parse(JsonOps.INSTANCE, value);

		if (parseResult.result().isEmpty()) {
			if (parseResult.error().isPresent()) {
				LOGGER.error("Failed to parse value for attachment {} of registry entry {}: {}",
						this.attachment.id(), keyId, parseResult.error().get().message());
			} else {
				LOGGER.error("Failed to parse value for attachment {} of registry entry {}: unknown error",
						this.attachment.id(), keyId);
			}

			LOGGER.error("Ignoring attachment value for {} in '{}' since it's invalid", keyId, resourceId);
			return;
		}

		Object parsedValue = parseResult.result().get();
		if (isTag) {
			this.putTag(keyId, parsedValue);
		} else {
			this.put(keyId, parsedValue);
		}
	}

	private void handleMirrors(Map<ResourceLocation, ResourceLocation> map, ResourceLocation resourceId, JsonObject mirrors,
							   boolean checkRegistry) {
		for (Map.Entry<String, JsonElement> entry : mirrors.entrySet()) {
			ResourceLocation target;
			try {
				target = new ResourceLocation(entry.getKey());
			} catch (ResourceLocationException e) {
				LOGGER.error("Invalid identifier in mirrors of {}: '{}', ignoring",
						resourceId, entry.getKey());
				LOGGER.error("", e);
				continue;
			}

			if (checkRegistry && !this.registry.containsKey(target)) {
				continue;
			}

			if (entry.getValue() instanceof JsonPrimitive prim && prim.isString()) {
				ResourceLocation source;
				try {
					source = new ResourceLocation(prim.getAsString());
				} catch (ResourceLocationException e) {
					LOGGER.error("Invalid mirror '{}' in {}: invalid source identifier, ignoring",
							target, resourceId);
					LOGGER.error("", e);
					continue;
				}

				if (checkRegistry && !this.registry.containsKey(source)) {
					continue;
				}

				if (source.equals(target)) {
					LOGGER.error("Invalid mirror '{}' in {}: can't mirror self, ignoring",
							target, resourceId);
				}

				map.put(target, source);
			} else {
				LOGGER.error("Invalid mirror '{}' in {}: expected string, got {}; ignoring",
						target, resourceId, GsonHelper.getType(entry.getValue()));
			}
		}
	}

	public record ValueTarget(ResourceLocation id, Type type) {
		enum Type {
			ENTRY, TAG;
		}
	}
}