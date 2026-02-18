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

	private static TextureAndModelInformation parseModel(JsonElement e) {
		if (e == null || e.isJsonNull()) return null;

		if (e.isJsonPrimitive()) {
			JsonPrimitive p = e.getAsJsonPrimitive();
			if (!p.isString()) throw new IllegalArgumentException("Model must be a string Identifier or an object");
			TextureAndModelInformation info = new TextureAndModelInformation();
			info.parent = Identifier.parse(p.getAsString()); // fail fast
			return info;
		}

		if (!e.isJsonObject()) throw new IllegalArgumentException("Model must be a string Identifier or an object");

		JsonObject o = e.getAsJsonObject();
		TextureAndModelInformation info = new TextureAndModelInformation();

		Identifier parent = readIdentifier(o, "parent");
		if (parent == null) parent = readIdentifier(o, "model");
		if (parent == null) parent = readIdentifier(o, "id");
		info.parent = parent;

		info.textures = readTextures(o, "textures");
		return info;
	}

	private static TextureAndModelInformation[] parseModelArray(JsonElement e) {
		if (e == null || e.isJsonNull()) return null;
		if (!e.isJsonArray()) throw new IllegalArgumentException("Model array must be a JSON array");

		var arr = e.getAsJsonArray();
		TextureAndModelInformation[] out = new TextureAndModelInformation[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			out[i] = parseModel(arr.get(i));
		}
		return out;
	}

	private static Identifier readIdentifier(JsonObject o, String key) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString())
			throw new IllegalArgumentException("Field '" + key + "' must be a string Identifier");
		return Identifier.parse(e.getAsString());
	}

	private static Map<String, Identifier> readTextures(JsonObject o, String key) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonObject()) throw new IllegalArgumentException("Field '" + key + "' must be an object");
		JsonObject texObj = e.getAsJsonObject();

		Map<String, Identifier> out = new LinkedHashMap<>();
		for (var entry : texObj.entrySet()) {
			JsonElement val = entry.getValue();
			if (val == null || val.isJsonNull()) continue;
			if (!val.isJsonPrimitive() || !val.getAsJsonPrimitive().isString())
				throw new IllegalArgumentException("Texture '" + entry.getKey() + "' must be a string Identifier");
			out.put(entry.getKey(), Identifier.parse(val.getAsString()));
		}
		return out;
	}

	public TextureAndModelInformation getItemModel() {
		return parseModel(itemModel);
	}

	public TextureAndModelInformation getBlockingModel() {
		return parseModel(blockingModel);
	}

	public boolean hasItemModelObject() {
		return itemModel != null && itemModel.isJsonObject();
	}

	public boolean hasItemModelString() {
		return itemModel != null && itemModel.isJsonPrimitive() && itemModel.getAsJsonPrimitive().isString();
	}

	public TextureAndModelInformation getCastModel() {
		return parseModel(castModel);
	}

	public TextureAndModelInformation getThrowingModel() {
		return parseModel(throwingModel);
	}

	public TextureAndModelInformation getArrowModel() {
		return parseModel(arrowModel);
	}

	public TextureAndModelInformation[] getPullingModels() {
		return parseModelArray(pullingModels);
	}

	public TextureAndModelInformation getChargedModel() {
		return parseModel(chargedModel);
	}

	public TextureAndModelInformation getFireworkModel() {
		return parseModel(fireworkModel);
	}

	public boolean hasBlockingModelObject() {
		return blockingModel != null && blockingModel.isJsonObject();
	}

	public boolean hasCastModelObject() {
		return castModel != null && castModel.isJsonObject();
	}

	public boolean hasThrowingModelObject() {
		return throwingModel != null && throwingModel.isJsonObject();
	}

	public boolean hasPullingModels() {
		return pullingModels != null && pullingModels.isJsonArray() && pullingModels.getAsJsonArray().size() > 0;
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
		// item/<path> + suffix
		Identifier base = Utils.prependToPath(itemId, "item/");
		return base.withSuffix(suffix);
	}
}