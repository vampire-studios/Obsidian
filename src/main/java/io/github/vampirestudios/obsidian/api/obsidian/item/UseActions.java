package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.world.item.UseAnim;

public class UseActions {

	public String action;
	public String right_click_actions;
	public int gui_size;
	public NameInformation gui_title;
	public String url;

	public UseAnim getAction() {
		return switch (action) {
			case "none" -> UseAnim.NONE;
			case "eat" -> UseAnim.EAT;
			case "drink" -> UseAnim.DRINK;
			case "block" -> UseAnim.BLOCK;
			case "bow" -> UseAnim.BOW;
			case "spear" -> UseAnim.SPEAR;
			case "crossbow" -> UseAnim.CROSSBOW;
			case "spyglass" -> UseAnim.SPYGLASS;
			default -> throw new IllegalStateException("Unexpected value: " + action);
		};
	}

}
