package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Turns a {@code block_properties} or {@code item_properties} declaration into settings.
 *
 * <p>A pack may write the settings out, or name an entry in {@code block/property} or
 * {@code item/property} to use instead, and either of those may name a {@code parent} to start from.
 * All of that lands here, so both kinds of settings resolve the same way and every reader gets one
 * finished object rather than something it has to walk itself.
 *
 * <p>Settings are merged as JSON before they are bound, one level at a time: the parent's fields
 * first, then the child's on top. That is what makes {@code parent} mean anything — a field the child
 * did not write keeps the parent's value, rather than the default the field happens to start at.
 */
public final class SettingsResolver {

	private SettingsResolver() {
	}

	/** How far a {@code parent} chain may go before we assume it loops back on itself. */
	private static final int MAX_DEPTH = 8;

	/** Problems already reported, so one bad field in a pack of thousands is said once. */
	private static final java.util.Set<String> REPORTED = java.util.concurrent.ConcurrentHashMap.newKeySet();

	/** The complaint worth showing: Gson wraps the real one. */
	private static Throwable rootCause(Throwable e) {
		Throwable cause = e;
		while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
		return cause;
	}

	/**
	 * @param declaration what the pack wrote: settings, the id of registered settings, or nothing
	 * @param registry    where a named entry is looked up
	 * @param type        the settings class to bind
	 * @return the resolved settings, never null — a declaration that resolves to nothing gets defaults
	 */
	public static <T> T resolve(Object declaration, Registry<T> registry, Class<T> type, T fallback) {
		return resolve(declaration, registry, type, fallback, settings -> {
		});
	}

	/**
	 * @param legacy reads the spellings older packs used into the current ones. It runs on each set of
	 *               settings in the chain, before they are merged, so an inherited entry written years
	 *               ago is understood the same way as the file in front of you.
	 */
	public static <T> T resolve(Object declaration, Registry<T> registry, Class<T> type, T fallback,
	                            Consumer<JsonObject> legacy) {
		JsonElement tree = tree(declaration, registry, type, legacy);
		if (tree == null || !tree.isJsonObject()) return fallback;

		try {
			T resolved = BaseGson.GSON.fromJson(merged(tree.getAsJsonObject(), registry, type, legacy, 0), type);
			return resolved == null ? fallback : resolved;
		} catch (Exception e) {
			// The same malformed field usually appears in every file of a pack, and settings are read more
			// than once per thing, so each distinct complaint is only worth saying once.
			String problem = type.getSimpleName() + ": " + rootCause(e).getMessage();
			if (REPORTED.add(problem)) {
				Obsidian.LOGGER.error("[Obsidian] Could not read {}", problem, e);
			}
			return fallback;
		}
	}

	/** The settings a {@code parent} names, without the child merged in. */
	public static <T> T parent(Object declaration, Registry<T> registry, Class<T> type) {
		JsonElement tree = tree(declaration, registry, type, settings -> {
		});
		if (tree == null || !tree.isJsonObject()) return null;

		JsonElement parent = tree.getAsJsonObject().get("parent");
		if (parent == null || parent.isJsonNull()) return null;
		return resolve(parent, registry, type, null);
	}

	/** Everything a declaration can be, as the JSON object the settings class binds from. */
	private static <T> JsonElement tree(Object declaration, Registry<T> registry, Class<T> type,
	                                    Consumer<JsonObject> legacy) {
		JsonElement tree = switch (declaration) {
			case null -> null;
			case JsonElement element -> element.isJsonNull() ? null : element;
			case Identifier id -> registered(id, registry);
			case String s -> registered(Identifier.tryParse(s), registry);
			case Map<?, ?> map -> BaseGson.GSON.toJsonTree(map);
			default -> type.isInstance(declaration) ? BaseGson.GSON.toJsonTree(declaration) : null;
		};

		if (tree != null && tree.isJsonObject()) {
			// On a copy: the declaration may be a tree the pack's own file still holds, and reading it
			// should not rewrite what is sitting in the pack.
			JsonObject copy = tree.getAsJsonObject().deepCopy();
			legacy.accept(copy);
			return copy;
		}
		return tree;
	}

	private static <T> JsonElement registered(Identifier id, Registry<T> registry) {
		if (id == null) return null;

		T settings = registry.getValue(id);
		if (settings == null) {
			Obsidian.LOGGER.warn("[Obsidian] No settings registered as {}; using the defaults instead", id);
			return null;
		}
		return BaseGson.GSON.toJsonTree(settings);
	}

	/** The declaration with its parent chain folded in, nearest parent last so the child still wins. */
	private static <T> JsonObject merged(JsonObject declaration, Registry<T> registry, Class<T> type,
	                                     Consumer<JsonObject> legacy, int depth) {
		JsonElement parent = declaration.get("parent");
		if (parent == null || parent.isJsonNull()) return declaration;

		if (depth >= MAX_DEPTH) {
			Obsidian.LOGGER.warn("[Obsidian] Settings inherit more than {} levels deep, or inherit in a "
					+ "circle; ignoring the rest of the chain", MAX_DEPTH);
			return declaration;
		}

		JsonElement parentTree = tree(parent, registry, type, legacy);
		if (parentTree == null || !parentTree.isJsonObject()) return declaration;

		JsonObject merged = merged(parentTree.getAsJsonObject(), registry, type, legacy, depth + 1).deepCopy();
		for (Map.Entry<String, JsonElement> field : declaration.entrySet()) {
			if (field.getKey().equals("parent")) continue;
			merged.add(field.getKey(), field.getValue());
		}
		merged.remove("parent");
		return merged;
	}

}
