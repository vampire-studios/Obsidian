package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Set;

/**
 * Gives the unqualified paths in a Nexo {@code Pack} block their namespace, before the tree is turned
 * into objects.
 *
 * <p>This has to run on the raw tree. {@code Identifier.tryParse} maps a bare {@code witch/broom} and
 * an explicit {@code minecraft:witch/broom} onto the same value, so once deserialization has happened
 * there is no way to tell which the author wrote — and therefore no way to move one without moving the
 * other. Here the difference is still a colon in a string.
 *
 * <p>Bare paths mean {@code minecraft}, which is how Nexo reads them and what a pack brought over from
 * it expects. A {@code namespace} key on the {@code Pack} block sends them somewhere else instead:
 *
 * <pre>
 * Pack:
 *   namespace: pack          # this pack's own namespace; or name one outright
 *   model: witch/broom       # -&gt; &lt;pack&gt;:witch/broom
 *   texture: minecraft:foo   # written out, so left alone
 * </pre>
 */
public final class NexoPackPaths {

	/** Model and texture keys whose value is a single path. */
	private static final Set<String> SINGLE = Set.of(
			"model", "blocking_model", "charged_model", "firework_model", "cast_model", "dyeable_model",
			"texture", "dyeable_texture", "blocking_texture", "charged_texture", "firework_texture",
			"cast_texture"
	);

	/** Keys whose value is a list of paths. */
	private static final Set<String> LISTS = Set.of("pulling_models", "damaged_models", "pulling_textures");

	/**
	 * {@code parent_model} is left out on purpose. A parent usually is a vanilla model —
	 * {@code item/generated} being the common one — so moving it would break the ordinary case to fix
	 * a rare one.
	 */
	private static final String NAMESPACE_KEY = "namespace";

	private NexoPackPaths() {
	}

	/** Qualifies the {@code Pack} block of one item declaration in place. */
	public static void qualify(JsonNode item, String packNamespace) {
		if (!(item instanceof ObjectNode)) return;
		if (!(item.get("Pack") instanceof ObjectNode pack)) return;

		String namespace = targetNamespace(pack, packNamespace);
		if (namespace == null) return;

		for (String key : SINGLE) qualifyValue(pack, key, namespace);
		for (String key : LISTS) qualifyList(pack.get(key), namespace);

		// `textures` is whichever of the three shapes the author felt like: one path, a list, or a map
		// of slot to path.
		qualifyTextures(pack, namespace);
	}

	/**
	 * The namespace bare paths should take, or null to leave them alone — which is the default, since
	 * leaving them alone is what makes them {@code minecraft}.
	 */
	private static String targetNamespace(ObjectNode pack, String packNamespace) {
		JsonNode declared = pack.get(NAMESPACE_KEY);
		if (declared == null || !declared.isTextual()) return null;

		String requested = declared.asText().trim();
		if (requested.isEmpty()) return null;

		return requested.equals("pack") || requested.equals("self") ? packNamespace : requested;
	}

	private static void qualifyValue(ObjectNode pack, String key, String namespace) {
		JsonNode value = pack.get(key);
		if (isBare(value)) pack.put(key, namespace + ":" + value.asText().trim());
	}

	private static void qualifyList(JsonNode node, String namespace) {
		if (!(node instanceof ArrayNode array)) return;

		for (int index = 0; index < array.size(); index++) {
			JsonNode entry = array.get(index);
			if (isBare(entry)) array.set(index, array.textNode(namespace + ":" + entry.asText().trim()));
		}
	}

	private static void qualifyTextures(ObjectNode pack, String namespace) {
		JsonNode textures = pack.get("textures");
		if (textures == null) return;

		if (isBare(textures)) {
			pack.put("textures", namespace + ":" + textures.asText().trim());
		} else if (textures instanceof ArrayNode) {
			qualifyList(textures, namespace);
		} else if (textures instanceof ObjectNode map) {
			// Replacing a value under an existing key does not disturb the iteration.
			for (Map.Entry<String, JsonNode> slot : map.properties()) {
				if (isBare(slot.getValue())) {
					map.put(slot.getKey(), namespace + ":" + slot.getValue().asText().trim());
				}
			}
		}
	}

	/** A path the author wrote without a namespace — the only kind that gets moved. */
	private static boolean isBare(JsonNode node) {
		if (node == null || !node.isTextual()) return false;

		String text = node.asText().trim();
		return !text.isEmpty() && !text.contains(":");
	}
}
