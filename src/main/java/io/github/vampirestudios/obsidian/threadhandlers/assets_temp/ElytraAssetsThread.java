package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import net.vampirestudios.packwright.api.RuntimeResourcePack;

import java.util.Map;

/**
 * An elytra needs two things drawn: the item in the inventory, which is an ordinary item model, and
 * the wings on the wearer's back, which come from the equipment asset the item's
 * {@code EQUIPPABLE} component points at.
 */
public class ElytraAssetsThread implements Runnable {

	private final Elytra elytra;
	private final RuntimeResourcePack resourcePack;

	public ElytraAssetsThread(RuntimeResourcePack resourcePack, Elytra elytra) {
		this.resourcePack = resourcePack;
		this.elytra = elytra;
	}

	@Override
	public void run() {
		new ItemInitThread(resourcePack, elytra).run();

		Map<String, ItemDisplayInformation.EquipmentLayer> layers = elytra.rendering != null
				? elytra.rendering.resolveEquipment()
				: Map.of();

		// a pack written before the texture moved under rendering.equipment declares it at the top
		// level and names no layer at all; it can only ever have meant the wings
		if (layers.isEmpty() && elytra.getTexture() != null) {
			layers = Map.of("wings", new ItemDisplayInformation.EquipmentLayer(elytra.getTexture(), false, null));
		}

		// the asset id is the item id — see the EQUIPPABLE component in the Elytras module
		ARRPGenerationHelper.generateEquipmentModel(resourcePack, elytra.information.id, layers);
	}
}
