package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.block.MultiBlockVariants;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementVariants;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisplayInformation {

	@SerializedName("block_model")
	public JsonElement blockModel;

	@SerializedName("powered_model")
	public JsonElement poweredModel;

	@Deprecated
	public JsonElement model;

	public TextureAndModelInformation hangingModel;
	public TextureAndModelInformation trapdoorBottomModel;
	public TextureAndModelInformation trapdoorOpenModel;
	public TextureAndModelInformation trapdoorTopModel;
	public TextureAndModelInformation doorBottomModel;
	public TextureAndModelInformation doorBottomHingeModel;
	public TextureAndModelInformation doorTopModel;
	public TextureAndModelInformation doorTopHingeModel;
	public TextureAndModelInformation onModel;
	public TextureAndModelInformation offModel;
	public TextureAndModelInformation stickyPiston;

	/**
	 * Per-placement models, picked by the {@code placement} block state property. Each entry takes the same
	 * forms as {@code block_model} — a model identifier, or an object with a parent and textures.
	 */
	@SerializedName("placement_models")
	public PlacementModels placementModels;

	/**
	 * Per-cell models of a multi-block, keyed by cell name ({@code "<x>_<y>_<z>"}). Each entry takes the same
	 * forms as {@code block_model}.
	 */
	@SerializedName("part_models")
	public Map<String, JsonElement> partModels;

	/** Models for the rotation blocks, keyed by the value of their {@code rotation} property. */
	@SerializedName("rotation_models")
	public Map<String, Identifier> rotationModels;

	public Map<Integer, Identifier> getRotationModels() {
		if (rotationModels == null || rotationModels.isEmpty()) return Map.of();

		Map<Integer, Identifier> resolved = new LinkedHashMap<>();
		rotationModels.forEach((key, model) -> {
			try {
				if (model != null) resolved.put(Integer.parseInt(key.trim()), model);
			} catch (NumberFormatException ignored) {
			}
		});
		return resolved;
	}

	/**
	 * Per-variant powered models, keyed by placement name or cell name. Used in place of the block-wide
	 * {@code powered_model} when a powerable or toggleable block also has variants, so each of them can
	 * light up differently.
	 */
	@SerializedName("powered_models")
	public Map<String, JsonElement> poweredModels;

	@SerializedName("item_model")
	public JsonElement itemModel;

	@SerializedName("block_state")
	public BlockProperty blockState;

	/**
	 * Extra clockwise Y-rotation (in degrees, multiples of 90) applied to the model
	 * for all facing variants of HORIZONTAL_DIRECTIONAL and DIRECTIONAL blocks.
	 * Use 90 to rotate the model one step clockwise relative to the default orientation.
	 */
	@SerializedName("model_rotation_offset")
	public int model_rotation_offset = 0;

	public TextureAndModelInformation getBlockModel() {
		return parseModel(blockModel);
	}

	public TextureAndModelInformation getPoweredModel() {
		return parseModel(poweredModel);
	}

	public TextureAndModelInformation getItemModel() {
		return parseModel(itemModel);
	}

	public TextureAndModelInformation getModel() {
		return parseModel(model);
	}

	public boolean hasItemModelObject() {
		return itemModel != null && itemModel.isJsonObject();
	}

	public boolean hasItemModelString() {
		return itemModel != null && itemModel.isJsonPrimitive() && itemModel.getAsJsonPrimitive().isString();
	}

	public boolean hasBlockModelObject() {
		return blockModel != null && blockModel.isJsonObject();
	}

	public boolean hasBlockModelString() {
		return blockModel != null && blockModel.isJsonPrimitive() && blockModel.getAsJsonPrimitive().isString();
	}

	public boolean hasPoweredModelObject() {
		return poweredModel != null && poweredModel.isJsonObject();
	}

	public boolean hasPoweredModelString() {
		return poweredModel != null && poweredModel.isJsonPrimitive() && poweredModel.getAsJsonPrimitive().isString();
	}

	public JsonElement placementModel(String placement) {
		return placementModels == null ? null : placementModels.get(placement);
	}

	public TextureAndModelInformation getPlacementModel(String placement) {
		return parseModel(placementModel(placement));
	}

	public boolean hasPlacementModelObject(String placement) {
		JsonElement e = placementModel(placement);
		return e != null && e.isJsonObject();
	}

	public boolean hasPlacementModelString(String placement) {
		JsonElement e = placementModel(placement);
		return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isString();
	}

	public JsonElement partModel(String part) {
		return partModels == null ? null : partModels.get(part);
	}

	public JsonElement poweredVariantModel(String variant) {
		return poweredModels == null || variant == null ? null : poweredModels.get(variant);
	}

	public TextureAndModelInformation getPoweredVariantModel(String variant) {
		return parseModel(poweredVariantModel(variant));
	}

	public boolean hasPoweredVariantModelObject(String variant) {
		JsonElement e = poweredVariantModel(variant);
		return e != null && e.isJsonObject();
	}

	public TextureAndModelInformation getPartModel(String part) {
		return parseModel(partModel(part));
	}

	public boolean hasPartModelObject(String part) {
		JsonElement e = partModel(part);
		return e != null && e.isJsonObject();
	}

	public boolean hasPartModelString(String part) {
		JsonElement e = partModel(part);
		return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isString();
	}

	public boolean hasLegacyModelObject() {
		return model != null && model.isJsonObject();
	}

	public boolean hasLegacyModelString() {
		return model != null && model.isJsonPrimitive() && model.getAsJsonPrimitive().isString();
	}

	/**
	 * Accepts:
	 * - "namespace:path"
	 * - { "parent": "namespace:path", "textures": { "0": "...", "particle": "..." } }
	 * Also tolerates aliases:
	 * - { "model": "..." } or { "id": "..." } treated as parent
	 */
	/** Reads one model declaration in any of the forms {@code block_model} accepts. */
	public static TextureAndModelInformation parseModelDeclaration(JsonElement declaration) {
		return parseModel(declaration);
	}

	private static TextureAndModelInformation parseModel(JsonElement e) {
		if (e == null || e.isJsonNull()) return null;

		if (e.isJsonPrimitive()) {
			JsonPrimitive p = e.getAsJsonPrimitive();
			if (!p.isString()) {
				throw new IllegalArgumentException("Model must be a string Identifier or an object");
			}
			TextureAndModelInformation info = new TextureAndModelInformation();
			String raw = p.getAsString();
			// Unqualified paths like "block/cube_all" are vanilla model parents; prepend "minecraft:"
			if (!raw.contains(":")) {
				raw = "minecraft:" + raw;
			}
			info.parent = Identifier.parse(raw);
			return info;
		}

		if (!e.isJsonObject()) {
			throw new IllegalArgumentException("Model must be a string Identifier or an object");
		}

		JsonObject o = e.getAsJsonObject();
		TextureAndModelInformation info = new TextureAndModelInformation();

		Identifier parent = readIdentifier(o, "parent");
		if (parent == null) parent = readIdentifier(o, "model");
		if (parent == null) parent = readIdentifier(o, "id");
		info.parent = parent;

		info.textures = readTextures(o, "textures");

		return info;
	}

	private static Identifier readIdentifier(JsonObject o, String key) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
			throw new IllegalArgumentException("Field '" + key + "' must be a string Identifier");
		}
		String raw = e.getAsString();
		// Unqualified paths like "block/cube_all" are vanilla model parents; prepend "minecraft:"
		if (!raw.contains(":")) {
			raw = "minecraft:" + raw;
		}
		return Identifier.parse(raw);
	}

	private static Map<String, Identifier> readTextures(JsonObject o, String key) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;

		JsonElement e = o.get(key);
		if (!e.isJsonObject()) {
			throw new IllegalArgumentException("Field '" + key + "' must be an object of texture variables");
		}

		JsonObject texObj = e.getAsJsonObject();
		Map<String, Identifier> out = new LinkedHashMap<>();

		for (Map.Entry<String, JsonElement> entry : texObj.entrySet()) {
			String var = entry.getKey();
			JsonElement val = entry.getValue();

			if (val == null || val.isJsonNull()) continue;

			if (!val.isJsonPrimitive() || !val.getAsJsonPrimitive().isString()) {
				throw new IllegalArgumentException("Texture '" + var + "' must be a string Identifier");
			}

			out.put(var, Identifier.parse(val.getAsString()));
		}

		return out;
	}

	public Identifier resolveItemDefinitionModelId(Identifier blockId) {
		// output ids (where YOUR generator writes model json)
		Identifier outBlockModel = Utils.prependToPath(blockId, "block/");
		Identifier outItemModel = Utils.prependToPath(blockId, "item/");

		// 1) item_model present:
		// - string => direct reference (already-existing model id)
		// - object => you will generate models/item/<id>.json, so reference that output id
		if (itemModel != null && !itemModel.isJsonNull()) {
			if (hasItemModelString()) return Identifier.parse(itemModel.getAsString());
			return outItemModel; // object
		}

		// 2) block_model present:
		// - string => direct reference
		// - object => you generate block model at outBlockModel, so reference that output id
		if (blockModel != null && !blockModel.isJsonNull()) {
			if (hasBlockModelString()) return Identifier.parse(blockModel.getAsString());
			return outBlockModel;
		}

		// 3) legacy model present:
		if (model != null && !model.isJsonNull()) {
			if (hasLegacyModelString()) return Identifier.parse(model.getAsString());
			return outBlockModel;
		}

		// 4) variant models only: no models/block/<id> is written in that case, so the block item shows the
		// variant the player gets first — the origin cell of a multi-block, else the first placement.
		if (partModels != null) {
			Identifier origin = variantModelId(partModels.get(MultiBlockVariants.ORIGIN), blockId, "_" + MultiBlockVariants.ORIGIN);
			if (origin != null) return origin;
		}
		if (placementModels != null) {
			for (String placement : List.of(PlacementVariants.FLOOR, PlacementVariants.WALL, PlacementVariants.CEILING)) {
				Identifier resolved = variantModelId(placementModels.get(placement), blockId, "_" + placement);
				if (resolved != null) return resolved;
			}
		}

		return outBlockModel;
	}

	/** Where a variant's model ends up: referenced directly when given as a string, generated when given inline. */
	private static Identifier variantModelId(JsonElement declaration, Identifier blockId, String suffix) {
		if (declaration == null || declaration.isJsonNull()) return null;
		if (declaration.isJsonPrimitive() && declaration.getAsJsonPrimitive().isString()) {
			return Identifier.parse(declaration.getAsString());
		}
		return Utils.appendAndPrependToPath(blockId, "block/", suffix);
	}

	public static class PlacementModels {
		public JsonElement floor;
		public JsonElement wall;
		public JsonElement ceiling;

		public JsonElement get(String placement) {
			return switch (placement) {
				case PlacementVariants.FLOOR -> floor;
				case PlacementVariants.WALL -> wall;
				case PlacementVariants.CEILING -> ceiling;
				default -> null;
			};
		}
	}

	public static class Property {
		public Identifier model;
		public int x;
		public int y;
		public int z;
	}

}
