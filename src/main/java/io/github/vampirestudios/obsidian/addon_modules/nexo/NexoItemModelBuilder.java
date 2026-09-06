package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

/**
 * Writes Nexo's recursive {@code ItemModel} builder as a modern vanilla item definition.
 *
 * <p>The Nexo format intentionally mirrors the vanilla model tree. Keeping it as JSON here means
 * newly-added vanilla properties continue to pass through, while the few Nexo conveniences
 * (unqualified types, properties, components and model ids) are normalized in one place.</p>
 */
public final class NexoItemModelBuilder {
	private static final ObjectMapper JSON = new ObjectMapper();

	private NexoItemModelBuilder() {
	}

	public static boolean generate(RuntimeResourcePack resourcePack, NexoItem item) {
		if (item == null || item.id == null || item.itemModel == null || item.itemModel.isNull()) return false;
		try {
			ObjectNode definition = JSON.createObjectNode();
			JsonNode model = item.itemModel;
			if (model.isTextual()) {
				ObjectNode reference = JSON.createObjectNode();
				reference.put("type", "minecraft:model");
				reference.put("model", qualify(model.asText(), item.id.getNamespace()));
				model = reference;
			} else {
				model = normalize(model, item.id.getNamespace());
			}
			definition.set("model", model);
			Identifier output = configuredItemModel(item);
			Identifier path = Identifier.fromNamespaceAndPath(output.getNamespace(),
					"items/" + output.getPath() + ".json");
			resourcePack.addResource(PackType.CLIENT_RESOURCES, path,
					JSON.writerWithDefaultPrettyPrinter().writeValueAsBytes(definition));
			return true;
		} catch (Exception exception) {
			Obsidian.LOGGER.error("Failed to generate Nexo ItemModel for {}", item.id, exception);
			return false;
		}
	}

	/**
	 * Resolves the ordinary block/item model selected by an item definition for a known furniture
	 * state. This is the bridge that lets block-rendered furniture use Nexo ITEM_MODEL visuals
	 * without a block entity or a persistent display entity.
	 */
	public static Identifier resolveBlockModel(RuntimeResourcePack resourcePack, NexoItem owner,
	                                           Identifier itemModelId, Map<String, Object> properties) {
		if (itemModelId == null) return null;
		try {
			JsonNode definition = inlineDefinition(owner, itemModelId);
			if (definition == null) definition = readDefinition(resourcePack, itemModelId);
			if (definition == null) return null;
			JsonNode model = definition.has("model") && !definition.has("type")
					? definition.get("model") : definition;
			return resolve(model, properties != null ? properties : Map.of(), owner.id.getNamespace());
		} catch (Exception exception) {
			Obsidian.LOGGER.warn("Could not resolve Nexo item model {} for furniture {}: {}",
					itemModelId, owner.id, exception.getMessage());
			return null;
		}
	}

	public static Identifier configuredItemModel(NexoItem item) {
		if (item.components != null) {
			Identifier component = item.components.get(DataComponents.ITEM_MODEL);
			if (component != null) return component;
		}
		return item.id;
	}

	private static JsonNode inlineDefinition(NexoItem owner, Identifier requested) {
		if (owner.itemModel != null && !owner.itemModel.isNull()) {
			Identifier configured = configuredItemModel(owner);
			if (requested.equals(owner.id) || requested.equals(configured)) return owner.itemModel;
		}
		NexoItem linked = ContentRegistries.NEXO_ITEMS.getValue(requested);
		return linked != null && linked.itemModel != null && !linked.itemModel.isNull() ? linked.itemModel : null;
	}

	private static JsonNode readDefinition(RuntimeResourcePack resourcePack, Identifier id) throws Exception {
		Identifier path = Identifier.fromNamespaceAndPath(id.getNamespace(), "items/" + id.getPath() + ".json");
		var resource = resourcePack.getResource(PackType.CLIENT_RESOURCES, path);
		if (resource == null) return null;
		try (InputStream input = resource.get()) {
			return JSON.readTree(input);
		}
	}

	private static JsonNode normalize(JsonNode node, String defaultNamespace) {
		if (node == null || node.isNull() || node.isValueNode()) return node;
		if (node.isArray()) {
			ArrayNode result = JSON.createArrayNode();
			for (JsonNode child : node) result.add(normalize(child, defaultNamespace));
			return result;
		}

		ObjectNode result = JSON.createObjectNode();
		for (Map.Entry<String, JsonNode> field : node.properties()) {
			String key = field.getKey();
			JsonNode value = field.getValue();
			if (value != null && value.isTextual()) {
				String text = value.asText();
				if ("type".equals(key) || "property".equals(key) || "component".equals(key)) {
					result.put(key, qualify(text, "minecraft"));
					continue;
				}
				if ("kind".equals(key)) {
					result.put(key, qualify(text, "minecraft"));
					continue;
				}
				if ("base".equals(key) || "id".equals(key) && "reference".equals(localName(node.path("type").asText()))) {
					result.put(key, qualify(text, defaultNamespace));
					continue;
				}
				if ("model".equals(key) && isModelLeaf(node)) {
					result.put(key, qualify(text, defaultNamespace));
					continue;
				}
			}
			result.set(key, normalize(value, defaultNamespace));
		}
		return result;
	}

