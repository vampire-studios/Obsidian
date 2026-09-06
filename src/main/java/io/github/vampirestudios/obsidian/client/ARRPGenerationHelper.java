package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.MultiBlockVariants;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementVariants;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.assets.blockstates.BlockState;
import net.vampirestudios.packwright.assets.blockstates.SimpleModel;
import net.vampirestudios.packwright.assets.blockstates.Variant;
import net.vampirestudios.packwright.assets.equipment.EquipmentModel;
import net.vampirestudios.packwright.assets.equipment.Layer;
import net.vampirestudios.packwright.assets.item.ItemModel;
import net.vampirestudios.packwright.assets.item.ItemModelDefinition;
import net.vampirestudios.packwright.assets.item.RangeEntry;
import net.vampirestudios.packwright.assets.item.SelectCase;
import net.vampirestudios.packwright.assets.item.models.ModelBasic;
import net.vampirestudios.packwright.assets.item.models.ModelCondition;
import net.vampirestudios.packwright.assets.item.models.ModelRangeDispatch;
import net.vampirestudios.packwright.assets.item.models.ModelSelect;
import net.vampirestudios.packwright.assets.item.properties.PropertyChargeType;
import net.vampirestudios.packwright.assets.item.properties.PropertyCrossbowPull;
import net.vampirestudios.packwright.assets.item.properties.PropertyUseDuration;
import net.vampirestudios.packwright.assets.item.properties.PropertyUsingItem;
import net.vampirestudios.packwright.assets.item.tints.Tint;
import net.vampirestudios.packwright.assets.models.Model;
import net.vampirestudios.packwright.assets.models.Textures;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static net.vampirestudios.packwright.assets.blockstates.BlockState.variant;
import static net.vampirestudios.packwright.assets.models.Model.model;
import static net.vampirestudios.packwright.assets.models.Model.textures;

public class ARRPGenerationHelper {

