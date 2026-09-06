package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.addon_modules.nexo.NexoItemModelBuilder;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.minecraft.oraxen.ConnectableFurnitureBlock;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.assets.blockstates.BlockState;
import net.vampirestudios.packwright.assets.blockstates.Variant;
import net.vampirestudios.packwright.assets.equipment.EquipmentModel;
import net.vampirestudios.packwright.assets.equipment.Layer;
import net.vampirestudios.packwright.assets.equipment.LayerType;
import net.vampirestudios.packwright.assets.item.ItemModel;
import net.vampirestudios.packwright.assets.item.ItemModelDefinition;
import net.vampirestudios.packwright.assets.item.RangeEntry;
import net.vampirestudios.packwright.assets.item.models.ModelCondition;
import net.vampirestudios.packwright.assets.item.models.ModelRangeDispatch;
import net.vampirestudios.packwright.assets.item.properties.PropertyDamage;
import net.vampirestudios.packwright.assets.item.properties.PropertyUsingItem;
import net.vampirestudios.packwright.assets.item.tints.TintDye;

import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.vampirestudios.packwright.assets.blockstates.BlockState.state;

public class OraxenItemInitThread implements Runnable {
	private static final Set<String> GENERATED = ConcurrentHashMap.newKeySet();

	/** What a flat, texture-only item model inherits from. */
	private static final Identifier DEFAULT_ITEM_PARENT = Identifier.withDefaultNamespace("item/generated");

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

