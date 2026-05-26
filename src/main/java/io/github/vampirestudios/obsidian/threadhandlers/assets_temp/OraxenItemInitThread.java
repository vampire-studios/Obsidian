package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.assets.blockstates.BlockState;
import net.vampirestudios.arrp.assets.blockstates.Variant;
import net.vampirestudios.arrp.assets.equipment.EquipmentModel;
import net.vampirestudios.arrp.assets.equipment.Layer;
import net.vampirestudios.arrp.assets.equipment.LayerType;
import net.vampirestudios.arrp.assets.item.ItemModel;
import net.vampirestudios.arrp.assets.item.ItemModelDefinition;
import net.vampirestudios.arrp.assets.item.RangeEntry;
import net.vampirestudios.arrp.assets.item.models.ModelCondition;
import net.vampirestudios.arrp.assets.item.models.ModelRangeDispatch;
import net.vampirestudios.arrp.assets.item.properties.PropertyDamage;
import net.vampirestudios.arrp.assets.item.properties.PropertyUsingItem;
import net.vampirestudios.arrp.assets.item.tints.TintDye;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.vampirestudios.arrp.assets.blockstates.BlockState.state;

public class OraxenItemInitThread implements Runnable {
	private static final Set<String> GENERATED = ConcurrentHashMap.newKeySet();

	private final NexoItem item;
	private final RuntimeResourcePack resourcePack;

	public OraxenItemInitThread(RuntimeResourcePack resourcePack, NexoItem item) {
		this.item = item;
		this.resourcePack = resourcePack;
	}

