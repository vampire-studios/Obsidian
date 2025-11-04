package io.github.vampirestudios.obsidian.scripting.std;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class ObsSuggest {
	private static final Map<String, SuggestionProvider<CommandSourceStack>> REG = new HashMap<>();
	private static boolean bootstrapped = false;
	private ObsSuggest() {
	}

	public static void ensureDefaults() {
		if (bootstrapped) return;
		bootstrapped = true;

		REG.computeIfAbsent("items",    k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.ITEM)));
		REG.computeIfAbsent("blocks",   k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.BLOCK)));
		REG.computeIfAbsent("entities", k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.ENTITY_TYPE)));
		REG.computeIfAbsent("biomes",   k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.BIOME)));
		REG.computeIfAbsent("sounds",   k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.SOUND_EVENT)));
		REG.computeIfAbsent("dimensions", k -> (ctx, b) -> suggestAllFiltered(b, registryIds(ctx, Registries.DIMENSION)));
		REG.computeIfAbsent("gamemodes", k -> (ctx, b) -> {
			suggestAllFiltered(b, List.of("survival","creative","adventure","spectator"));
			return b.buildFuture();
		});
	}

	/** Remove all providers. Useful if you want a clean slate on reload. */
	public static void clear() { REG.clear(); }

	public static void register(String id, SuggestionProvider<CommandSourceStack> provider) {
		REG.put(id, provider);
	}

	public static Optional<SuggestionProvider<CommandSourceStack>> get(String id) {
		return Optional.ofNullable(REG.get(id));
	}

	/* -------- helpers for provider authors -------- */

	/** Safely read a previously-parsed argument; returns "" if unavailable. */
	public static String arg(CommandContext<CommandSourceStack> ctx, String name) {
		try { return String.valueOf(ctx.getArgument(name, Object.class)); }
		catch (Exception ignored) { return ""; }
	}

	/** Lowercased partial token the player is completing right now. */
	public static String prefix(SuggestionsBuilder b) { return b.getRemainingLowerCase(); }

	/** Suggest only entries that contain the current prefix (case-insensitive). */
	public static CompletableFuture<Suggestions> suggestAllFiltered(
			SuggestionsBuilder b, Collection<String> all) {
		String pref = prefix(b);
		for (String s : all) if (s.toLowerCase(Locale.ROOT).contains(pref)) b.suggest(s);
		return b.buildFuture();
	}

	// Overload using registry key constants (less verbose)
	private static Collection<String> registryIds(CommandContext<CommandSourceStack> ctx, net.minecraft.resources.ResourceKey<? extends net.minecraft.core.Registry<?>> key) {
		return ctx.getSource().getServer().registryAccess()
				.lookupOrThrow(key).keySet().stream().map(ResourceLocation::toString).toList();
	}
}
