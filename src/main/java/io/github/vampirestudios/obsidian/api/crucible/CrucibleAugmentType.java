package io.github.vampirestudios.obsidian.api.crucible;

public class CrucibleAugmentType {
	public boolean Enabled = true;
	public String Display = "";
	public Formatting Formatting = new Formatting();
	public Icons Icons = new Icons();

	public static class Formatting {
		public String Empty = "<augment.icon> Empty <augment.type> Slot";
		public String Filled = "<augment.icon> <augment.type>: <augment.tooltip>";
		public boolean ShowEmptySlot = true;
	}

	public static class Icons {
		public String Empty = "☆";
		public String Filled = "★";
		public String Invalid = "";
	}

	/** Resolves a lore line for an empty slot, or null if ShowEmptySlot is false. */
	public String resolveEmptyLine() {
		if (!Formatting.ShowEmptySlot) return null;
		return Formatting.Empty
				.replace("<augment.icon>", Icons.Empty)
				.replace("<augment.type>", Display)
				.replace("<augment.tooltip>", "");
	}

	/** Resolves a lore line for a filled slot. */
	public String resolveFilledLine(String tooltip, String customIcon) {
		String icon = (customIcon != null && !customIcon.isEmpty()) ? customIcon : Icons.Filled;
		return Formatting.Filled
				.replace("<augment.icon>", icon)
				.replace("<augment.type>", Display)
				.replace("<augment.tooltip>", tooltip != null ? tooltip : "");
	}
}
