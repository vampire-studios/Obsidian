package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemDisplayInformation {

	@Deprecated
	public TextureAndModelInformation model;

	@SerializedName("blocking_model")
	@com.google.gson.annotations.SerializedName("blocking_model")
	public JsonElement blockingModel;

	@com.google.gson.annotations.SerializedName("item_model")
	@blue.endless.jankson.annotation.SerializedName("item_model")
	public JsonElement itemModel;

	@SerializedName("pulling_models")
	@com.google.gson.annotations.SerializedName("pulling_models")
	public JsonElement pullingModels; // array of string|object

	@SerializedName("charged_model")
	@com.google.gson.annotations.SerializedName("charged_model")
	public JsonElement chargedModel;  // string|object

	@SerializedName("firework_model")
	@com.google.gson.annotations.SerializedName("firework_model")
	public JsonElement fireworkModel; // string|object

	@SerializedName("cast_model")
	@com.google.gson.annotations.SerializedName("cast_model")
	public JsonElement castModel; // fishing rod

	@SerializedName("throwing_model")
	@com.google.gson.annotations.SerializedName("throwing_model")
	public JsonElement throwingModel; // trident

	@SerializedName("arrow_model")
	@com.google.gson.annotations.SerializedName("arrow_model")
	public JsonElement arrowModel; // crossbow (optional; if absent, chargedModel is used)

	// Pick a sensible default if inline object omits base model id.
	// If you prefer handheld or something else, change here.
	private static final Identifier DEFAULT_INLINE_PARENT = Identifier.parse("minecraft:item/generated");

	private static TextureAndModelInformation parseModel(JsonElement e, String fieldName) {
		if (e == null || e.isJsonNull()) return null;

		// String => direct reference (non-generated)
		if (e.isJsonPrimitive()) {
			JsonPrimitive p = e.getAsJsonPrimitive();
			if (!p.isString()) {
				throw new IllegalArgumentException("Field '" + fieldName + "' must be a string Identifier or an object");
			}
			TextureAndModelInformation info = new TextureAndModelInformation();
			info.parent = Identifier.parse(p.getAsString()); // fail fast
			info.inlineGenerated = false;
			return info;
		}

		if (!e.isJsonObject()) {
			throw new IllegalArgumentException("Field '" + fieldName + "' must be a string Identifier or an object");
		}

		// Object => inline-generated model (written to item/<id> by your generator)
		JsonObject o = e.getAsJsonObject();
		TextureAndModelInformation info = new TextureAndModelInformation();
		info.inlineGenerated = true;

		// Accept ONLY these aliases for base model id.
		// Removed "id" alias (conflicts with your legacy/optional item id cleanup).
		Identifier parent = readIdentifier(o, "parent", fieldName);
		if (parent == null) parent = readIdentifier(o, "model", fieldName);

		info.textures = readTextures(o, "textures", fieldName);

		// Enforce meaning:
		// - If user provided textures but no base model => default base parent
		// - If user provided neither => object is meaningless => throw
		if (parent == null) {
			if (info.textures != null && !info.textures.isEmpty()) {
				parent = DEFAULT_INLINE_PARENT;
			} else {
				throw new IllegalArgumentException(
						"Field '" + fieldName + "' object must contain 'parent'/'model' and/or a non-empty 'textures' object"
				);
			}
		}

		info.parent = parent;
		return info;
	}

	private static TextureAndModelInformation[] parseModelArray(JsonElement e, String fieldName) {
		if (e == null || e.isJsonNull()) return null;
		if (!e.isJsonArray()) throw new IllegalArgumentException("Field '" + fieldName + "' must be a JSON array");

		var arr = e.getAsJsonArray();
		TextureAndModelInformation[] out = new TextureAndModelInformation[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			JsonElement el = arr.get(i);
			if (el == null || el.isJsonNull()) {
				throw new IllegalArgumentException("Field '" + fieldName + "' contains null at index " + i);
			}
			out[i] = parseModel(el, fieldName + "[" + i + "]");
		}
		return out;
	}

	private static Identifier readIdentifier(JsonObject o, String key, String fieldName) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
			throw new IllegalArgumentException(
					"Field '" + fieldName + "." + key + "' must be a string Identifier"
			);
		}
		return Identifier.parse(e.getAsString());
	}

	private static Map<String, Identifier> readTextures(JsonObject o, String key, String fieldName) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonObject()) {
			throw new IllegalArgumentException(
					"Field '" + fieldName + "." + key + "' must be an object"
			);
		}
		JsonObject texObj = e.getAsJsonObject();

		Map<String, Identifier> out = new LinkedHashMap<>();
		for (var entry : texObj.entrySet()) {
			JsonElement val = entry.getValue();
			if (val == null || val.isJsonNull()) continue;
			if (!val.isJsonPrimitive() || !val.getAsJsonPrimitive().isString()) {
				throw new IllegalArgumentException(
						"Texture '" + fieldName + "." + key + "." + entry.getKey() + "' must be a string Identifier"
				);
			}
			out.put(entry.getKey(), Identifier.parse(val.getAsString()));
		}
		return out;
	}

	public TextureAndModelInformation getItemModel() {
		return parseModel(itemModel, "item_model");
	}

	public TextureAndModelInformation getBlockingModel() {
		return parseModel(blockingModel, "blocking_model");
	}

	public boolean hasItemModelObject() {
		return itemModel != null && !itemModel.isJsonNull() && itemModel.isJsonObject();
	}

	public boolean hasItemModelString() {
		return itemModel != null && !itemModel.isJsonNull() && itemModel.isJsonPrimitive() && itemModel.getAsJsonPrimitive().isString();
	}

	public TextureAndModelInformation getCastModel() {
		return parseModel(castModel, "cast_model");
	}

	public TextureAndModelInformation getThrowingModel() {
		return parseModel(throwingModel, "throwing_model");
	}

	public TextureAndModelInformation getArrowModel() {
		return parseModel(arrowModel, "arrow_model");
	}

	public TextureAndModelInformation[] getPullingModels() {
		return parseModelArray(pullingModels, "pulling_models");
	}

	public TextureAndModelInformation getChargedModel() {
		return parseModel(chargedModel, "charged_model");
	}

	public TextureAndModelInformation getFireworkModel() {
		return parseModel(fireworkModel, "firework_model");
	}

	public boolean hasBlockingModelObject() {
		return blockingModel != null && !blockingModel.isJsonNull() && blockingModel.isJsonObject();
	}

	public boolean hasCastModelObject() {
		return castModel != null && !castModel.isJsonNull() && castModel.isJsonObject();
	}

	public boolean hasThrowingModelObject() {
		return throwingModel != null && !throwingModel.isJsonNull() && throwingModel.isJsonObject();
	}

	public boolean hasPullingModels() {
		return pullingModels != null
				&& !pullingModels.isJsonNull()
				&& pullingModels.isJsonArray()
				&& pullingModels.getAsJsonArray().size() > 0;
	}

	public boolean hasChargedModel() {
		return chargedModel != null && !chargedModel.isJsonNull();
	}

	public boolean hasFireworkModel() {
		return fireworkModel != null && !fireworkModel.isJsonNull();
	}

	public Identifier resolveItemDefinitionModelId(Identifier itemId) {
		Identifier generatedId = Utils.prependToPath(itemId, "item/");

		if (itemModel != null && !itemModel.isJsonNull()) {
			if (hasItemModelString()) return Identifier.parse(itemModel.getAsString());
			return generatedId; // object => generated model at item/<id>
		}

		return generatedId;
	}

	public Identifier variantModelId(Identifier itemId, String suffix) {
		Identifier base = Utils.prependToPath(itemId, "item/");
		return base.withSuffix(suffix);
	}
}
