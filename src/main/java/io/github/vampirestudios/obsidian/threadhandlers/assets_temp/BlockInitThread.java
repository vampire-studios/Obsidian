package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import com.google.gson.JsonElement;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.CompanionBlocks;
import io.github.vampirestudios.obsidian.api.obsidian.block.MultiBlockVariants;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementVariants;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.DerivedBlockAssets;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableBlockEntity;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vampirestudios.packwright.api.RuntimeResourcePack;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlockInitThread implements Runnable {

	/** What a variant names to draw nothing: vanilla's own empty model, which has no elements. */
	private static final Identifier EMPTY_MODEL = Identifier.fromNamespaceAndPath("minecraft", "block/air");
	private static final String NO_MODEL = "none";

	private final Block block;
	private final RuntimeResourcePack resourcePack;
	private final boolean registerBlockColors;

	public BlockInitThread(RuntimeResourcePack resourcePack, Block blockIn) {
		this(resourcePack, blockIn, true);
	}

	public BlockInitThread(RuntimeResourcePack resourcePack, Block blockIn, boolean registerBlockColors) {
		block = blockIn;
		this.resourcePack = resourcePack;
		this.registerBlockColors = registerBlockColors;
	}

	public static int getBlockEntityColor(Block block, BlockGetter view, BlockPos pos) {
		BlockEntity be = view.getBlockEntity(pos);
		if (be instanceof DyeableBlockEntity dye) return dye.getDyeColor();
		return block.additional_information.defaultColor;
	}

	/**
	 * A companion block's name: the one it was given, or the base block's name in each language with the
	 * variant's own word after it — "Tutorial Bricks" becoming "Tutorial Bricks Slab".
	 *
	 * @param translated the base block's names, which may be absent — a variant that names itself does
	 *                   not need them
	 */
	private static void variantTranslation(Map<String, String> translated, CompanionBlocks.Declared variant) {
		Identifier id = variant.id();
		String key = "block." + id.getNamespace() + "." + id.getPath();

		NameInformation name = variant.options().name;
		if (name != null && name.translations != null && !name.translations.isEmpty()) {
			name.translations.forEach((languageId, text) ->
					ClientInit.addTranslation(id.getNamespace(), languageId, key, text));
			return;
		}

		if (translated == null) return;
		translated.forEach((languageId, text) ->
				ClientInit.addTranslation(id.getNamespace(), languageId, key, text + variant.type().englishSuffix));
	}

	public static void translation(Map<String, String> translated, Identifier blockId, String unTranslatedType, String translatedType) {
		translated.forEach((languageId, name) -> ClientInit.addTranslation(
				blockId.getNamespace(), languageId,
				"block." + blockId.getNamespace() + "." + blockId.getPath() + unTranslatedType, name + translatedType
		));
	}

	@Override
	public void run() {
		try {
			NameInformation nameInformation = block.information.name;
			Identifier blockId = block.information.id;
			Map<String, String> translated = nameInformation.translations;
			if (translated != null) {
				translated.forEach((languageId, name) -> ClientInit.addTranslation(
						blockId.getNamespace(), languageId, nameInformation.text, name
				));
				translation(translated, blockId, "", "");
			}
			if (block.lore != null) {
				for (SpecialText lore : block.lore) {
					if (lore.textType != null && lore.textType.equals("translatable")) {
						lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
								blockId.getNamespace(), languageId, lore.text, name
						));
					}
				}
			}
			if (block.rendering != null) {
				// A block that is itself a stairs, slab, wall, fence, fence gate, door, trapdoor, button or
				// pressure plate needs that shape's whole set of models and its blockstate, not the single
				// model the generic path below writes — which would leave it a plain cube in every state.
				boolean shaped = DerivedBlockAssets.generateForType(resourcePack, block, blockId);
				if (block.getBlockType() != null && !shaped) {
					switch (block.getBlockType()) {
						case HORIZONTAL_DIRECTIONAL -> generateHorizontalFacingBlockState(block, resourcePack, blockId);
						case DIRECTIONAL -> generateFacingBlockState(block, resourcePack, blockId);
						case EIGHT_DIRECTIONAL_BLOCK -> generateRotationBlockState(block, resourcePack, blockId, 8);
						case SIXTEEN_DIRECTIONAL_BLOCK -> generateRotationBlockState(block, resourcePack, blockId, 16);
						case LOG, ROTATED_PILLAR, STEM -> generatePillarBlockState(block, resourcePack, blockId);
//                        case FURNACE, BLAST_FURNACE, SMOKER ->
//                                ARRPGenerationHelper.generateOnOffHorizontalFacingBlockState(resourcePack, blockId);
//                        case PISTON -> {
//                            ARRPGenerationHelper.generatePistonBlockState(resourcePack, blockId,
//                                    block.rendering.blockState.model, block.rendering.blockState.stickyModel);
//                            ARRPGenerationHelper.generatePistonModels(resourcePack, blockId,
//                                    block.rendering.blockModel.parent, block.rendering.blockModel.textures,
//                                    block.rendering.stickyPiston.parent, block.rendering.stickyPiston.textures);
//                        }
						case OXIDIZING_BLOCK -> {
							for (Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
								for (Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
									Identifier outModelId = Utils.prependToPath(variantBlock.id, "block/");

									// if block_model was given as a string, use it directly
									if (variantBlock.display.blockModel != null
											&& variantBlock.display.blockModel.isJsonPrimitive()
											&& variantBlock.display.blockModel.getAsJsonPrimitive().isString()) {
										outModelId = Identifier.parse(variantBlock.display.blockModel.getAsString());
									}

									ARRPGenerationHelper.generateBasicBlockState(resourcePack, variantBlock.id, outModelId);
								}
							}
						}
						case LANTERN -> {
							if (block.rendering.blockState.model != null) {
								ARRPGenerationHelper.generateLanternBlockState(resourcePack, blockId,
										block.rendering.blockState.model, block.rendering.blockState.hangingModel);
							} else {
								ARRPGenerationHelper.generateLanternBlockState(resourcePack, blockId);
							}
						}
						default -> generateBlockState(block, resourcePack, blockId);
					}
				}
				generateVariantBlockModels(block, resourcePack, blockId);
				if (block.rendering.getBlockModel() != null && !shaped) {
					TextureAndModelInformation textureAndModelInformation = block.rendering.getBlockModel();
					if (block.getBlockType() != null) {
						switch (block.getBlockType()) {
							case OXIDIZING_BLOCK:
								for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
									for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
										textureAndModelInformation = variantBlock.display.getBlockModel();
										ARRPGenerationHelper.generateBlockModel(resourcePack, variantBlock.id,
												textureAndModelInformation.parent, textureAndModelInformation.textures);
									}
								}
								break;
							case LANTERN:
								TextureAndModelInformation hangingModelInformation = block.rendering.hangingModel;
								ARRPGenerationHelper.generateLanternBlockModels(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures, hangingModelInformation.parent, hangingModelInformation.textures);
								break;
							default:
								if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json")) != null) {
									break;
								}
								ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
								break;
						}
					} else {
						ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
					}
				}
				if (block.rendering.getModel() != null && !shaped) {
					TextureAndModelInformation textureAndModelInformation = block.rendering.getModel();

					// The variant blockstate written above already covers every state of the block; the plain one
					// for its type would replace it and lose the per-variant models. The same goes for a
					// powered block, whose blockstate carries a model for each of its two states.
					boolean hasVariants = !PlacementVariants.declared(block).isEmpty()
							|| !MultiBlockVariants.declared(block).isEmpty()
							|| hasPoweredState(block);

					if (block.getBlockType() != null) {
						switch (block.getBlockType()) {
							case OXIDIZING_BLOCK:
								for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
									for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
										textureAndModelInformation = variantBlock.display.getModel();
										ARRPGenerationHelper.generateBasicBlockState(resourcePack, Utils.prependToPath(variantBlock.id, "block/"));
										ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
									}
								}
								break;
							case LANTERN:
								TextureAndModelInformation textureAndModelInformation2 = block.rendering.hangingModel;
								ARRPGenerationHelper.generateLanternBlockModels(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), textureAndModelInformation.textures, textureAndModelInformation2.parent, textureAndModelInformation2.textures);
								break;
							case DIRECTIONAL:
								if (!hasVariants) {
									ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), block.rendering.model_rotation_offset);
								}
								if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json")) != null) {
									break;
								}
								ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
								break;
							case HORIZONTAL_DIRECTIONAL:
								if (!hasVariants) {
									ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), block.rendering.model_rotation_offset);
								}
								if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json")) != null) {
									break;
								}
								ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
								break;
							case ROTATED_PILLAR, LOG:
								if (!hasVariants) {
									ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
								}
								if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json")) != null) {
									break;
								}
								ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
								break;
							default:
								if (!hasVariants) {
									ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
								}
								if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json")) != null) {
									break;
								}
								ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
								break;
						}
					}
