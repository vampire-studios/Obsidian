package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.util.UseAction;

public class UseActions {

	public String action;
	public String rightClickAction;
	public int guiSize;
	public String inventoryName;

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
