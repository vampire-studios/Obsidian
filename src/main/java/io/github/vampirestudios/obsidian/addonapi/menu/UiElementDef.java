package io.github.vampirestudios.obsidian.addonapi.menu;

import io.github.vampirestudios.obsidian.addonapi.model.ActionDefinition;
import io.github.vampirestudios.obsidian.addonapi.model.TextComponentDef;

import java.util.List;

public class UiElementDef {
	public String type;           // "button", "label", "image"
	public String id;

	public AnchorPosDef x;
	public AnchorPosDef y;

	public int width = 200;
	public int height = 20;

	// label / button
	public TextComponentDef label;
	public TextComponentDef text;

	// image
	public String texture;
	public int u = 0;
	public int v = 0;
	public int tex_width = 256;
	public int tex_height = 256;

	public List<ActionDefinition> actions;
}
