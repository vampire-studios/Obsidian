package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import net.minecraft.resources.Identifier;

public class Elytra extends Item {

	/**
	 * @deprecated the wing texture lives under {@code rendering.equipment.elytra} with the item's
	 * other rendering fields. Still read, so packs written before the move keep working.
	 */
	@Deprecated
	public Identifier texture;

	/**
	 * The wing texture drawn on the wearer's back, from {@code rendering.equipment.elytra} (or
	 * {@code .wings}), falling back to the legacy top-level {@code texture}.
	 *
	 * @return the texture as written, or null when the elytra declares none and should keep vanilla's
	 */
	public Identifier getTexture() {
		if (rendering != null) {
			ItemDisplayInformation.EquipmentLayer layer = rendering.equipmentLayer("elytra", "wings");
			if (layer != null) return layer.texture();
		}
		return texture;
	}

}
