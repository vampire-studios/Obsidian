package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Item display/rendering configuration.
 *
 * <h2>JSON model spec format</h2>
 * Most model fields accept either:
 * <ul>
 *   <li><b>String</b> (Identifier): a direct reference to an existing model id</li>
 *   <li><b>Object</b>: an inline model definition that your pipeline will generate into
 *       {@code item/<itemId>} (via {@link #resolveItemDefinitionModelId(Identifier)}).</li>
 * </ul>
 *
 * <h3>Object form</h3>
 * Supported keys:
 * <ul>
 *   <li>{@code "model"} or {@code "parent"}: base model id (written as JSON "parent")</li>
 *   <li>{@code "textures"}: optional texture overrides</li>
 * </ul>
 *
 * If an object omits {@code model/parent} but provides non-empty {@code textures},
 * a default base parent is applied ({@link #DEFAULT_INLINE_PARENT}).
 *
 * <h2>Generation rule</h2>
 * For {@code item_model} specifically:
 * <ul>
 *   <li>string => use that model id directly</li>
 *   <li>object => generate a model at {@code item/<itemId>}</li>
 *   <li>null/missing => fallback to {@code item/<itemId>}</li>
 * </ul>
 */
public class ItemDisplayInformation {

	// ----------------------------
	// JSON field name constants
	// ----------------------------

	private static final String F_ITEM_MODEL      = "item_model";
	private static final String F_BLOCKING_MODEL  = "blocking_model";
	private static final String F_PULLING_MODELS  = "pulling_models";
	private static final String F_CHARGED_MODEL   = "charged_model";
	private static final String F_FIREWORK_MODEL  = "firework_model";
	private static final String F_CAST_MODEL      = "cast_model";
	private static final String F_THROWING_MODEL  = "throwing_model";
	private static final String F_ARROW_MODEL     = "arrow_model";
	private static final String F_BROKEN_MODEL   = "broken_model";
	private static final String F_DAMAGED_MODELS = "damaged_models";
	private static final String F_COOLDOWN_MODEL = "cooldown_model";
	private static final String F_CHARGING_MODELS = "charging_models";
	private static final String F_USE_MODELS      = "use_models";
	private static final String F_BINARY_SELECTS = "binary_selects";

	private static final String K_PARENT   = "parent";
	private static final String K_MODEL    = "model";
	private static final String K_TEXTURES = "textures";

	/**
	 * Default base model used when an inline object only supplies textures.
	 * Change if your project prefers another base (e.g. handheld).
	 */
	private static final Identifier DEFAULT_INLINE_PARENT = Identifier.parse("minecraft:item/generated");

	// ----------------------------
	// Raw JSON (Gson/Jankson)
	// ----------------------------

	/** @deprecated Legacy combined model field. Prefer the explicit per-state fields below. */
	@Deprecated
	public TextureAndModelInformation model;

	@SerializedName(F_BLOCKING_MODEL)
	@com.google.gson.annotations.SerializedName(F_BLOCKING_MODEL)
	public JsonElement blockingModel;

	@SerializedName(F_ITEM_MODEL)
	@com.google.gson.annotations.SerializedName(F_ITEM_MODEL)
	public JsonElement itemModel;

	@SerializedName(F_PULLING_MODELS)
	@com.google.gson.annotations.SerializedName(F_PULLING_MODELS)
	public JsonElement pullingModels; // array of string|object

	@SerializedName(F_CHARGED_MODEL)
	@com.google.gson.annotations.SerializedName(F_CHARGED_MODEL)
	public JsonElement chargedModel;  // string|object

	@SerializedName(F_FIREWORK_MODEL)
	@com.google.gson.annotations.SerializedName(F_FIREWORK_MODEL)
	public JsonElement fireworkModel; // string|object

	@SerializedName(F_CAST_MODEL)
	@com.google.gson.annotations.SerializedName(F_CAST_MODEL)
	public JsonElement castModel; // fishing rod

	@SerializedName(F_THROWING_MODEL)
	@com.google.gson.annotations.SerializedName(F_THROWING_MODEL)
	public JsonElement throwingModel; // trident

	@SerializedName(F_ARROW_MODEL)
	@com.google.gson.annotations.SerializedName(F_ARROW_MODEL)
	public JsonElement arrowModel; // crossbow (optional; if absent, chargedModel is used)

	@SerializedName(F_BROKEN_MODEL)
	@com.google.gson.annotations.SerializedName(F_BROKEN_MODEL)
	public JsonElement brokenModel; // string|object

	@SerializedName(F_DAMAGED_MODELS)
	@com.google.gson.annotations.SerializedName(F_DAMAGED_MODELS)
	public JsonElement damagedModels; // array of string|object

	@SerializedName(F_COOLDOWN_MODEL)
	@com.google.gson.annotations.SerializedName(F_COOLDOWN_MODEL)
	public JsonElement cooldownModel; // string|object

	@SerializedName(F_CHARGING_MODELS)
	@com.google.gson.annotations.SerializedName(F_CHARGING_MODELS)
	public JsonElement chargingModels; // array of string|object

	@SerializedName(F_USE_MODELS)
	@com.google.gson.annotations.SerializedName(F_USE_MODELS)
	public JsonElement useModels; // object map: action -> string|object

	@SerializedName(F_BINARY_SELECTS)
	@com.google.gson.annotations.SerializedName(F_BINARY_SELECTS)
	public JsonElement binarySelects; // object map: name -> { property, cases: [{when,bool, model}] }

	// ----------------------------
	// Parsed caches (avoid reparsing)
	// ----------------------------

	// Mark transient so serializers ignore them.
	private transient TextureAndModelInformation cachedItemModel;
	private transient TextureAndModelInformation cachedBlockingModel;
	private transient TextureAndModelInformation cachedChargedModel;
	private transient TextureAndModelInformation cachedFireworkModel;
	private transient TextureAndModelInformation cachedCastModel;
	private transient TextureAndModelInformation cachedThrowingModel;
	private transient TextureAndModelInformation cachedArrowModel;
	private transient TextureAndModelInformation[] cachedPullingModels;
	private transient TextureAndModelInformation cachedBrokenModel;
	private transient TextureAndModelInformation[] cachedDamagedModels;
	private transient TextureAndModelInformation cachedCooldownModel;
	private transient TextureAndModelInformation[] cachedChargingModels;
	private transient Map<String, TextureAndModelInformation> cachedUseModels;
	private transient Map<String, BinarySelect> cachedBinarySelects;

	private transient boolean cachedItemModelSet;
	private transient boolean cachedBlockingModelSet;
	private transient boolean cachedChargedModelSet;
	private transient boolean cachedFireworkModelSet;
	private transient boolean cachedCastModelSet;
	private transient boolean cachedThrowingModelSet;
	private transient boolean cachedArrowModelSet;
	private transient boolean cachedPullingModelsSet;
	private transient boolean cachedBrokenModelSet;
	private transient boolean cachedDamagedModelsSet;
	private transient boolean cachedCooldownModelSet;
	private transient boolean cachedChargingModelsSet;
	private transient boolean cachedUseModelsSet;
	private transient boolean cachedBinarySelectsSet;

	// ----------------------------
	// Parsing
	// ----------------------------

	private static TextureAndModelInformation parseModel(JsonElement e, String fieldName) {
		if (e == null || e.isJsonNull()) return null;

		// String => direct reference
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

		// Object => inline generated
		JsonObject o = e.getAsJsonObject();
		TextureAndModelInformation info = new TextureAndModelInformation();
		info.inlineGenerated = true;

		// Only accept model/parent as base model id keys.
		Identifier parent = readIdentifier(o, K_PARENT, fieldName);
		if (parent == null) parent = readIdentifier(o, K_MODEL, fieldName);

		info.textures = readTextures(o, K_TEXTURES, fieldName);

		// Enforce meaning
		if (parent == null) {
			if (info.textures != null && !info.textures.isEmpty()) {
				parent = DEFAULT_INLINE_PARENT;
			} else {
				throw new IllegalArgumentException(
						"Field '" + fieldName + "' object must contain '" + K_MODEL + "'/'" + K_PARENT +
								"' and/or a non-empty '" + K_TEXTURES + "' object"
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
			throw new IllegalArgumentException("Field '" + fieldName + "." + key + "' must be a string Identifier");
		}
		return Identifier.parse(e.getAsString());
	}

	private static Map<String, Identifier> readTextures(JsonObject o, String key, String fieldName) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonObject()) {
			throw new IllegalArgumentException("Field '" + fieldName + "." + key + "' must be an object");
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

	private static Map<String, TextureAndModelInformation> parseModelMap(JsonElement e, String fieldName) {
		if (e == null || e.isJsonNull()) return null;
		if (!e.isJsonObject()) throw new IllegalArgumentException("Field '" + fieldName + "' must be an object");

		JsonObject o = e.getAsJsonObject();
		Map<String, TextureAndModelInformation> out = new LinkedHashMap<>();

		for (var entry : o.entrySet()) {
			String key = entry.getKey();
			JsonElement val = entry.getValue();
			if (val == null || val.isJsonNull()) continue;
			out.put(key, parseModel(val, fieldName + "." + key));
		}
		return out;
	}

	private static Map<String, BinarySelect> parseBinarySelects(JsonElement e, String fieldName) {
		if (e == null || e.isJsonNull()) return null;
		if (!e.isJsonObject()) throw new IllegalArgumentException("Field '" + fieldName + "' must be an object");

		JsonObject root = e.getAsJsonObject();
		Map<String, BinarySelect> out = new LinkedHashMap<>();

		for (var entry : root.entrySet()) {
			String name = entry.getKey();
			JsonElement val = entry.getValue();
			if (val == null || val.isJsonNull()) continue;
			if (!val.isJsonObject()) {
				throw new IllegalArgumentException("Field '" + fieldName + "." + name + "' must be an object");
			}

			JsonObject obj = val.getAsJsonObject();

			String property = readString(obj, "property", fieldName + "." + name);
			if (property == null || property.isBlank()) {
				throw new IllegalArgumentException("Field '" + fieldName + "." + name + ".property' must be a non-empty string");
			}

			JsonElement casesEl = obj.get("cases");
			if (casesEl == null || casesEl.isJsonNull() || !casesEl.isJsonArray()) {
				throw new IllegalArgumentException("Field '" + fieldName + "." + name + ".cases' must be an array");
			}

			TextureAndModelInformation whenTrue = null;
			TextureAndModelInformation whenFalse = null;

			var arr = casesEl.getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				JsonElement caseEl = arr.get(i);
				if (caseEl == null || caseEl.isJsonNull() || !caseEl.isJsonObject()) {
					throw new IllegalArgumentException("Field '" + fieldName + "." + name + ".cases[" + i + "]' must be an object");
				}

				JsonObject c = caseEl.getAsJsonObject();

				Boolean when = readBoolean(c, "when", fieldName + "." + name + ".cases[" + i + "]");
				if (when == null) {
					throw new IllegalArgumentException("Field '" + fieldName + "." + name + ".cases[" + i + "].when' must be boolean");
				}

				JsonElement modelEl = c.get("model");
				TextureAndModelInformation model = parseModel(modelEl, fieldName + "." + name + ".cases[" + i + "].model");
				if (model == null) {
					throw new IllegalArgumentException("Field '" + fieldName + "." + name + ".cases[" + i + "].model' is required");
				}

				if (when) whenTrue = model;
				else whenFalse = model;
			}

			if (whenTrue == null || whenFalse == null) {
				throw new IllegalArgumentException(
						"Field '" + fieldName + "." + name + "' must define both cases: when=true and when=false"
				);
			}

			out.put(name, new BinarySelect(property, whenTrue, whenFalse));
		}

		return out;
	}

	private static String readString(JsonObject o, String key, String ctx) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
			throw new IllegalArgumentException("Field '" + ctx + "." + key + "' must be a string");
		}
		return e.getAsString();
	}

	private static Boolean readBoolean(JsonObject o, String key, String ctx) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		JsonElement e = o.get(key);
		if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isBoolean()) {
			throw new IllegalArgumentException("Field '" + ctx + "." + key + "' must be a boolean");
		}
		return e.getAsBoolean();
	}

	// ----------------------------
	// Cached getters
	// ----------------------------

	public TextureAndModelInformation getItemModel() {
		if (!cachedItemModelSet) {
			cachedItemModel = parseModel(itemModel, F_ITEM_MODEL);
			cachedItemModelSet = true;
		}
		return cachedItemModel;
	}

	public TextureAndModelInformation getBlockingModel() {
		if (!cachedBlockingModelSet) {
			cachedBlockingModel = parseModel(blockingModel, F_BLOCKING_MODEL);
			cachedBlockingModelSet = true;
		}
		return cachedBlockingModel;
	}

	public TextureAndModelInformation[] getPullingModels() {
		if (!cachedPullingModelsSet) {
			cachedPullingModels = parseModelArray(pullingModels, F_PULLING_MODELS);
			cachedPullingModelsSet = true;
		}
		return cachedPullingModels;
	}

	public TextureAndModelInformation getChargedModel() {
		if (!cachedChargedModelSet) {
			cachedChargedModel = parseModel(chargedModel, F_CHARGED_MODEL);
			cachedChargedModelSet = true;
		}
		return cachedChargedModel;
	}

	public TextureAndModelInformation getFireworkModel() {
		if (!cachedFireworkModelSet) {
			cachedFireworkModel = parseModel(fireworkModel, F_FIREWORK_MODEL);
			cachedFireworkModelSet = true;
		}
		return cachedFireworkModel;
	}

	public TextureAndModelInformation getCastModel() {
		if (!cachedCastModelSet) {
			cachedCastModel = parseModel(castModel, F_CAST_MODEL);
			cachedCastModelSet = true;
		}
		return cachedCastModel;
	}

	public TextureAndModelInformation getThrowingModel() {
		if (!cachedThrowingModelSet) {
			cachedThrowingModel = parseModel(throwingModel, F_THROWING_MODEL);
			cachedThrowingModelSet = true;
		}
		return cachedThrowingModel;
	}

	public TextureAndModelInformation getArrowModel() {
		if (!cachedArrowModelSet) {
			cachedArrowModel = parseModel(arrowModel, F_ARROW_MODEL);
			cachedArrowModelSet = true;
		}
		return cachedArrowModel;
	}

	public TextureAndModelInformation getBrokenModel() {
		if (!cachedBrokenModelSet) {
			cachedBrokenModel = parseModel(brokenModel, F_BROKEN_MODEL);
			cachedBrokenModelSet = true;
		}
		return cachedBrokenModel;
	}

	public TextureAndModelInformation[] getDamagedModels() {
		if (!cachedDamagedModelsSet) {
			cachedDamagedModels = parseModelArray(damagedModels, F_DAMAGED_MODELS);
			cachedDamagedModelsSet = true;
		}
		return cachedDamagedModels;
	}

	public TextureAndModelInformation getCooldownModel() {
		if (!cachedCooldownModelSet) {
			cachedCooldownModel = parseModel(cooldownModel, F_COOLDOWN_MODEL);
			cachedCooldownModelSet = true;
		}
		return cachedCooldownModel;
	}

	public TextureAndModelInformation[] getChargingModels() {
		if (!cachedChargingModelsSet) {
			cachedChargingModels = parseModelArray(chargingModels, F_CHARGING_MODELS);
			cachedChargingModelsSet = true;
		}
		return cachedChargingModels;
	}

	public Map<String, TextureAndModelInformation> getUseModels() {
		if (!cachedUseModelsSet) {
			cachedUseModels = parseModelMap(useModels, F_USE_MODELS);
			cachedUseModelsSet = true;
		}
		return cachedUseModels;
	}

	public TextureAndModelInformation getUseModel(String action) {
		if (action == null) return null;
		Map<String, TextureAndModelInformation> map = getUseModels();
		if (map == null) return null;
		return map.get(action);
	}

	public Map<String, BinarySelect> getBinarySelects() {
		if (!cachedBinarySelectsSet) {
			cachedBinarySelects = parseBinarySelects(binarySelects, F_BINARY_SELECTS);
			cachedBinarySelectsSet = true;
		}
		return cachedBinarySelects;
	}

	public BinarySelect getBinarySelect(String name) {
		if (name == null) return null;
		Map<String, BinarySelect> map = getBinarySelects();
		if (map == null) return null;
		return map.get(name);
	}

	// ----------------------------
	// Consistent "has" helpers
	// ----------------------------

	private static boolean isPresent(JsonElement e) {
		return e != null && !e.isJsonNull();
	}

	private static boolean isString(JsonElement e) {
		return isPresent(e) && e.isJsonPrimitive() && e.getAsJsonPrimitive().isString();
	}

	private static boolean isObject(JsonElement e) {
		return isPresent(e) && e.isJsonObject();
	}

	public boolean hasItemModel()          { return isPresent(itemModel); }
	public boolean hasItemModelString()    { return isString(itemModel); }
	public boolean hasItemModelObject()    { return isObject(itemModel); }

	public boolean hasBlockingModel()      { return isPresent(blockingModel); }
	public boolean hasBlockingModelObject(){ return isObject(blockingModel); }

	public boolean hasCastModel()          { return isPresent(castModel); }
	public boolean hasCastModelObject()    { return isObject(castModel); }

	public boolean hasThrowingModel()      { return isPresent(throwingModel); }
	public boolean hasThrowingModelObject(){ return isObject(throwingModel); }

	public boolean hasChargedModel()       { return isPresent(chargedModel); }
	public boolean hasFireworkModel()      { return isPresent(fireworkModel); }

	public boolean hasPullingModels() {
		return pullingModels != null
				&& !pullingModels.isJsonNull()
				&& pullingModels.isJsonArray()
				&& !pullingModels.getAsJsonArray().isEmpty();
	}

	/**
	 * Resolves the "item definition model id" used by your rendering pipeline.
	 * <p>
	 * Contract:
	 * <ul>
	 *   <li>If {@code item_model} is a string => returns that Identifier</li>
	 *   <li>If {@code item_model} is an object => returns generated id {@code item/<itemId>}</li>
	 *   <li>If {@code item_model} is missing => returns fallback {@code item/<itemId>}</li>
	 * </ul>
	 */
	public Identifier resolveItemDefinitionModelId(Identifier itemId) {
		Objects.requireNonNull(itemId, "itemId");

		Identifier generatedId = Utils.prependToPath(itemId, "item/");

		if (isPresent(itemModel)) {
			if (hasItemModelString()) return Identifier.parse(itemModel.getAsString());
			return generatedId; // object => generated model at item/<id>
		}

		return generatedId;
	}

	/**
	 * Convenience helper for variant model ids (e.g. "_blocking", "_pulling_0", etc).
	 * Produces {@code item/<path> + suffix}.
	 */
	public Identifier variantModelId(Identifier itemId, String suffix) {
		Objects.requireNonNull(itemId, "itemId");
		Objects.requireNonNull(suffix, "suffix");

		Identifier base = Utils.prependToPath(itemId, "item/");
		return base.withSuffix(suffix);
	}

	public Identifier blockingModelId(Identifier itemId) { return variantModelId(itemId, "_blocking"); }
	public Identifier chargedModelId(Identifier itemId)  { return variantModelId(itemId, "_charged"); }
	public Identifier fireworkModelId(Identifier itemId) { return variantModelId(itemId, "_firework"); }
	public Identifier castModelId(Identifier itemId)     { return variantModelId(itemId, "_cast"); }
	public Identifier throwingModelId(Identifier itemId) { return variantModelId(itemId, "_throwing"); }
	public Identifier arrowModelId(Identifier itemId)    { return variantModelId(itemId, "_arrow"); }
	public Identifier pullingModelId(Identifier itemId, int idx) { return variantModelId(itemId, "_pulling_" + idx); }

	/**
	 * @param property  What to evaluate to a boolean (component/property key etc).
	 * @param whenTrue  Model for when the property is true/false. Both required.  */
	public record BinarySelect(String property, TextureAndModelInformation whenTrue,
							   TextureAndModelInformation whenFalse) {
		public TextureAndModelInformation pick(boolean value) {
			return value ? whenTrue : whenFalse;
		}
	}
}