//                    ARRPGenerationHelper.generateBasicItemDefinition(resourcePack, block, blockId, Utils.prependToPath(blockId, "block/"));
				}

				Identifier directModelId = block.rendering.resolveItemDefinitionModelId(blockId);
				if (block.rendering.itemModel != null && block.rendering.itemModel.isJsonObject()) {
					TextureAndModelInformation itemInfo = block.rendering.getItemModel();
					ARRPGenerationHelper.generateItemModel(resourcePack, blockId, itemInfo.parent, itemInfo.textures);
				}
				// Generate the powered model if powered_model was given as an object
				if ((block.information.powerable || block.information.toggleable) && block.rendering.hasPoweredModelObject()) {
					TextureAndModelInformation poweredInfo = block.rendering.getPoweredModel();
					ARRPGenerationHelper.generateBlockModel(resourcePack, Utils.appendToPath(blockId, "_powered"),
							poweredInfo.parent, poweredInfo.textures);
				}

				// A shaped block already got the item definition its shape needs — a wall's and a fence's point
				// at their inventory model, a door's at its sprite — so leave that one alone.
				if (!shaped) {
					ARRPGenerationHelper.generateBasicItemDefinition(resourcePack, block, blockId, directModelId);
				}
			} else {
				// No rendering config at all (pack provides blockstate/model via file-based resources).
				// In 1.21.4+ every block item still needs an items/<id>.json, so emit one that
				// points to the canonical block/<id> model which the file-based pack must provide.
				ARRPGenerationHelper.generateBasicItemDefinition(resourcePack, block, blockId,
						Utils.prependToPath(blockId, "block/"));
			}
			DerivedBlockAssets.generate(resourcePack, block, blockId);
			for (CompanionBlocks.Declared variant : CompanionBlocks.declared(block)) {
				variantTranslation(translated, variant);
			}

			boolean dyable = block.additional_information != null && block.additional_information.dyable;
			dyable |= block.getBlockType() == Block.BlockType.DYEABLE;
			if (registerBlockColors && dyable) {
				net.minecraft.world.level.block.Block registeredBlock = BuiltInRegistries.BLOCK.getValue(block.information.id);
				BlockColorRegistry.register(List.of(new BlockTintSource() {
					@Override
					public int color(BlockState state) {
						return block.additional_information.defaultColor;
					}

					@Override
					public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
						if (level == null || pos == null) return block.additional_information.defaultColor;
						return getBlockEntityColor(block, level, pos);
					}
				}), registeredBlock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
		Identifier unpoweredModelId = Utils.prependToPath(blockId, "block/");

		// if block_model was given as a string, use it directly
		if (block.rendering.blockModel != null
				&& block.rendering.blockModel.isJsonPrimitive()
				&& block.rendering.blockModel.getAsJsonPrimitive().isString()) {
			unpoweredModelId = Identifier.parse(block.rendering.blockModel.getAsString());
		}

		if (generateVariantBlockState(block, resourcePack, blockId, unpoweredModelId, variantFacings(block))) return;

		if (hasPoweredState(block)) {
			Identifier poweredModelId = resolvePoweredModelId(block, blockId, unpoweredModelId);
			ARRPGenerationHelper.generatePoweredBlockState(resourcePack, blockId, unpoweredModelId, poweredModelId);
		} else {
			ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, unpoweredModelId);
		}
	}

	private void generateRotationBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId, int segments) {
		Identifier modelId = Utils.prependToPath(blockId, "block/");

		// if block_model was given as a string, use it directly
		if (block.rendering.blockModel != null
				&& block.rendering.blockModel.isJsonPrimitive()
				&& block.rendering.blockModel.getAsJsonPrimitive().isString()) {
			modelId = Identifier.parse(block.rendering.blockModel.getAsString());
		}

		ARRPGenerationHelper.generateRotationBlockState(resourcePack, blockId, modelId, segments,
				block.rendering.getRotationModels());
	}

	private void generatePillarBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
		Identifier outModelId = Utils.prependToPath(blockId, "block/");

		// if block_model was given as a string, use it directly
		if (block.rendering.blockModel != null
				&& block.rendering.blockModel.isJsonPrimitive()
				&& block.rendering.blockModel.getAsJsonPrimitive().isString()) {
			outModelId = Identifier.parse(block.rendering.blockModel.getAsString());
		}

		if (hasPoweredState(block)) {
			ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, outModelId,
					resolvePoweredModelId(block, blockId, outModelId));
		} else {
			ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, outModelId);
		}
	}

	private void generateHorizontalFacingBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
		Identifier unpoweredModelId = Utils.prependToPath(blockId, "block/");

		// if block_model was given as a string, use it directly
		if (block.rendering.blockModel != null
				&& block.rendering.blockModel.isJsonPrimitive()
				&& block.rendering.blockModel.getAsJsonPrimitive().isString()) {
			unpoweredModelId = Identifier.parse(block.rendering.blockModel.getAsString());
		}

		if (generateVariantBlockState(block, resourcePack, blockId, unpoweredModelId,
				List.copyOf(Direction.Plane.HORIZONTAL.stream().toList()))) return;

		int yOffset = block.rendering.model_rotation_offset;
		if (hasPoweredState(block)) {
			Identifier poweredModelId = resolvePoweredModelId(block, blockId, unpoweredModelId);
			ARRPGenerationHelper.generatePoweredHorizontalFacingBlockState(resourcePack, blockId, unpoweredModelId,
					poweredModelId, yOffset);
		} else {
			ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, unpoweredModelId, yOffset);
		}
	}

	private void generateFacingBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
		Identifier outModelId = Utils.prependToPath(blockId, "block/");

		// if block_model was given as a string, use it directly
		if (block.rendering.blockModel != null
				&& block.rendering.blockModel.isJsonPrimitive()
				&& block.rendering.blockModel.getAsJsonPrimitive().isString()) {
			outModelId = Identifier.parse(block.rendering.blockModel.getAsString());
		}

		if (generateVariantBlockState(block, resourcePack, blockId, outModelId, List.of(Direction.values()))) return;

		int yOffset = block.rendering.model_rotation_offset;
		if (hasPoweredState(block)) {
			ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, outModelId,
					resolvePoweredModelId(block, blockId, outModelId), yOffset);
		} else {
			ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, outModelId, yOffset);
		}
	}

	/**
	 * Writes the blockstate for a block that declares placement or multi-block variants, covering the product
	 * of those with the block's facing and powered state. Returns false when the block declares neither, so
	 * the caller falls through to the plain blockstate for its type.
	 */
	private boolean generateVariantBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId,
	                                          Identifier baseModelId, List<Direction> facings) {
		List<String> placements = PlacementVariants.declared(block);
		List<String> parts = MultiBlockVariants.declared(block);
		if (placements.isEmpty() && parts.isEmpty()) return false;

		Identifier poweredModelId = resolvePoweredModelId(block, blockId, baseModelId);
		boolean powered = hasPoweredState(block);

		ARRPGenerationHelper.generateVariantBlockState(resourcePack, blockId, placements, parts, facings, powered,
				(placement, part, isPowered) -> {
					if (isPowered) {
						Identifier variantPowered = resolvePoweredVariantModelId(block, blockId, placement, part);
						if (variantPowered != null) return variantPowered;
					}
					return resolveVariantModelId(block, blockId, placement, part, isPowered ? poweredModelId : baseModelId);
				},
				block.rendering.model_rotation_offset);
		return true;
	}

	/**
	 * The powered model for one variant, when the block gives each of them its own — a cell first, then a
	 * placement. Null when it declares none, leaving the block-wide {@code powered_model} to answer.
	 */
	private Identifier resolvePoweredVariantModelId(Block block, Identifier blockId, String placement, String part) {
		for (String variant : new String[]{part, placement}) {
			JsonElement declared = block.rendering.poweredVariantModel(variant);
			if (declared == null || declared.isJsonNull()) continue;

			if (declared.isJsonPrimitive() && declared.getAsJsonPrimitive().isString()) {
				String id = declared.getAsString();
				return NO_MODEL.equals(id) ? EMPTY_MODEL : Identifier.parse(id);
			}
			return Utils.appendAndPrependToPath(blockId, "block/", "_" + variant + "_powered");
		}
		return null;
	}

	/**
	 * Where one variant's model lives, most specific first — the multi-block cell, then the placement:
	 * - String model → use directly
	 * - Object model → generated at block/&lt;id&gt;_&lt;variant&gt;
	 * - Variant declared with shapes only → the block's own model, so geometry can vary without art doing so
	 */
	private Identifier resolveVariantModelId(Block block, Identifier blockId, String placement, String part, Identifier fallback) {
		if (part != null) {
			if (block.rendering.hasPartModelString(part)) {
				String declared = block.rendering.partModel(part).getAsString();
				return NO_MODEL.equals(declared) ? EMPTY_MODEL : Identifier.parse(declared);
			}
			if (block.rendering.hasPartModelObject(part)) {
				return Utils.appendAndPrependToPath(blockId, "block/", "_" + part);
			}

			// One model on the origin covers the structure, so the cells around it draw nothing.
			Block.MultiBlockInformation multiBlock = MultiBlockVariants.information(block);
			if (multiBlock != null && multiBlock.wholeModel && !MultiBlockVariants.ORIGIN.equals(part)) {
				return EMPTY_MODEL;
			}
		}
		if (placement != null) {
			if (block.rendering.hasPlacementModelString(placement)) {
				String declared = block.rendering.placementModel(placement).getAsString();
				return NO_MODEL.equals(declared) ? EMPTY_MODEL : Identifier.parse(declared);
			}
			if (block.rendering.hasPlacementModelObject(placement)) {
				return Utils.appendAndPrependToPath(blockId, "block/", "_" + placement);
			}
		}
		return fallback;
	}

	/**
	 * The facings the generated blockstate has to multiply the variants by. Every state of the block needs a
	 * variant of its own, so a plain block that declares a facing property gets the full product too.
	 */
	private List<Direction> variantFacings(Block block) {
		String[] vanillaProperties = block.information.vanillaProperties;
		if (vanillaProperties == null) return List.of();

		for (String name : vanillaProperties) {
			switch (name.toLowerCase(Locale.ROOT)) {
				case "facing" -> {
					return List.of(Direction.values());
				}
				case "horizontal_facing" -> {
					return List.copyOf(Direction.Plane.HORIZONTAL.stream().toList());
				}
			}
		}
		return List.of();
	}

	/** Models given inline for a variant, written to block/&lt;id&gt;_&lt;variant&gt;. */
	private void generateVariantBlockModels(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
		for (String placement : PlacementVariants.declared(block)) {
			if (!block.rendering.hasPlacementModelObject(placement)) continue;
			TextureAndModelInformation info = block.rendering.getPlacementModel(placement);
			ARRPGenerationHelper.generateBlockModel(resourcePack, Utils.appendToPath(blockId, "_" + placement),
					info.parent, info.textures);
		}
		for (String part : MultiBlockVariants.declared(block)) {
			if (!block.rendering.hasPartModelObject(part)) continue;
			TextureAndModelInformation info = block.rendering.getPartModel(part);
			ARRPGenerationHelper.generateBlockModel(resourcePack, Utils.appendToPath(blockId, "_" + part),
					info.parent, info.textures);
		}
		if (block.rendering.poweredModels != null) {
			for (String variant : block.rendering.poweredModels.keySet()) {
				if (!block.rendering.hasPoweredVariantModelObject(variant)) continue;
				TextureAndModelInformation info = block.rendering.getPoweredVariantModel(variant);
				ARRPGenerationHelper.generateBlockModel(resourcePack, Utils.appendToPath(blockId, "_" + variant + "_powered"),
						info.parent, info.textures);
			}
		}
	}

	/**
	 * Resolves the powered model ID for a block.
	 * - String powered_model → use directly
	 * - Object powered_model → will be generated at block/<id>_powered
	 * - No powered_model → fall back to the unpowered model (same appearance for both states)
	 */
	/**
	 * Whether this block actually carries the {@code powered} state, which is what decides if its
	 * blockstate may key on one. Asking for {@code powerable} is not enough on its own: the state comes
	 * from the block's implementation, and only these types have one that adds it. A blockstate naming a
	 * property the block does not have is an error, not a model that never shows.
	 */
	private boolean hasPoweredState(Block block) {
		if (block.information == null || !(block.information.powerable || block.information.toggleable)) return false;

		Block.BlockType type = block.getBlockType();
		if (type == null) return true;
		return switch (type) {
			case BLOCK, WOOD, HORIZONTAL_DIRECTIONAL, DIRECTIONAL, ROTATED_PILLAR -> true;
			default -> false;
		};
	}

	private Identifier resolvePoweredModelId(Block block, Identifier blockId, Identifier unpoweredModelId) {
		if (block.rendering.poweredModel == null) return unpoweredModelId;
		if (block.rendering.hasPoweredModelString()) {
			return Identifier.parse(block.rendering.poweredModel.getAsString());
		}
		if (block.rendering.hasPoweredModelObject()) {
			return Utils.appendAndPrependToPath(blockId, "block/", "_powered");
		}
		return unpoweredModelId;
	}

}