	public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockState(BlockState.state(variant(BlockState.model(Utils.prependToPath(name, "block/")))), name);
	}

	public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
		clientResourcePackBuilder.addBlockState(BlockState.state(variant(BlockState.model(modelId))), name);
	}

	public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
		Identifier modelPath = Utils.prependToPath(name, "block/");
		// Both states belong to one variant object: two of them would be written as a JSON array, which is
		// not what a blockstate's "variants" is. The hanging model is "<name>_hanging", appended, not prepended.
		BlockState hangingModel = BlockState.state(variant()
				.put("hanging=false", BlockState.model(modelPath))
				.put("hanging=true", BlockState.model(Utils.appendToPath(modelPath, "_hanging")))
		);
		clientResourcePackBuilder.addBlockState(hangingModel, name);
	}

	public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name,
	                                             Identifier modelId, Identifier hangingModel) {
		BlockState model = BlockState.state(variant()
				.put("hanging=false", BlockState.model(modelId))
				.put("hanging=true", BlockState.model(hangingModel))
		);
		clientResourcePackBuilder.addBlockState(model, name);
	}

	public static void generateLanternBlockModels(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent,
	                                              Map<String, Identifier> textures, Identifier parentHanging,
	                                              Map<String, Identifier> texturesHanging) {
		Model model = model(parent);
		Textures textures1 = textures();
		if (textures != null)
			textures.forEach((s, location) -> textures1.var(s, location.toString()));
		clientResourcePackBuilder.addModel(model.textures(textures1), name);

		Model hangingModel = model(parentHanging);
		Textures textures2 = textures();
		if (texturesHanging != null)
			texturesHanging.forEach((s, location) -> textures2.var(s, location.toString()));
		clientResourcePackBuilder.addModel(hangingModel.textures(textures2), Utils.appendToPath(name, "_hanging"));
	}

	public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
		Variant variant = new Variant();
		addAxes(variant, modelId, "");
		clientResourcePackBuilder.addBlockState(BlockState.state(variant), name);
	}

	/** A pillar that also has a {@code powered} state, drawing a model of its own while powered. */
	public static void generatePillarBlockState(RuntimeResourcePack pack, Identifier name,
	                                            Identifier unpoweredModelId, Identifier poweredModelId) {
		Variant variant = new Variant();
		addAxes(variant, unpoweredModelId, ",powered=false");
		addAxes(variant, poweredModelId, ",powered=true");
		pack.addBlockState(BlockState.state(variant), name);
	}

	private static void addAxes(Variant variant, Identifier modelId, String suffix) {
		variant.put("axis=y" + suffix, BlockState.model(modelId));
		variant.put("axis=x" + suffix, BlockState.model(modelId).x(90).y(90));
		variant.put("axis=z" + suffix, BlockState.model(modelId).x(90));
	}

	public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
		generateHorizontalFacingBlockState(clientResourcePackBuilder, name, modelId, 0);
	}

	public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId, int yOffset) {
		Variant variant = new Variant();
		addHorizontalFacings(variant, modelId, yOffset, "");
		clientResourcePackBuilder.addBlockState(BlockState.state(variant), name);
	}

	/** The four horizontal facings, turned from a model authored facing north. */
	private static void addHorizontalFacings(Variant variant, Identifier modelId, int yOffset, String suffix) {
		variant.put("facing=north" + suffix, BlockState.model(modelId).y(Math.floorMod(yOffset, 360)));
		variant.put("facing=east" + suffix, BlockState.model(modelId).y(Math.floorMod(90 + yOffset, 360)));
		variant.put("facing=south" + suffix, BlockState.model(modelId).y(Math.floorMod(180 + yOffset, 360)));
		variant.put("facing=west" + suffix, BlockState.model(modelId).y(Math.floorMod(270 + yOffset, 360)));
	}

	/** Unturned: {@code RotationBlockRenderer} applies the angle, so a rotation here would be applied twice. */
	public static void generateRotationBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name,
	                                              Identifier modelId, int segments,
	                                              Map<Integer, Identifier> rotationModels) {
		Variant variant = new Variant();

		for (int rotation = 0; rotation < segments; rotation++) {
			Identifier model = rotationModels != null ? rotationModels.get(rotation) : null;
			variant.put("rotation=" + rotation, BlockState.model(model != null ? model : modelId));
		}

		clientResourcePackBuilder.addBlockState(BlockState.state(variant), name);
	}

	public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
		generateFacingBlockState(clientResourcePackBuilder, name, modelId, 0);
	}

	public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId, int yOffset) {
		Variant variant = new Variant();
		addFacings(variant, modelId, yOffset, "");
		clientResourcePackBuilder.addBlockState(BlockState.state(variant), name);
	}

	/** A six-way block that also has a {@code powered} state, drawing a model of its own while powered. */
	public static void generateFacingBlockState(RuntimeResourcePack pack, Identifier name,
	                                            Identifier unpoweredModelId, Identifier poweredModelId, int yOffset) {
		Variant variant = new Variant();
		addFacings(variant, unpoweredModelId, yOffset, ",powered=false");
		addFacings(variant, poweredModelId, yOffset, ",powered=true");
		pack.addBlockState(BlockState.state(variant), name);
	}

	private static void addFacings(Variant variant, Identifier modelId, int yOffset, String suffix) {
		addHorizontalFacings(variant, modelId, yOffset, suffix);
		variant.put("facing=up" + suffix, BlockState.model(modelId).y(Math.floorMod(yOffset, 360)));
		variant.put("facing=down" + suffix, BlockState.model(modelId).x(180).y(Math.floorMod(yOffset, 360)));
	}

	public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
				.var("all", Utils.prependToPath(name, "block/").toString())
		), name);
	}

	public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier texture) {
		clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
				.var("all", Utils.prependToPath(texture, "block/").toString())
		), name);
	}

	public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
				.var("cross", Utils.prependToPath(name, "block/").toString())
		), name);
	}

	public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier texture) {
		clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
				.var("cross", Utils.prependToPath(texture, "block/").toString())
		), name);
	}

	public static void generateColumnBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier endTexture, Identifier sideTexture) {
		clientResourcePackBuilder.addModel(model("block/cube_column").textures(textures()
				.var("end", Utils.prependToPath(endTexture, "block/").toString())
				.var("side", Utils.prependToPath(sideTexture, "block/").toString())
		), name);
	}

	public static void generateModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
		Model itemModel = model(parent);
		Textures tex = textures();
		if (textures != null) textures.forEach((k, v) -> tex.var(k, v.toString()));
		clientResourcePackBuilder.addModel(itemModel, Utils.prependToPath(name, "block/"));
	}

	public static void generateTopBottomBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
		clientResourcePackBuilder.addModel(model("block/cube_top_bottom").textures(textures()
				.var("top", Utils.prependToPath(topTexture, "block/").toString())
				.var("bottom", Utils.prependToPath(bottomTexture, "block/").toString())
				.var("side", Utils.prependToPath(sideTexture, "block/").toString())
		), name);
	}

	public static void generateLadderBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addModel(model("block/ladder").textures(textures()
				.var("texture", Utils.prependToPath(name, "block/").toString())
		), name);
	}

	public static void generateBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
		Model model = model(parent);
		Textures textures1 = textures();
		if (textures != null)
			textures.forEach((s, location) -> textures1.var(s, location.toString()));
		clientResourcePackBuilder.addModel(model.textures(textures1), Utils.prependToPath(name, "block/"));
	}

	public static void generateItemModel(RuntimeResourcePack pack, Identifier name, Identifier parent, Map<String, Identifier> textures) {
		if (name == null) return;
		generateItemModel1(pack, Utils.prependToPath(name, "item/"), parent, textures);
	}

	public static void generateItemModel1(RuntimeResourcePack pack, Identifier modelId, Identifier parent, Map<String, Identifier> textures) {
		if (modelId == null || parent == null) return;

		Model itemModel = model(parent);
		Textures tex = textures();
		if (textures != null) {
			textures.forEach((k, v) -> tex.var(k, v.toString()));
		}

		// modelId is a FULL model identifier like "<ns>:item/foo_blocking" OR "<ns>:item/foo"
		// pack.addModel expects the *model identifier* (no "item/" prefix added here)
		pack.addModel(itemModel.textures(tex), modelId);
	}

	public static void generateItemModel(NexoItem item, RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
		if (name == null || parent == null) return;

		var tex = textures();
		if (textures != null) textures.forEach((k, v) -> tex.var(k, v.toString()));
		Identifier modelId = Utils.prependToPath(name, "item/");
		clientResourcePackBuilder.addModel(model(parent).textures(tex), modelId);

		ItemModelDefinition itemInfo = new ItemModelDefinition();
		var fallbackModel = net.vampirestudios.packwright.assets.item.models.ModelBasic.model(modelId);
		ItemModel model = fallbackModel;

		if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
			if (item.pack.blocking_model != null) {
				model = new ModelCondition()
						.property(new PropertyUsingItem())
						.onTrue(ItemModel.model(item.pack.blocking_model))
						.onFalse(model);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.BOW)) {
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
				ModelRangeDispatch pullDispatch = (ModelRangeDispatch) new ModelRangeDispatch()
						.property(PropertyUseDuration.useDuration())
						.scale(0.05f)
						.fallback(ItemModel.model(item.pack.pulling_models.get(0)));
				pullDispatch.entry(RangeEntry.of(0.65f, ItemModel.model(item.pack.pulling_models.get(1))));
				pullDispatch.entry(RangeEntry.of(0.9f, ItemModel.model(item.pack.pulling_models.get(2))));
				model = new ModelCondition()
						.property(new PropertyUsingItem())
						.onTrue(pullDispatch)
						.onFalse(model);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.CROSSBOW)) {
			ModelRangeDispatch pullDispatch = (ModelRangeDispatch) new ModelRangeDispatch()
					.property(PropertyCrossbowPull.crossbowPull())
					.fallback(item.pack.pulling_models != null && !item.pack.pulling_models.isEmpty()
							? ItemModel.model(item.pack.pulling_models.get(0))
							: fallbackModel);
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 2) {
				pullDispatch.entry(RangeEntry.of(0.58f, ItemModel.model(item.pack.pulling_models.get(1))));
			}
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
				pullDispatch.entry(RangeEntry.of(1.0f, ItemModel.model(item.pack.pulling_models.get(2))));
			}

			ModelCondition using = new ModelCondition()
					.property(new PropertyUsingItem())
					.onTrue(pullDispatch)
					.onFalse(model);

			ModelSelect chargeType = new ModelSelect()
					.property(PropertyChargeType.chargeType());
			if (item.pack.charged_model != null) {
				chargeType.addCase(SelectCase.of("arrow", ItemModel.model(item.pack.charged_model)));
			}
			if (item.pack.firework_model != null) {
				chargeType.addCase(SelectCase.of("rocket", ItemModel.model(item.pack.firework_model)));
			}
			chargeType.fallback(using);
			model = chargeType;
		}

		itemInfo.model(model);
		clientResourcePackBuilder.addItemModelInfo(itemInfo, name);
	}

	public static void generateSimpleItemModel(NexoItem item, RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent) {
		Identifier modelId = Utils.prependToPath(name, "item/");
		clientResourcePackBuilder.addModel(model(parent), modelId);

		ItemModelDefinition itemInfo = new ItemModelDefinition();
		var fallbackModel = net.vampirestudios.packwright.assets.item.models.ModelBasic.model(modelId);
		ItemModel model = fallbackModel;

		if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
			if (item.pack.blocking_model != null) {
				model = new ModelCondition()
						.property(new PropertyUsingItem())
						.onTrue(ItemModel.model(item.pack.blocking_model))
						.onFalse(model);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.BOW)) {
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
				ModelRangeDispatch pullDispatch = (ModelRangeDispatch) new ModelRangeDispatch()
						.property(PropertyUseDuration.useDuration())
						.scale(0.05f)
						.fallback(ItemModel.model(item.pack.pulling_models.get(0)));
				pullDispatch.entry(RangeEntry.of(0.65f, ItemModel.model(item.pack.pulling_models.get(1))));
				pullDispatch.entry(RangeEntry.of(0.9f, ItemModel.model(item.pack.pulling_models.get(2))));
				model = new ModelCondition()
						.property(new PropertyUsingItem())
						.onTrue(pullDispatch)
						.onFalse(model);
			}
		} else if (item.getItemType().equals(NexoItem.ItemType.CROSSBOW)) {
			ModelRangeDispatch pullDispatch = (ModelRangeDispatch) new ModelRangeDispatch()
					.property(PropertyCrossbowPull.crossbowPull())
					.fallback(item.pack.pulling_models != null && !item.pack.pulling_models.isEmpty()
							? ItemModel.model(item.pack.pulling_models.get(0))
							: fallbackModel);
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 2) {
				pullDispatch.entry(RangeEntry.of(0.58f, ItemModel.model(item.pack.pulling_models.get(1))));
			}
			if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
				pullDispatch.entry(RangeEntry.of(1.0f, ItemModel.model(item.pack.pulling_models.get(2))));
			}

			ModelCondition using = new ModelCondition()
					.property(new PropertyUsingItem())
					.onTrue(pullDispatch)
					.onFalse(model);

			ModelSelect chargeType = new ModelSelect()
					.property(PropertyChargeType.chargeType());
			if (item.pack.charged_model != null) {
				chargeType.addCase(SelectCase.of("arrow", ItemModel.model(item.pack.charged_model)));
			}
			if (item.pack.firework_model != null) {
				chargeType.addCase(SelectCase.of("rocket", ItemModel.model(item.pack.firework_model)));
			}
			chargeType.fallback(using);
			model = chargeType;
		}

		itemInfo.model(model);
		clientResourcePackBuilder.addItemModelInfo(itemInfo, name);
	}

	public static void generatePoweredBlockState(RuntimeResourcePack pack, Identifier name,
	                                             Identifier unpoweredModelId, Identifier poweredModelId) {
		BlockState state = BlockState.state(new Variant()
				.put("powered=false", BlockState.model(unpoweredModelId))
				.put("powered=true", BlockState.model(poweredModelId))
		);
		pack.addBlockState(state, name);
	}

	public static void generatePoweredHorizontalFacingBlockState(RuntimeResourcePack pack, Identifier name,
	                                                             Identifier unpoweredModelId, Identifier poweredModelId) {
		generatePoweredHorizontalFacingBlockState(pack, name, unpoweredModelId, poweredModelId, 0);
	}

	/**
	 * The same four facings {@link #generateHorizontalFacingBlockState} writes, once unpowered and once
	 * powered. This used to turn its models from south rather than north, so a block rendered a half-turn
	 * around the moment it was made powerable; both now read the model as authored facing north.
	 */
	public static void generatePoweredHorizontalFacingBlockState(RuntimeResourcePack pack, Identifier name,
	                                                             Identifier unpoweredModelId, Identifier poweredModelId,
	                                                             int yOffset) {
		Variant variant = new Variant();
		addHorizontalFacings(variant, unpoweredModelId, yOffset, ",powered=false");
		addHorizontalFacings(variant, poweredModelId, yOffset, ",powered=true");
		pack.addBlockState(BlockState.state(variant), name);
	}

	/** Picks the model for one combination of the variant properties a block declares. */
	@FunctionalInterface
	public interface VariantModel {
		Identifier get(@Nullable String placement, @Nullable String part, boolean powered);
	}

	/**
	 * A blockstate keyed on the variant properties a block declares — {@code placement}, {@code part},
	 * {@code facing} and {@code powered} — as their full product, so every state of the block is covered by
	 * exactly one variant. Horizontal facings rotate the model the same way {@link #generateFacingBlockState}
	 * does; {@code down} flips it, since a model authored for {@code up} cannot be derived otherwise.
	 *
	 * <p>Pass an empty list for a property the block does not have; {@code powered} adds both values at once.
	 */
	public static void generateVariantBlockState(RuntimeResourcePack pack, Identifier name,
	                                             List<String> placements, List<String> parts,
	                                             List<Direction> facings, boolean powered,
	                                             VariantModel models, int yOffset) {
		Variant variant = new Variant();

		for (String placement : placements.isEmpty() ? singletonOfNull() : placements) {
			for (String part : parts.isEmpty() ? singletonOfNull() : parts) {
				for (Boolean isPowered : powered ? List.of(false, true) : Collections.<Boolean>singletonList(null)) {
					for (Direction facing : facings.isEmpty() ? Collections.<Direction>singletonList(null) : facings) {
						List<String> key = new ArrayList<>(4);
						if (facing != null) key.add("facing=" + facing.getSerializedName());
						// A single variant is not a state property on the block — it picks the model, but
						// there is nothing to key the blockstate on, and naming a property the block does
						// not have would leave every state without a model.
						if (part != null && MultiBlockVariants.needsProperty(parts)) key.add("part=" + part);
						if (placement != null && PlacementVariants.needsProperty(placements)) {
							key.add("placement=" + placement);
						}
						if (isPowered != null) key.add("powered=" + isPowered);

						Identifier modelId = models.get(placement, part, Boolean.TRUE.equals(isPowered));
						variant.put(String.join(",", key), orient(BlockState.model(modelId), facing, yOffset));
					}
				}
			}
		}

		pack.addBlockState(BlockState.state(variant), name);
	}

	private static List<String> singletonOfNull() {
		return Collections.singletonList(null);
	}

	private static SimpleModel orient(SimpleModel model, @Nullable Direction facing, int yOffset) {
		int y = Math.floorMod(switch (facing) {
			case SOUTH -> 180 + yOffset;
			case EAST -> 90 + yOffset;
			case WEST -> 270 + yOffset;
			case null, default -> yOffset;
		}, 360);

		if (facing == Direction.DOWN) model.x(180);
		return y != 0 ? model.y(y) : model;
	}

	public static void generateSlabBlockState(RuntimeResourcePack pack, Identifier name, Identifier doubleBlockName) {
		// One variant object holding all three states — a blockstate is a map of state to model, so writing
		// three separate variants would leave only the last one.
		Variant variant = new Variant();
		for (SlabType t : SlabType.values()) {
			SimpleModel var = switch (t) {
				case BOTTOM -> BlockState.model(Utils.prependToPath(name, "block/"));
				case TOP -> BlockState.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
				case DOUBLE -> BlockState.model(Utils.prependToPath(doubleBlockName, "block/"));
			};
			// getSerializedName(), not name(): the game only knows the lowercase spellings.
			variant.put("type=" + t.getSerializedName(), var);
		}
		pack.addBlockState(BlockState.state(variant), name);
	}

	/**
	 * Layer names vanilla's equipment model accepts, from {@code EquipmentClientInfo.LayerType}. A
	 * layer outside this set makes the whole file fail to parse, so unknown ones are dropped with a
	 * message instead of being written out.
	 */
	private static final Set<String> EQUIPMENT_LAYERS = Set.of(
			"humanoid", "humanoid_leggings", "humanoid_baby", "wings", "wolf_body", "horse_body",
			"llama_body", "pig_saddle", "strider_saddle", "camel_saddle", "camel_husk_saddle",
			"horse_saddle", "donkey_saddle", "mule_saddle", "zombie_horse_saddle",
			"skeleton_horse_saddle", "happy_ghast_body", "nautilus_saddle", "nautilus_body"
	);

	/** Friendlier spellings a pack may use for a layer. */
	private static final Map<String, String> EQUIPMENT_LAYER_ALIASES = Map.of("elytra", "wings");

	/**
	 * Writes {@code assets/<namespace>/equipment/<path>.json} for an item that is worn — the file the
	 * client reads to know what to draw on the wearer, which is separate from the item's own model.
	 *
	 * @param assetId the item's equipment asset id, which is the item id for everything Obsidian
	 *                registers
	 * @param layers  the declared layers, from {@link ItemDisplayInformation#resolveEquipment()}
	 */
	public static void generateEquipmentModel(RuntimeResourcePack pack, Identifier assetId,
											  Map<String, ItemDisplayInformation.EquipmentLayer> layers) {
		if (layers == null || layers.isEmpty()) return;

		EquipmentModel model = EquipmentModel.model();
		boolean any = false;
		for (Map.Entry<String, ItemDisplayInformation.EquipmentLayer> entry : layers.entrySet()) {
			String name = EQUIPMENT_LAYER_ALIASES.getOrDefault(entry.getKey(), entry.getKey());
			if (!EQUIPMENT_LAYERS.contains(name)) {
				Obsidian.LOGGER.warn("[Obsidian] {} declares equipment layer '{}', which is not one the game "
						+ "knows; skipping it", assetId, entry.getKey());
				continue;
			}

			ItemDisplayInformation.EquipmentLayer declared = entry.getValue();
			Layer layer = Layer.layer()
					.texture(equipmentTexture(assetId, declared.texture(), name))
					.usePlayerTexture(declared.usePlayerTexture());
			if (declared.dyeableColor() != null) {
				layer.dyeable(Optional.of(declared.dyeableColor()));
			}
			model.addLayer(name, layer);
			any = true;
		}

		if (any) pack.addEquipmentModel(model, assetId);
	}

	/**
	 * The texture id vanilla wants in an equipment layer: a bare name, which it resolves against
	 * {@code textures/entity/equipment/<layer>/}.
	 *
	 * <p>Packs reasonably write the whole texture path instead, so the known prefixes and the
	 * extension are trimmed back off. That fixes the id but cannot move the file, so anything written
	 * outside the layer's own directory is called out — the png has to live there for the game to find
	 * it.</p>
	 */
	private static Identifier equipmentTexture(Identifier assetId, Identifier texture, String layer) {
		String path = texture.getPath();
		if (path.endsWith(".png")) path = path.substring(0, path.length() - ".png".length());

		String directory = "textures/entity/equipment/" + layer + "/";
		if (path.startsWith(directory)) {
			path = path.substring(directory.length());
		} else if (path.startsWith("textures/") || path.startsWith("entity/")) {
			path = path.substring(path.lastIndexOf('/') + 1);
			Obsidian.LOGGER.warn("[Obsidian] {} points its '{}' layer at {}, but the game only looks for that "
					+ "texture in {}{}.png — move the file there", assetId, layer, texture, directory, path);
		}
		return Identifier.fromNamespaceAndPath(texture.getNamespace(), path);
	}

	public static void generateBasicItemDefinition(RuntimeResourcePack pack, Block block, Identifier name, Identifier directModelId) {
		ItemModelDefinition itemInfo = new ItemModelDefinition();
		var itemModel = ModelBasic.model(directModelId);

		if (block.additional_information != null && block.additional_information.dyable) {
			itemModel.tint(Tint.dye(block.additional_information.defaultColor));
		}

		itemInfo.model(itemModel);
		pack.addItemModelInfo(itemInfo, name);
	}

}
