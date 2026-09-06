package io.github.vampirestudios.obsidian.addonapi;

import io.github.vampirestudios.obsidian.addonapi.model.ActionDefinition;

import java.util.List;

public class UiElementDef {
	public String type;           // "button", "label", "image"
	public String id;

	public AnchorPosDef x;
	public AnchorPosDef y;

	// shared optional stuff:
	public int width = 200;
	public int height = 20;

	// per type:
	public TextComponent label;   // for button
	public TextComponent text;    // for label
	public String texture;        // for image
	public int u = 0, v = 0;
	public int tex_width = 256, tex_height = 256;

	public List<ActionDefinition> actions = List.of(); // for button / clickable image
}