package io.github.vampirestudios.obsidian.client;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.concurrent.CompletableFuture;

public class GuiSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
		for (GUI gui : ContentRegistries.GUIS) {
			builder.suggest(gui.id.toString());
		}
		return builder.buildFuture();
	}
}