	@Override
	public void run() {
		if (item.mechanics != null && item.mechanics.armor != null) {
			String ns = item.id.getNamespace();
			String prefix = item.id.getPath().replaceAll("(.+?)_.*", "$1");

			if (GENERATED.add(prefix)) {
				Identifier id = Identifier.fromNamespaceAndPath(ns, prefix);
				EquipmentModel body = EquipmentModel.model()
						.addLayer(LayerType.HUMANOID, Layer.layer().texture(id))
						.addLayer(LayerType.HUMANOID_LEGGINGS, Layer.layer().texture(id))
						.addLayer(LayerType.WOLF_BODY, Layer.layer().texture(id))
						.addLayer(LayerType.HORSE_BODY, Layer.layer().texture(id))
						.addLayer(LayerType.LLAMA_BODY, Layer.layer().texture(id));

				// writes to assets/<ns>/models/equipment/<prefix>.json
				resourcePack.addEquipmentModel(
						body,
						Identifier.fromNamespaceAndPath(ns, prefix)
				);

				// 2) elytra wings
				EquipmentModel wings = EquipmentModel.model()
						.addLayer("wings",
								Layer.layer().texture(id.withSuffix("_elytra"))
										.usePlayerTexture(true)
						);

				// writes to assets/<ns>/models/equipment/<prefix>_elytra.json
				resourcePack.addEquipmentModel(wings, id.withSuffix("_elytra"));
			}
		}

		Identifier id = item.id;
		if (item.pack == null) return;

		if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(id, "item/")) != null) return;

		if (!item.pack.generate_model) {
			if (item.pack.model != null) {
				ARRPGenerationHelper.generateSimpleItemModel(item, resourcePack, id, item.pack.model);
			}
		} else {
			if (item.pack.parent_model != null) {
				ARRPGenerationHelper.generateItemModel(item, resourcePack, id, item.pack.parent_model, item.pack.getTextures());
			}
		}

		ItemModelDefinition itemInfo = new ItemModelDefinition();
		var baseModelPath = Utils.prependToPath(id, "item/");
		ItemModel model = ItemModel.model(baseModelPath);
		if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
			if (item.pack.model != null && item.pack.blocking_model != null) {
				ModelCondition blockingSelect = new ModelCondition().property(new PropertyUsingItem());
				ItemModel onFalseModel = ItemModel.model(item.pack.model);

				ItemModel onTrueModel = ItemModel.model(item.pack.blocking_model);

				model = blockingSelect.onFalse(onFalseModel).onTrue(onTrueModel);
			}
		}/* else if (item.getItemType().equals(NexoItem.ItemType.BOW)) {
			if (item.pack.pulling_models != null && !item.pack.pulling_models.isEmpty()) {
				JModelRangeDispatch dispatch = JModelRangeDispatch.rangeDispatch()
						.property(JPropertyUseDuration.useDuration());
				int stages = item.pack.pulling_models.size();
				for (int i = 0; i < stages; i++) {
					float threshold = (i + 1) / (float) stages;
					dispatch.entry(JRangeEntry.of(threshold,
							JItemModel.model(item.pack.pulling_models.get(i))));
				}
				String unpulled = item.pack.model != null ? item.pack.model : baseModelPath;
				dispatch.fallback(JItemModel.model(unpulled));

				model = JModelCondition.condition()
						.property(new JPropertyUsingItem())
						.onFalse(JItemModel.model(unpulled))
						.onTrue(dispatch);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.CROSSBOW)) {
			if (item.pack.pulling_models != null && !item.pack.pulling_models.isEmpty()) {
				JModelRangeDispatch pullingModel = JModelRangeDispatch.rangeDispatch()
						.property(JPropertyCrossbowPull.crossbowPull());
				int stages = item.pack.pulling_models.size();
				for (int i = 0; i < stages; i++) {
					float threshold = (float) (i + 1) / stages;
					pullingModel.entry(JRangeEntry.of(threshold,
							JItemModel.model(item.pack.pulling_models.get(i))));
				}
				String baseModel = item.pack.model != null ? item.pack.model : baseModelPath;
				pullingModel.fallback(JItemModel.model(baseModel));

				JModelSelect chargedSelectModel = JModelSelect.select()
						.property(JPropertyChargeType.chargeType());
				if (item.pack.charged_model != null)
					chargedSelectModel.addCase(JSelectCase.of("arrow", JItemModel.model(item.pack.charged_model)));
				if (item.pack.firework_model != null)
					chargedSelectModel.addCase(JSelectCase.of("rocket", JItemModel.model(item.pack.firework_model)));
				chargedSelectModel.fallback(JItemModel.model(baseModel));

				model = JModelCondition.condition()
						.property(new JPropertyUsingItem())
						.onTrue(pullingModel)
						.onFalse(chargedSelectModel);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.FISHING_ROD)) {
			if (item.pack.cast_model != null) {
				String uncast = item.pack.model != null ? item.pack.model : baseModelPath;
				model = JModelCondition.condition()
						.property(JPropertyFishingRodCast.fishingRodCast())
						.onFalse(JItemModel.model(uncast))
						.onTrue(JItemModel.model(item.pack.cast_model));
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.TRIDENT)) {
			if (item.mechanics != null && item.mechanics.trident != null
					&& item.mechanics.trident.thrown_item_model != null && item.pack.model != null) {
				model = JModelCondition.condition().property(new JPropertyUsingItem())
						.onFalse(JModelTrident.trident().base(item.pack.model))
						.onTrue(JModelTrident.trident().base(item.mechanics.trident.thrown_item_model));
			}
		}*/

		// Damaged model stages (durability-based model swapping)
		if (item.pack.damaged_models != null && !item.pack.damaged_models.isEmpty()) {
			ModelRangeDispatch damageDispatch = (ModelRangeDispatch) ModelRangeDispatch.rangeDispatch()
					.property(PropertyDamage.of(false))
					.fallback(model);
			int stages = item.pack.damaged_models.size();
			for (int i = 0; i < stages; i++) {
				float threshold = (float) (i + 1) / stages;
				damageDispatch.entry(RangeEntry.of(threshold, ItemModel.model(item.pack.damaged_models.get(i))));
			}
			model = damageDispatch;
		}

		// Dyeable tint
		if (item.mechanics != null && item.mechanics.dyeable != null && item.mechanics.dyeable.enabled) {
			model.tint(new TintDye(item.mechanics.dyeable.getDefaultColor()));
		}

		boolean hasItemModelComponent = item.components != null && item.components.get(DataComponents.ITEM_MODEL) != null;

		if (!hasItemModelComponent) {
			itemInfo.model(model);
			resourcePack.addItemModelInfo(itemInfo, id);
		}

		if (item.mechanics != null && item.mechanics.furniture != null) {
			Identifier furnitureModel = item.pack.model != null ? item.pack.model : id;
			Identifier lidId = (
					item.mechanics.furniture.lights != null &&
							item.mechanics.furniture.lights.toggled_item_model != null
			) ? item.mechanics.furniture.lights.toggled_item_model : furnitureModel;
			Variant variant = new Variant()
					// north
					.put("facing=north,lit=false,occupied=false", BlockState.model(furnitureModel))
					.put("facing=north,lit=false,occupied=true",  BlockState.model(furnitureModel))
					.put("facing=north,lit=true,occupied=false",  BlockState.model(lidId))
					.put("facing=north,lit=true,occupied=true",   BlockState.model(lidId))
					// east
					.put("facing=east,lit=false,occupied=false",  BlockState.model(furnitureModel).y(90))
					.put("facing=east,lit=false,occupied=true",   BlockState.model(furnitureModel).y(90))
					.put("facing=east,lit=true,occupied=false",   BlockState.model(lidId).y(90))
					.put("facing=east,lit=true,occupied=true",    BlockState.model(lidId).y(90))
					// south
					.put("facing=south,lit=false,occupied=false", BlockState.model(furnitureModel).y(180))
					.put("facing=south,lit=false,occupied=true",  BlockState.model(furnitureModel).y(180))
					.put("facing=south,lit=true,occupied=false",  BlockState.model(lidId).y(180))
					.put("facing=south,lit=true,occupied=true",   BlockState.model(lidId).y(180))
					// west
					.put("facing=west,lit=false,occupied=false",  BlockState.model(furnitureModel).y(270))
					.put("facing=west,lit=false,occupied=true",   BlockState.model(furnitureModel).y(270))
					.put("facing=west,lit=true,occupied=false",   BlockState.model(lidId).y(270))
					.put("facing=west,lit=true,occupied=true",    BlockState.model(lidId).y(270));
			resourcePack.addBlockState(state(variant), id);
		}
	}
}
