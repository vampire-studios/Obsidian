package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.util.UseAction;

public class UseActions {

	public String action;
	public String right_click_actions;
	public int gui_size;
	public NameInformation gui_title;

	public UseAction getAction() {
		return switch (action) {
			case "none" -> UseAction.NONE;
			case "eat" -> UseAction.EAT;
			case "drink" -> UseAction.DRINK;
			case "block" -> UseAction.BLOCK;
			case "bow" -> UseAction.BOW;
			case "spear" -> UseAction.SPEAR;
			case "crossbow" -> UseAction.CROSSBOW;
			case "spyglass" -> UseAction.SPYGLASS;
			default -> throw new IllegalStateException("Unexpected value: " + action);
		};
	}

}