		boolean hasGeneratedModel = resourcePack.getResource(PackType.CLIENT_RESOURCES,
				Identifier.fromNamespaceAndPath(id.getNamespace(), "models/item/" + id.getPath() + ".json")) != null;
		if (!hasGeneratedModel) {
			if (!item.pack.generate_model) {
				// Wrapping a model in a child of itself gives a model with no textures and a parent
				// loop, which is what happens when the pack ships models/item/<id>.json and names it.
				if (item.pack.model != null && !item.pack.model.equals(Utils.prependToPath(id, "item/"))) {
					ARRPGenerationHelper.generateSimpleItemModel(item, resourcePack, id, item.pack.model);
				}
			} else {
				// An item that only names a texture has no parent to inherit from, and used to fall out
				// of here generating nothing at all — which is most of them. A flat item model is the
				// right default, the same one vanilla item textures use.
				Identifier parent = item.pack.parent_model != null ? item.pack.parent_model : DEFAULT_ITEM_PARENT;
				Map<String, Identifier> textures = item.pack.getTextures();
				if (textures != null && !textures.isEmpty()) {
					ARRPGenerationHelper.generateItemModel(item, resourcePack, id, parent, textures);
				} else if (item.pack.parent_model != null) {
					ARRPGenerationHelper.generateItemModel(item, resourcePack, id, parent, Map.of());
				} else {
					System.err.println("[Obsidian] Nexo item " + id
							+ " asks for a generated model but names no texture; skipping its model.");
				}
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

		boolean hasItemModelComponent = item.components != null
				&& item.components.get(DataComponents.ITEM_MODEL) != null;

		boolean generatedBuilder = NexoItemModelBuilder.generate(resourcePack, item);
		if (!generatedBuilder && !hasItemModelComponent) {
			itemInfo.model(model);
			resourcePack.addItemModelInfo(itemInfo, id);
		}

		generateBlockState(id);
	}

	private void generateBlockState(Identifier id) {
		if (item.mechanics != null && item.mechanics.furniture != null) {
			Identifier furnitureModel = item.pack.model != null ? item.pack.model : id;
			Identifier lidId = toggledFurnitureModel(furnitureModel);
			Variant variant = furnitureVariants(furnitureModel, lidId);
			resourcePack.addBlockState(state(variant), id);
		} else if (item.mechanics != null && (item.mechanics.custom_block != null
				|| item.mechanics.noteblock != null || item.mechanics.stringblock != null)) {
			Identifier model = item.pack.model != null ? item.pack.model : customBlockModel(id);
			resourcePack.addBlockState(state(new Variant().put("", BlockState.model(model))), id);
		}
	}

	private Identifier toggledFurnitureModel(Identifier fallback) {
		NexoItem.Mechanics.Lights lights = item.mechanics.furniture.lights;
		if (lights == null) return fallback;
		if (lights.toggled_item_model != null) {
			Identifier resolved = NexoItemModelBuilder.resolveBlockModel(resourcePack, item,
					resolveItemModelId(lights.toggled_item_model), Map.of("lit", true));
			if (resolved != null) return resolved;
		}
		if (lights.toggled_model != null && !lights.toggled_model.isBlank()) {
			Identifier linkedId = lights.toggled_model.contains(":") ? Identifier.parse(lights.toggled_model)
					: Identifier.fromNamespaceAndPath(item.id.getNamespace(), lights.toggled_model);
			NexoItem linked = ContentRegistries.NEXO_ITEMS.getValue(linkedId);
			if (linked != null && linked.pack != null && linked.pack.model != null) return linked.pack.model;
		}
		return fallback;
	}

	private Identifier customBlockModel(Identifier fallback) {
		NexoItem.Mechanics.CustomBlock mechanic = item.mechanics.custom_block != null
				? item.mechanics.custom_block : item.mechanics.noteblock != null
				? item.mechanics.noteblock : item.mechanics.stringblock;
		if (mechanic == null || mechanic.model == null || mechanic.model.isBlank()) return fallback;
		return mechanic.model.contains(":") ? Identifier.parse(mechanic.model)
				: Identifier.fromNamespaceAndPath(item.id.getNamespace(), mechanic.model);
	}

	private Variant furnitureVariants(Identifier furnitureModel, Identifier litModel) {
		Variant variants = new Variant();
		NexoItem.Mechanics.Furniture furniture = item.mechanics.furniture;
		String[] facings = {"north", "east", "south", "west"};
		int[] rotations = {0, 90, 180, 270};
		boolean connectable = furniture.connectable != null;
		boolean stateful = furniture.states != null && !furniture.states.isEmpty();
		boolean visualLit = furniture.lights != null && (furniture.lights.toggled_item_model != null
				|| furniture.lights.toggled_model != null && !furniture.lights.toggled_model.isBlank());
		boolean visualOpen = furniture.door != null && !furniture.door.is_sliding;
		int[] stateValues = stateful ? allStateValues() : new int[]{0};

		for (int facingIndex = 0; facingIndex < facings.length; facingIndex++) {
			for (boolean lit : visualLit ? new boolean[]{false, true} : new boolean[]{false}) {
				for (boolean open : visualOpen ? new boolean[]{false, true} : new boolean[]{false}) {
					for (int stateValue : stateValues) {
						Identifier stateModel = furnitureStateModel(stateValue, furnitureModel);
						Identifier visibleModel = lit ? toggledFurnitureModel(stateModel) : stateModel;
						if (connectable) {
							for (ConnectableFurnitureBlock.ConnectType type : ConnectableFurnitureBlock.ConnectType.values()) {
								Identifier model = connectableModel(type, visibleModel, lit, stateValue);
								int rotation = modelRotation(rotations[facingIndex], type, open);
								variants.put(variantKey(facings[facingIndex], lit, open, stateValue,
										visualLit, visualOpen, stateful) + ",connectable=" + type.getSerializedName(),
										BlockState.model(model).y(rotation));
							}
						} else {
							int rotation = modelRotation(rotations[facingIndex], null, open);
							variants.put(variantKey(facings[facingIndex], lit, open, stateValue,
									visualLit, visualOpen, stateful), BlockState.model(visibleModel).y(rotation));
						}
					}
				}
			}
		}
		return variants;
	}

	private String variantKey(String facing, boolean lit, boolean open, int state, boolean visualLit,
	                          boolean visualOpen, boolean stateful) {
		StringBuilder key = new StringBuilder("facing=").append(facing);
		if (visualLit) key.append(",lit=").append(lit);
		if (visualOpen) key.append(",open=").append(open);
		if (stateful) key.append(",furniture_state=").append(state);
		return key.toString();
	}

	private int modelRotation(int base, ConnectableFurnitureBlock.ConnectType type, boolean open) {
		int rotation = base;
		if (open && item.mechanics.furniture.door != null && !item.mechanics.furniture.door.is_sliding) rotation += 90;
		if (type == ConnectableFurnitureBlock.ConnectType.INNER_RIGHT
				|| type == ConnectableFurnitureBlock.ConnectType.OUTER_RIGHT) rotation += 90;
		return rotation % 360;
	}

	private Identifier connectableModel(ConnectableFurnitureBlock.ConnectType type, Identifier fallback,
	                                    boolean lit, int furnitureState) {
		NexoItem.Mechanics.Furniture.Connectable connectable = item.mechanics.furniture.connectable;
		if (connectable.type == NexoItem.Mechanics.Furniture.Connectable.ConnectableItemType.ITEM_MODEL) {
			Identifier base = connectable.defaultModel != null ? resolveItemModelId(connectable.defaultModel)
					: NexoItemModelBuilder.configuredItemModel(item);
			Identifier configured = switch (type) {
				case STRAIGHT -> connectable.straight;
				case LEFT -> connectable.left;
				case RIGHT -> connectable.right;
				case INNER_LEFT, INNER_RIGHT -> connectable.inner;
				case OUTER_LEFT, OUTER_RIGHT -> connectable.outer;
				default -> connectable.defaultModel;
			};
			Identifier selected = configured != null ? resolveItemModelId(configured) : base;
			Map<String, Object> properties = new LinkedHashMap<>();
			properties.put("connectable", connectableValue(type));
			properties.put("lit", lit);
			properties.put("furniture_state", furnitureState);
			Identifier resolved = NexoItemModelBuilder.resolveBlockModel(resourcePack, item, selected, properties);
			if (resolved != null) return resolved;

			if (configured == null && type != ConnectableFurnitureBlock.ConnectType.SINGLE) {
				Identifier suffixed = base.withSuffix(switch (type) {
					case STRAIGHT -> "_straight";
					case LEFT -> "_left";
					case RIGHT -> "_right";
					case INNER_LEFT, INNER_RIGHT -> "_inner";
					case OUTER_LEFT, OUTER_RIGHT -> "_outer";
					default -> "";
				});
				resolved = NexoItemModelBuilder.resolveBlockModel(resourcePack, item, suffixed, properties);
				if (resolved != null) return resolved;
			}
			return fallback;
		}
		Identifier base = connectable.defaultModel != null ? resolveLinkedModel(connectable.defaultModel, fallback) : fallback;
		Identifier configured = switch (type) {
			case STRAIGHT -> connectable.straight;
			case LEFT -> connectable.left;
			case RIGHT -> connectable.right;
			case INNER_LEFT, INNER_RIGHT -> connectable.inner;
			case OUTER_LEFT, OUTER_RIGHT -> connectable.outer;
			default -> connectable.defaultModel;
		};
		if (configured != null) return resolveLinkedModel(configured, base);
		if (connectable.defaultModel == null || type == ConnectableFurnitureBlock.ConnectType.SINGLE) return base;
		return base.withSuffix(switch (type) {
			case STRAIGHT -> "_straight";
			case LEFT -> "_left";
			case RIGHT -> "_right";
			case INNER_LEFT, INNER_RIGHT -> "_inner";
			case OUTER_LEFT, OUTER_RIGHT -> "_outer";
			default -> "";
		});
	}

	private String connectableValue(ConnectableFurnitureBlock.ConnectType type) {
		return switch (type) {
			case STRAIGHT -> "straight";
			case LEFT -> "left";
			case RIGHT -> "right";
			case INNER_LEFT, INNER_RIGHT -> "inner";
			case OUTER_LEFT, OUTER_RIGHT -> "outer";
			default -> "default";
		};
	}

	private Identifier furnitureStateModel(int stateValue, Identifier fallback) {
		if (stateValue <= 0 || item.mechanics.furniture.states == null) return fallback;
		List<Map.Entry<String, NexoItem.Mechanics.Furniture.FurnitureState>> states =
				new ArrayList<>(item.mechanics.furniture.states.entrySet());
		if (stateValue > states.size() || stateValue > 15) return fallback;
		NexoItem.Mechanics.Furniture.FurnitureState state = states.get(stateValue - 1).getValue();
		if (state.model != null) return resolveItemModelId(state.model);

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("furniture_state", stateValue);
		if (state.value instanceof Number number) properties.put("custom_model_data", number);
		Identifier itemModel = state.item_model != null ? resolveItemModelId(state.item_model)
				: NexoItemModelBuilder.configuredItemModel(item);
		Identifier resolved = NexoItemModelBuilder.resolveBlockModel(resourcePack, item, itemModel, properties);
		return resolved != null ? resolved : fallback;
	}

	private int[] allStateValues() {
		int[] values = new int[16];
		for (int index = 0; index < values.length; index++) values[index] = index;
		return values;
	}

	private Identifier resolveItemModelId(Identifier configured) {
		return configured.getNamespace().equals("minecraft")
				? Identifier.fromNamespaceAndPath(item.id.getNamespace(), configured.getPath()) : configured;
	}

	private Identifier resolveLinkedModel(Identifier configured, Identifier fallback) {
		if (item.mechanics.furniture.connectable.type != NexoItem.Mechanics.Furniture.Connectable.ConnectableItemType.ITEM) {
			return configured;
		}
		Identifier linkedId = configured.getNamespace().equals("minecraft")
				? Identifier.fromNamespaceAndPath(item.id.getNamespace(), configured.getPath()) : configured;
		NexoItem linked = ContentRegistries.NEXO_ITEMS.getValue(linkedId);
		return linked != null && linked.pack != null && linked.pack.model != null ? linked.pack.model : fallback;
	}
}
