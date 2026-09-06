package io.github.vampirestudios.obsidian.addonapi;

import io.github.vampirestudios.obsidian.addonapi.model.ConditionDefinition;

import java.util.List;

public final class MenuButton {
	public int slot;
	public IconDefinition icon;
	public List<ConditionDefinition> conditions = List.of();
	public ButtonActions actions;
}