	private static boolean isModelLeaf(JsonNode node) {
		String type = localName(node.path("type").asText("model"));
		return "model".equals(type);
	}

	private static Identifier resolve(JsonNode node, Map<String, Object> properties, String defaultNamespace) {
		if (node == null || node.isNull()) return null;
		if (node.isTextual()) return parseIdentifier(node.asText(), defaultNamespace);
		if (!node.isObject()) return null;
		if (node.has("model") && !node.has("type")) return resolve(node.get("model"), properties, defaultNamespace);

		String type = localName(node.path("type").asText("model"));
		return switch (type) {
			case "model" -> parseIdentifier(node.path("model").asText(null), defaultNamespace);
			case "select" -> resolveSelect(node, properties, defaultNamespace);
			case "condition" -> resolveCondition(node, properties, defaultNamespace);
			case "range_dispatch" -> resolveRange(node, properties, defaultNamespace);
			case "composite" -> resolveComposite(node, properties, defaultNamespace);
			case "empty", "special" -> null;
			default -> resolve(node.get("fallback"), properties, defaultNamespace);
		};
	}

	private static Identifier resolveSelect(JsonNode node, Map<String, Object> properties, String defaultNamespace) {
		String property = localName(node.path("property").asText());
		String key = "block_state".equals(property)
				? node.path("block_state_property").asText() : property;
		Object actual = properties.get(key);
		if (actual != null) {
			for (JsonNode selectCase : node.path("cases")) {
				if (matches(selectCase.get("when"), actual)) {
					return resolve(selectCase.get("model"), properties, defaultNamespace);
				}
			}
		}
		return resolve(node.get("fallback"), properties, defaultNamespace);
	}

	private static Identifier resolveCondition(JsonNode node, Map<String, Object> properties, String defaultNamespace) {
		String property = localName(node.path("property").asText());
		Object actual = properties.get(property);
		boolean value = actual instanceof Boolean bool && bool;
		return resolve(node.get(value ? "on_true" : "on_false"), properties, defaultNamespace);
	}

	private static Identifier resolveRange(JsonNode node, Map<String, Object> properties, String defaultNamespace) {
		String property = localName(node.path("property").asText());
		Object raw = properties.get(property);
		if (!(raw instanceof Number number)) return resolve(node.get("fallback"), properties, defaultNamespace);
		double actual = number.doubleValue();
		JsonNode selected = null;
		double selectedThreshold = -Double.MAX_VALUE;
		for (JsonNode entry : node.path("entries")) {
			double threshold = entry.path("threshold").asDouble(Double.NaN);
			if (!Double.isNaN(threshold) && threshold <= actual && threshold >= selectedThreshold) {
				selectedThreshold = threshold;
				selected = entry.get("model");
			}
		}
		return selected != null ? resolve(selected, properties, defaultNamespace)
				: resolve(node.get("fallback"), properties, defaultNamespace);
	}

	private static Identifier resolveComposite(JsonNode node, Map<String, Object> properties, String defaultNamespace) {
		for (JsonNode part : node.path("models")) {
			Identifier resolved = resolve(part, properties, defaultNamespace);
			if (resolved != null) return resolved;
		}
		return resolve(node.get("fallback"), properties, defaultNamespace);
	}

	private static boolean matches(JsonNode expected, Object actual) {
		if (expected == null || expected.isNull()) return false;
		if (expected.isArray()) {
			for (JsonNode value : expected) if (matches(value, actual)) return true;
			return false;
		}
		return expected.asText().equalsIgnoreCase(String.valueOf(actual));
	}

	private static String qualify(String value, String defaultNamespace) {
		if (value == null || value.isBlank() || value.indexOf(':') >= 0) return value;
		return defaultNamespace + ":" + value;
	}

	private static Identifier parseIdentifier(String value, String defaultNamespace) {
		if (value == null || value.isBlank()) return null;
		return Identifier.tryParse(qualify(value, defaultNamespace));
	}

	private static String localName(String value) {
		if (value == null) return "";
		int separator = value.indexOf(':');
		return (separator >= 0 ? value.substring(separator + 1) : value).toLowerCase(Locale.ROOT);
	}
}
