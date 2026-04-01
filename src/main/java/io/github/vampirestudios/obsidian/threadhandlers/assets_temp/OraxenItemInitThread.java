package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.devtech.arrp.json.equipmentinfo.JEquipmentModel;
import net.devtech.arrp.json.equipmentinfo.JLayer;
import net.devtech.arrp.json.equipmentinfo.LayerType;
import net.devtech.arrp.json.iteminfo.JItemInfo;
import net.devtech.arrp.json.iteminfo.model.JItemModel;
import net.devtech.arrp.json.iteminfo.model.JModelCondition;
import net.devtech.arrp.json.iteminfo.model.JModelRangeDispatch;
import net.devtech.arrp.json.iteminfo.model.JRangeEntry;
import net.devtech.arrp.json.iteminfo.property.JPropertyDamage;
import net.devtech.arrp.json.iteminfo.property.JPropertyUsingItem;
import net.devtech.arrp.json.iteminfo.tint.JTintDye;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.devtech.arrp.json.blockstate.JState.state;

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
				JEquipmentModel body = JEquipmentModel.model()
						.addLayer(LayerType.HUMANOID, JLayer.layer().texture(id))
						.addLayer(LayerType.HUMANOID_LEGGINGS, JLayer.layer().texture(id))
						.addLayer(LayerType.WOLF_BODY, JLayer.layer().texture(id))
						.addLayer(LayerType.HORSE_BODY, JLayer.layer().texture(id))
						.addLayer(LayerType.LLAMA_BODY, JLayer.layer().texture(id));

				// writes to assets/<ns>/models/equipment/<prefix>.json
				resourcePack.addEquipmentModel(
						body,
						Identifier.fromNamespaceAndPath(ns, prefix)
				);

				// 2) elytra wings
				JEquipmentModel wings = JEquipmentModel.model()
						.addLayer("wings",
								JLayer.layer().texture(id.withSuffix("_elytra"))
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

		JItemInfo itemInfo = new JItemInfo();
		String baseModelPath = Utils.prependToPath(id, "item/").toString();
		JItemModel model = JItemModel.model(baseModelPath);
		if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
			if (item.pack.model != null && item.pack.blocking_model != null) {
				JModelCondition blockingSelect = new JModelCondition().property(new JPropertyUsingItem());
				JItemModel onFalseModel = JItemModel.model(item.pack.model.toString());

				JItemModel onTrueModel = JItemModel.model(item.pack.blocking_model.toString());

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
							JItemModel.model(item.pack.pulling_models.get(i).toString())));
				}
				String unpulled = item.pack.model != null ? item.pack.model.toString() : baseModelPath;
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
							JItemModel.model(item.pack.pulling_models.get(i).toString())));
				}
				String baseModel = item.pack.model != null ? item.pack.model.toString() : baseModelPath;
				pullingModel.fallback(JItemModel.model(baseModel));

				JModelSelect chargedSelectModel = JModelSelect.select()
						.property(JPropertyChargeType.chargeType());
				if (item.pack.charged_model != null)
					chargedSelectModel.addCase(JSelectCase.of("arrow", JItemModel.model(item.pack.charged_model.toString())));
				if (item.pack.firework_model != null)
					chargedSelectModel.addCase(JSelectCase.of("rocket", JItemModel.model(item.pack.firework_model.toString())));
				chargedSelectModel.fallback(JItemModel.model(baseModel));

				model = JModelCondition.condition()
						.property(new JPropertyUsingItem())
						.onTrue(pullingModel)
						.onFalse(chargedSelectModel);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.FISHING_ROD)) {
			if (item.pack.cast_model != null) {
				String uncast = item.pack.model != null ? item.pack.model.toString() : baseModelPath;
				model = JModelCondition.condition()
						.property(JPropertyFishingRodCast.fishingRodCast())
						.onFalse(JItemModel.model(uncast))
						.onTrue(JItemModel.model(item.pack.cast_model.toString()));
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.TRIDENT)) {
			if (item.mechanics != null && item.mechanics.trident != null
					&& item.mechanics.trident.thrown_item_model != null && item.pack.model != null) {
				model = JModelCondition.condition().property(new JPropertyUsingItem())
						.onFalse(JModelTrident.trident().base(item.pack.model.toString()))
						.onTrue(JModelTrident.trident().base(item.mechanics.trident.thrown_item_model.toString()));
			}
		}*/

		// Damaged model stages (durability-based model swapping)
		if (item.pack.damaged_models != null && !item.pack.damaged_models.isEmpty()) {
			JModelRangeDispatch damageDispatch = (JModelRangeDispatch) JModelRangeDispatch.rangeDispatch()
					.property(JPropertyDamage.of(false))
					.fallback(model);
			int stages = item.pack.damaged_models.size();
			for (int i = 0; i < stages; i++) {
				float threshold = (float) (i + 1) / stages;
				damageDispatch.entry(JRangeEntry.of(threshold, JItemModel.model(item.pack.damaged_models.get(i).toString())));
			}
			model = damageDispatch;
		}

		// Dyeable tint
		if (item.mechanics != null && item.mechanics.dyeable != null && item.mechanics.dyeable.enabled) {
			model.tint(new JTintDye(item.mechanics.dyeable.getDefaultColor()));
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
			JVariant variant = new JVariant()
					// north
					.put("facing=north,lit=false,occupied=false", JState.model(furnitureModel))
					.put("facing=north,lit=false,occupied=true",  JState.model(furnitureModel))
					.put("facing=north,lit=true,occupied=false",  JState.model(lidId))
					.put("facing=north,lit=true,occupied=true",   JState.model(lidId))
					// east
					.put("facing=east,lit=false,occupied=false",  JState.model(furnitureModel).y(90))
					.put("facing=east,lit=false,occupied=true",   JState.model(furnitureModel).y(90))
					.put("facing=east,lit=true,occupied=false",   JState.model(lidId).y(90))
					.put("facing=east,lit=true,occupied=true",    JState.model(lidId).y(90))
					// south
					.put("facing=south,lit=false,occupied=false", JState.model(furnitureModel).y(180))
					.put("facing=south,lit=false,occupied=true",  JState.model(furnitureModel).y(180))
					.put("facing=south,lit=true,occupied=false",  JState.model(lidId).y(180))
					.put("facing=south,lit=true,occupied=true",   JState.model(lidId).y(180))
					// west
					.put("facing=west,lit=false,occupied=false",  JState.model(furnitureModel).y(270))
					.put("facing=west,lit=false,occupied=true",   JState.model(furnitureModel).y(270))
					.put("facing=west,lit=true,occupied=false",   JState.model(lidId).y(270))
					.put("facing=west,lit=true,occupied=true",    JState.model(lidId).y(270));
			resourcePack.addBlockState(state(variant), id);
		}
	}
}
