package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.BushProperties;
import io.github.vampirestudios.obsidian.api.obsidian.block.CompanionBlocks;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.assets.blockstates.BlockState;
import net.vampirestudios.packwright.assets.blockstates.Multipart;
import net.vampirestudios.packwright.assets.blockstates.SimpleModel;
import net.vampirestudios.packwright.assets.blockstates.Variant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assets for the extra blocks a block declares through {@code additional_information} — its slab,
 * stairs, wall, fence, fence gate, door, trapdoor, button and pressure plate.
 *
 * <p>Those blocks are registered by the block module, but nothing used to write a blockstate, a model
 * or an item definition for any of them, so every one of them rendered as the missing-model cube. Each
 * shape here gets vanilla's own model parents, textured from what the base block declares, and the
 * blockstate vanilla writes for that shape.
 *
 * <p>Anything the pack ships itself wins: a blockstate, model or item definition already in the pack is
 * never overwritten.
 */
public final class DerivedBlockAssets {

	private DerivedBlockAssets() {
	}

	private static final Identifier STAIRS = mc("block/stairs");
	private static final Identifier INNER_STAIRS = mc("block/inner_stairs");
	private static final Identifier OUTER_STAIRS = mc("block/outer_stairs");
	private static final Identifier CUBE_BOTTOM_TOP = mc("block/cube_bottom_top");
	private static final Identifier SLAB = mc("block/slab");
	private static final Identifier SLAB_TOP = mc("block/slab_top");
	private static final Identifier WALL_POST = mc("block/template_wall_post");
	private static final Identifier WALL_SIDE = mc("block/template_wall_side");
	private static final Identifier WALL_SIDE_TALL = mc("block/template_wall_side_tall");
	private static final Identifier WALL_INVENTORY = mc("block/wall_inventory");
	private static final Identifier FENCE_POST = mc("block/fence_post");
	private static final Identifier FENCE_SIDE = mc("block/fence_side");
	private static final Identifier FENCE_INVENTORY = mc("block/fence_inventory");
	private static final Identifier FENCE_GATE = mc("block/template_fence_gate");
	private static final Identifier FENCE_GATE_OPEN = mc("block/template_fence_gate_open");
	private static final Identifier FENCE_GATE_WALL = mc("block/template_fence_gate_wall");
	private static final Identifier FENCE_GATE_WALL_OPEN = mc("block/template_fence_gate_wall_open");
	private static final Identifier DOOR_BOTTOM_LEFT = mc("block/door_bottom_left");
	private static final Identifier DOOR_BOTTOM_LEFT_OPEN = mc("block/door_bottom_left_open");
	private static final Identifier DOOR_BOTTOM_RIGHT = mc("block/door_bottom_right");
	private static final Identifier DOOR_BOTTOM_RIGHT_OPEN = mc("block/door_bottom_right_open");
	private static final Identifier DOOR_TOP_LEFT = mc("block/door_top_left");
	private static final Identifier DOOR_TOP_LEFT_OPEN = mc("block/door_top_left_open");
	private static final Identifier DOOR_TOP_RIGHT = mc("block/door_top_right");
	private static final Identifier DOOR_TOP_RIGHT_OPEN = mc("block/door_top_right_open");
	private static final Identifier TRAPDOOR_BOTTOM = mc("block/template_orientable_trapdoor_bottom");
	private static final Identifier TRAPDOOR_TOP = mc("block/template_orientable_trapdoor_top");
	private static final Identifier TRAPDOOR_OPEN = mc("block/template_orientable_trapdoor_open");
	private static final Identifier BUTTON = mc("block/button");
	private static final Identifier BUTTON_PRESSED = mc("block/button_pressed");
	private static final Identifier BUTTON_INVENTORY = mc("block/button_inventory");
	private static final Identifier PRESSURE_PLATE_UP = mc("block/pressure_plate_up");
	private static final Identifier PRESSURE_PLATE_DOWN = mc("block/pressure_plate_down");
	private static final Identifier ITEM_GENERATED = mc("item/generated");

	private static final Identifier PANE_POST = mc("block/template_glass_pane_post");
	private static final Identifier PANE_SIDE = mc("block/template_glass_pane_side");
	private static final Identifier PANE_SIDE_ALT = mc("block/template_glass_pane_side_alt");
	private static final Identifier PANE_NOSIDE = mc("block/template_glass_pane_noside");
	private static final Identifier PANE_NOSIDE_ALT = mc("block/template_glass_pane_noside_alt");
	/** One template per candle count, in the order the {@code candles} state counts them. */
	private static final Identifier[] CANDLES = {
			mc("block/template_candle"), mc("block/template_two_candles"),
			mc("block/template_three_candles"), mc("block/template_four_candles")
	};
	private static final Identifier CAKE = mc("block/cake");
	private static final Identifier CAMPFIRE = mc("block/template_campfire");
	private static final Identifier CAMPFIRE_OFF = mc("block/campfire_off");
	private static final Identifier LEVER = mc("block/lever");
	private static final Identifier LEVER_ON = mc("block/lever_on");
	private static final Identifier CHAIN = mc("block/template_chain");
	private static final Identifier END_ROD = mc("block/end_rod");
	private static final Identifier LADDER = mc("block/ladder");
	private static final Identifier CROP = mc("block/crop");
	private static final Identifier CROSS = mc("block/cross");
	private static final Identifier TORCH = mc("block/template_torch");
	private static final Identifier CARPET = mc("block/carpet");
	private static final Identifier BED_HEAD = mc("block/template_bed_head");
	private static final Identifier BED_FOOT = mc("block/template_bed_foot");

	private static final String[] HORIZONTAL = {"north", "south", "west", "east"};

	private static Identifier mc(String path) {
		return Identifier.fromNamespaceAndPath("minecraft", path);
	}

	/**
	 * Writes the assets for every extra block {@code block} declares.
	 *
	 * @param blockId the block's own id, which is where its base model lives
	 */
	public static void generate(RuntimeResourcePack pack, Block block, Identifier blockId) {
		List<CompanionBlocks.Declared> variants = CompanionBlocks.declared(block);
		if (variants.isEmpty()) return;

		Textures blockTextures = resolveTextures(block, blockId);
		Identifier baseModel = resolveBaseModel(block, blockId);

		for (CompanionBlocks.Declared variant : variants) {
			Identifier id = variant.id();
			// A variant that names its own textures is built from those; the rest follow the block.
			Textures textures = variantTextures(variant, blockTextures);

			switch (variant.type()) {
				case SLAB -> slab(pack, block, id, textures, baseModel);
				case STAIRS -> stairs(pack, block, id, textures);
				case WALL -> wall(pack, block, id, textures);
				case FENCE -> fence(pack, block, id, textures);
				case FENCE_GATE -> fenceGate(pack, block, id, textures);
				case DOOR -> door(pack, block, id, textures);
				case TRAPDOOR -> trapdoor(pack, block, id, textures);
				case BUTTON -> button(pack, block, id, textures);
				case PRESSURE_PLATE -> pressurePlate(pack, block, id, textures);
			}
		}
	}

	private static Textures variantTextures(CompanionBlocks.Declared variant, Textures fallback) {
		TextureAndModelInformation declared = variant.options().getBlockModel();
		if (declared == null || declared.textures == null || declared.textures.isEmpty()) return fallback;
		return new Textures(declared.textures, fallback.fallback());
	}

	/**
	 * Writes the assets for a block that <em>is</em> one of these shapes — {@code "block_type": "stairs"} and
	 * the rest — rather than one that has them alongside it. Their models and blockstate live under the
	 * block's own id, so the caller has to leave the plain model and blockstate for the block alone.
	 *
	 * @return whether this block's type is one of the shapes and was handled here
	 */
	public static boolean generateForType(RuntimeResourcePack pack, Block block, Identifier blockId) {
		Block.BlockType type = block.getBlockType();
		if (type == null) return false;

		Textures textures = resolveTextures(block, blockId);
		switch (type) {
			case STAIRS -> stairs(pack, block, blockId, textures);
			case SLAB -> {
				// A standalone slab has no full block to double up into, so it needs one of its own.
				Identifier full = Utils.appendToPath(blockId, "_double");
				model(pack, full, CUBE_BOTTOM_TOP, textures.sideTopBottom());
				slab(pack, block, blockId, textures, blockModel(full));
			}
			case WALL -> wall(pack, block, blockId, textures);
			case FENCE -> fence(pack, block, blockId, textures);
			case FENCE_GATE -> fenceGate(pack, block, blockId, textures);
			case DOOR -> door(pack, block, blockId, textures);
			case TRAPDOOR -> trapdoor(pack, block, blockId, textures);
			case BUTTON -> button(pack, block, blockId, textures);
			case PRESSURE_PLATE -> pressurePlate(pack, block, blockId, textures);
			case PANE -> pane(pack, block, blockId, textures);
			case CANDLE -> candle(pack, block, blockId, textures);
			case CAKE -> cake(pack, block, blockId, textures);
			case CAMPFIRE -> campfire(pack, block, blockId, textures);
			case LEVER -> lever(pack, block, blockId, textures);
			case CHAIN -> chain(pack, block, blockId, textures);
			case ROD -> rod(pack, block, blockId, textures);
			case LADDER -> ladder(pack, block, blockId, textures);
			case TORCH -> torch(pack, block, blockId, textures);
			case CARPET -> carpet(pack, block, blockId, textures);
			case CROP -> crop(pack, block, blockId, textures);
			case BUSH -> bush(pack, block, blockId, textures);
			case BED -> bed(pack, block, blockId, textures);
			default -> {
				return false;
			}
		}
		return true;
	}

	/* ---------------------------------------------------------------------------------------------- */

	private static void slab(RuntimeResourcePack pack, Block block, Identifier id, Textures tex, Identifier fullModel) {
		model(pack, id, SLAB, tex.sideTopBottom());
		model(pack, Utils.appendToPath(id, "_top"), SLAB_TOP, tex.sideTopBottom());

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			variant.put("type=bottom", BlockState.model(blockModel(id)));
			variant.put("type=top", BlockState.model(blockModel(Utils.appendToPath(id, "_top"))));
			variant.put("type=double", BlockState.model(fullModel));
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	private static void stairs(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier inner = Utils.appendToPath(id, "_inner");
		Identifier outer = Utils.appendToPath(id, "_outer");
		model(pack, id, STAIRS, tex.sideTopBottom());
		model(pack, inner, INNER_STAIRS, tex.sideTopBottom());
		model(pack, outer, OUTER_STAIRS, tex.sideTopBottom());

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			// Vanilla's own table: the models are authored facing east on the bottom half, and every other
			// state is that model turned. The left corners are the right ones turned a further 270°, and
			// flipping a corner onto the top half mirrors it, which costs another quarter turn — the
			// straight piece is symmetric, so it only ever gets the flip.
			for (String half : new String[]{"bottom", "top"}) {
				boolean top = half.equals("top");
				int x = top ? 180 : 0;
				int mirrored = top ? 90 : 0;
				for (String facing : HORIZONTAL) {
					int y = yForStairFacing(facing);
					int right = Math.floorMod(y + mirrored, 360);
					int left = Math.floorMod(y + 270 + mirrored, 360);
					put(variant, key(facing, half, "straight"), blockModel(id), x, y, x != 0 || y != 0);
					put(variant, key(facing, half, "inner_right"), blockModel(inner), x, right, x != 0 || right != 0);
					put(variant, key(facing, half, "outer_right"), blockModel(outer), x, right, x != 0 || right != 0);
					put(variant, key(facing, half, "inner_left"), blockModel(inner), x, left, x != 0 || left != 0);
					put(variant, key(facing, half, "outer_left"), blockModel(outer), x, left, x != 0 || left != 0);
				}
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	/** Vanilla authors stairs facing east, so east is the unturned one. */
	private static int yForStairFacing(String facing) {
		return switch (facing) {
			case "west" -> 180;
			case "south" -> 90;
			case "north" -> 270;
			default -> 0;
		};
	}

	private static String key(String facing, String half, String shape) {
		return "facing=" + facing + ",half=" + half + ",shape=" + shape;
	}

	private static void wall(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier post = Utils.appendToPath(id, "_post");
		Identifier side = Utils.appendToPath(id, "_side");
		Identifier sideTall = Utils.appendToPath(id, "_side_tall");
		Identifier inventory = Utils.appendToPath(id, "_inventory");

		model(pack, post, WALL_POST, tex.named("wall"));
		model(pack, side, WALL_SIDE, tex.named("wall"));
		model(pack, sideTall, WALL_SIDE_TALL, tex.named("wall"));
		model(pack, inventory, WALL_INVENTORY, tex.named("wall"));

		if (!hasBlockState(pack, id)) {
			BlockState state = BlockState.state();
			state.add(Multipart.multipart()
					.when(Map.of("up", "true"))
					.addModel(BlockState.model(blockModel(post))));
			for (String connection : new String[]{"low", "tall"}) {
				Identifier arm = connection.equals("tall") ? sideTall : side;
				for (String facing : HORIZONTAL) {
					state.add(Multipart.multipart()
							.when(Map.of(facing, connection))
							.addModel(turned(blockModel(arm), 0, yForConnection(facing), true)));
				}
			}
			pack.addBlockState(state, id);
		}

		itemDefinition(pack, block, id, blockModel(inventory));
	}

	private static void fence(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier post = Utils.appendToPath(id, "_post");
		Identifier side = Utils.appendToPath(id, "_side");
		Identifier inventory = Utils.appendToPath(id, "_inventory");

		model(pack, post, FENCE_POST, tex.named("texture"));
		model(pack, side, FENCE_SIDE, tex.named("texture"));
		model(pack, inventory, FENCE_INVENTORY, tex.named("texture"));

		if (!hasBlockState(pack, id)) {
			BlockState state = BlockState.state();
			// The post is drawn for every state, so this part carries no condition at all.
			state.add(Multipart.multipart().addModel(BlockState.model(blockModel(post))));
			for (String facing : HORIZONTAL) {
				state.add(Multipart.multipart()
						.when(Map.of(facing, "true"))
						.addModel(turned(blockModel(side), 0, yForConnection(facing), true)));
			}
			pack.addBlockState(state, id);
		}

		itemDefinition(pack, block, id, blockModel(inventory));
	}

	/** Connection arms are authored pointing north. */
	private static int yForConnection(String facing) {
		return switch (facing) {
			case "east" -> 90;
			case "south" -> 180;
			case "west" -> 270;
			default -> 0;
		};
	}

	private static void fenceGate(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier open = Utils.appendToPath(id, "_open");
		Identifier inWall = Utils.appendToPath(id, "_wall");
		Identifier inWallOpen = Utils.appendToPath(id, "_wall_open");

		model(pack, id, FENCE_GATE, tex.named("texture"));
		model(pack, open, FENCE_GATE_OPEN, tex.named("texture"));
		model(pack, inWall, FENCE_GATE_WALL, tex.named("texture"));
		model(pack, inWallOpen, FENCE_GATE_WALL_OPEN, tex.named("texture"));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String facing : HORIZONTAL) {
				int y = yForGateFacing(facing);
				put(variant, "facing=" + facing + ",in_wall=false,open=false", blockModel(id), 0, y, true);
				put(variant, "facing=" + facing + ",in_wall=false,open=true", blockModel(open), 0, y, true);
				put(variant, "facing=" + facing + ",in_wall=true,open=false", blockModel(inWall), 0, y, true);
				put(variant, "facing=" + facing + ",in_wall=true,open=true", blockModel(inWallOpen), 0, y, true);
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	/** Fence gates are authored facing south. */
	private static int yForGateFacing(String facing) {
		return switch (facing) {
			case "west" -> 90;
			case "north" -> 180;
			case "east" -> 270;
			default -> 0;
		};
	}

	private static void door(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier bottomLeft = Utils.appendToPath(id, "_bottom_left");
		Identifier bottomLeftOpen = Utils.appendToPath(id, "_bottom_left_open");
		Identifier bottomRight = Utils.appendToPath(id, "_bottom_right");
		Identifier bottomRightOpen = Utils.appendToPath(id, "_bottom_right_open");
		Identifier topLeft = Utils.appendToPath(id, "_top_left");
		Identifier topLeftOpen = Utils.appendToPath(id, "_top_left_open");
		Identifier topRight = Utils.appendToPath(id, "_top_right");
		Identifier topRightOpen = Utils.appendToPath(id, "_top_right_open");

		Map<String, Identifier> doorTextures = new LinkedHashMap<>();
		doorTextures.put("bottom", tex.bottom());
		doorTextures.put("top", tex.top());
		model(pack, bottomLeft, DOOR_BOTTOM_LEFT, doorTextures);
		model(pack, bottomLeftOpen, DOOR_BOTTOM_LEFT_OPEN, doorTextures);
		model(pack, bottomRight, DOOR_BOTTOM_RIGHT, doorTextures);
		model(pack, bottomRightOpen, DOOR_BOTTOM_RIGHT_OPEN, doorTextures);
		model(pack, topLeft, DOOR_TOP_LEFT, doorTextures);
		model(pack, topLeftOpen, DOOR_TOP_LEFT_OPEN, doorTextures);
		model(pack, topRight, DOOR_TOP_RIGHT, doorTextures);
		model(pack, topRightOpen, DOOR_TOP_RIGHT_OPEN, doorTextures);

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String half : new String[]{"lower", "upper"}) {
				boolean lower = half.equals("lower");
				for (String facing : HORIZONTAL) {
					int y = yForDoorFacing(facing);
					// Swinging a door open turns it a quarter about its hinge, in the direction that hinge
					// is on — which is why the open states are turned and the closed ones are not.
					doorVariant(variant, facing, half, "left", false, lower ? bottomLeft : topLeft, y);
					doorVariant(variant, facing, half, "right", false, lower ? bottomRight : topRight, y);
					doorVariant(variant, facing, half, "left", true, lower ? bottomLeftOpen : topLeftOpen,
							Math.floorMod(y + 90, 360));
					doorVariant(variant, facing, half, "right", true, lower ? bottomRightOpen : topRightOpen,
							Math.floorMod(y + 270, 360));
				}
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		// A door's item is a flat sprite, not the block model.
		Identifier itemModel = Utils.prependToPath(id, "item/");
		if (!hasResource(pack, itemModel, "models/")) {
			ARRPGenerationHelper.generateItemModel1(pack, itemModel, ITEM_GENERATED, Map.of("layer0", tex.side()));
		}
		itemDefinition(pack, block, id, itemModel);
	}

	private static void doorVariant(Variant variant, String facing, String half, String hinge, boolean open,
	                                Identifier model, int y) {
		put(variant, "facing=" + facing + ",half=" + half + ",hinge=" + hinge + ",open=" + open,
				blockModel(model), 0, y, false);
	}

	/** Door models are authored facing east. */
	private static int yForDoorFacing(String facing) {
		return switch (facing) {
			case "south" -> 90;
			case "west" -> 180;
			case "north" -> 270;
			default -> 0;
		};
	}

	private static void trapdoor(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier bottom = Utils.appendToPath(id, "_bottom");
		Identifier top = Utils.appendToPath(id, "_top");
		Identifier open = Utils.appendToPath(id, "_open");

		model(pack, bottom, TRAPDOOR_BOTTOM, tex.named("texture"));
		model(pack, top, TRAPDOOR_TOP, tex.named("texture"));
		model(pack, open, TRAPDOOR_OPEN, tex.named("texture"));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String facing : HORIZONTAL) {
				int y = yForConnection(facing);
				// Closed trapdoors lie flat, so facing does not rotate their model. Both open halves use the
				// same upright model and rotation; the hinge position is already represented by the block state.
				put(variant, "facing=" + facing + ",half=bottom,open=false", blockModel(bottom), 0, 0, false);
				put(variant, "facing=" + facing + ",half=top,open=false", blockModel(top), 0, 0, false);
				put(variant, "facing=" + facing + ",half=bottom,open=true", blockModel(open), 0, y, false);
				put(variant, "facing=" + facing + ",half=top,open=true", blockModel(open), 0, y, false);
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(bottom));
	}

	private static void button(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier pressed = Utils.appendToPath(id, "_pressed");
		Identifier inventory = Utils.appendToPath(id, "_inventory");

		model(pack, id, BUTTON, tex.named("texture"));
		model(pack, pressed, BUTTON_PRESSED, tex.named("texture"));
		model(pack, inventory, BUTTON_INVENTORY, tex.named("texture"));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String face : new String[]{"floor", "wall", "ceiling"}) {
				for (String facing : HORIZONTAL) {
					int y = yForConnection(facing);
					int x = switch (face) {
						case "wall" -> 90;
						case "ceiling" -> 180;
						default -> 0;
					};
					// On a wall the button lies against it; on a ceiling it is upside down, which swaps the
					// side it points to.
					if (face.equals("ceiling")) y = Math.floorMod(y + 180, 360);
					for (String powered : new String[]{"false", "true"}) {
						Identifier model = powered.equals("true") ? pressed : id;
						put(variant, "face=" + face + ",facing=" + facing + ",powered=" + powered,
								blockModel(model), x, y, face.equals("wall"));
					}
				}
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(inventory));
	}

	private static void pressurePlate(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier down = Utils.appendToPath(id, "_down");

		model(pack, id, PRESSURE_PLATE_UP, tex.named("texture"));
		model(pack, down, PRESSURE_PLATE_DOWN, tex.named("texture"));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			variant.put("powered=false", BlockState.model(blockModel(id)));
			variant.put("powered=true", BlockState.model(blockModel(down)));
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	/* ---- Blocks that are a shape in themselves, rather than a companion of one ---------------------- */

	/**
	 * A pane, which draws a post plus a piece per side — and, unlike a fence, a piece per side it is
	 * <em>not</em> connected to, which is what closes off the open edges.
	 */
	private static void pane(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier post = Utils.appendToPath(id, "_post");
		Identifier side = Utils.appendToPath(id, "_side");
		Identifier sideAlt = Utils.appendToPath(id, "_side_alt");
		Identifier noSide = Utils.appendToPath(id, "_noside");
		Identifier noSideAlt = Utils.appendToPath(id, "_noside_alt");

		Map<String, Identifier> faces = new LinkedHashMap<>();
		faces.put("pane", tex.side());
		faces.put("edge", tex.pick("edge", "end", "top", "side", "all"));

		model(pack, post, PANE_POST, faces);
		model(pack, side, PANE_SIDE, faces);
		model(pack, sideAlt, PANE_SIDE_ALT, faces);
		model(pack, noSide, PANE_NOSIDE, Map.of("pane", tex.side()));
		model(pack, noSideAlt, PANE_NOSIDE_ALT, Map.of("pane", tex.side()));

		if (!hasBlockState(pack, id)) {
			BlockState state = BlockState.state();
			state.add(Multipart.multipart().addModel(BlockState.model(blockModel(post))));
			state.add(part("north", "true", blockModel(side), 0));
			state.add(part("east", "true", blockModel(side), 90));
			state.add(part("south", "true", blockModel(sideAlt), 0));
			state.add(part("west", "true", blockModel(sideAlt), 90));
			state.add(part("north", "false", blockModel(noSide), 0));
			state.add(part("east", "false", blockModel(noSideAlt), 0));
			state.add(part("south", "false", blockModel(noSideAlt), 90));
			state.add(part("west", "false", blockModel(noSide), 270));
			pack.addBlockState(state, id);
		}

		flatItem(pack, block, id, tex.side());
	}

	private static Multipart part(String property, String value, Identifier model, int y) {
		return Multipart.multipart().when(Map.of(property, value)).addModel(turned(model, 0, y, false));
	}

	/** One model per candle count, and the same again lit. */
	private static void candle(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		String[] counts = {"_one_candle", "_two_candles", "_three_candles", "_four_candles"};
		Identifier lit = tex.pick("lit", "candle_lit", "all", "side", "texture");

		Variant variant = new Variant();
		for (int count = 1; count <= 4; count++) {
			Identifier unlitModel = Utils.appendToPath(id, counts[count - 1]);
			Identifier litModel = Utils.appendToPath(unlitModel, "_lit");

			model(pack, unlitModel, CANDLES[count - 1], Map.of("all", tex.side(), "particle", tex.side()));
			model(pack, litModel, CANDLES[count - 1], Map.of("all", lit, "particle", lit));

			variant.put("candles=" + count + ",lit=false", BlockState.model(blockModel(unlitModel)));
			variant.put("candles=" + count + ",lit=true", BlockState.model(blockModel(litModel)));
		}

		if (!hasBlockState(pack, id)) pack.addBlockState(BlockState.state(variant), id);
		flatItem(pack, block, id, tex.side());
	}

	/** A cake and the six slices taken out of it. */
	private static void cake(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Map<String, Identifier> faces = new LinkedHashMap<>(tex.sideTopBottom());
		faces.put("inside", tex.pick("inside", "side", "all"));
		faces.put("particle", tex.side());

		model(pack, id, CAKE, faces);
		Variant variant = new Variant();
		variant.put("bites=0", BlockState.model(blockModel(id)));
		for (int bites = 1; bites <= 6; bites++) {
			Identifier slice = Utils.appendToPath(id, "_slice" + bites);
			model(pack, slice, mc("block/cake_slice" + bites), faces);
			variant.put("bites=" + bites, BlockState.model(blockModel(slice)));
		}

		if (!hasBlockState(pack, id)) pack.addBlockState(BlockState.state(variant), id);
		itemDefinition(pack, block, id, blockModel(id));
	}

	private static void campfire(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier off = Utils.appendToPath(id, "_off");
		Identifier log = tex.pick("log", "side", "all");

		model(pack, id, CAMPFIRE, Map.of(
				"fire", tex.pick("fire", "lit", "all"),
				"lit_log", tex.pick("lit_log", "log", "side", "all"),
				"log", log));
		// The unlit model has only its logs; there is no fire on it to texture.
		model(pack, off, CAMPFIRE_OFF, Map.of("log", log, "particle", log));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String facing : HORIZONTAL) {
				int y = yForGateFacing(facing);
				put(variant, "facing=" + facing + ",lit=true", blockModel(id), 0, y, false);
				put(variant, "facing=" + facing + ",lit=false", blockModel(off), 0, y, false);
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	private static void lever(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		// Named after the models they are built from, which is also how vanilla names them — and vanilla
		// draws the "_on" model when the lever is *not* powered, since that is the flipped-up position.
		Identifier flipped = Utils.appendToPath(id, "_on");
		Map<String, Identifier> faces = Map.of(
				"base", tex.pick("base", "side", "all"),
				"lever", tex.pick("lever", "texture", "side", "all"));

		model(pack, id, LEVER, faces);
		model(pack, flipped, LEVER_ON, faces);

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String face : new String[]{"floor", "wall", "ceiling"}) {
				int x = switch (face) {
					case "wall" -> 90;
					case "ceiling" -> 180;
					default -> 0;
				};
				for (String facing : HORIZONTAL) {
					int y = yForConnection(facing);
					if (face.equals("ceiling")) y = Math.floorMod(y + 180, 360);
					put(variant, "face=" + face + ",facing=" + facing + ",powered=false", blockModel(flipped), x, y, false);
					put(variant, "face=" + face + ",facing=" + facing + ",powered=true", blockModel(id), x, y, false);
				}
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		flatItem(pack, block, id, tex.pick("lever", "texture", "side", "all"));
	}

	private static void chain(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		model(pack, id, CHAIN, Map.of("all", tex.side(), "texture", tex.side(), "particle", tex.side()));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			put(variant, "axis=y", blockModel(id), 0, 0, false);
			put(variant, "axis=z", blockModel(id), 90, 0, false);
			put(variant, "axis=x", blockModel(id), 90, 90, false);
			pack.addBlockState(BlockState.state(variant), id);
		}

		flatItem(pack, block, id, tex.side());
	}

	/** An end-rod-shaped block, which points any of the six ways. */
	private static void rod(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		model(pack, id, END_ROD, Map.of("end_rod", tex.side(), "particle", tex.side()));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			put(variant, "facing=up", blockModel(id), 0, 0, false);
			put(variant, "facing=down", blockModel(id), 180, 0, false);
			put(variant, "facing=north", blockModel(id), 90, 0, false);
			put(variant, "facing=south", blockModel(id), 90, 180, false);
			put(variant, "facing=east", blockModel(id), 90, 90, false);
			put(variant, "facing=west", blockModel(id), 90, 270, false);
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(id));
	}

	private static void ladder(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		model(pack, id, LADDER, Map.of("texture", tex.side(), "particle", tex.side()));

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String facing : HORIZONTAL) {
				put(variant, "facing=" + facing, blockModel(id), 0, yForConnection(facing), false);
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		flatItem(pack, block, id, tex.side());
	}

	private static void torch(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		model(pack, id, TORCH, Map.of("torch", tex.side(), "particle", tex.side()));
		if (!hasBlockState(pack, id)) {
			pack.addBlockState(BlockState.state(BlockState.variant(BlockState.model(blockModel(id)))), id);
		}
		flatItem(pack, block, id, tex.side());
	}

	private static void carpet(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		model(pack, id, CARPET, Map.of("wool", tex.side(), "particle", tex.side()));
		if (!hasBlockState(pack, id)) {
			pack.addBlockState(BlockState.state(BlockState.variant(BlockState.model(blockModel(id)))), id);
		}
		itemDefinition(pack, block, id, blockModel(id));
	}

	/**
	 * One model per growth stage, as far as the block grows.
	 *
	 * <p>A crop's age property is always vanilla's {@code age} 0–7, whatever {@code growable.max_age}
	 * says — that only caps how far growth advances, since the property is built before the block's own
	 * configuration is reachable. So the blockstate has to name all eight ages or the ones left out have
	 * no model at all, and the ages past the last stage draw it.
	 */
	private static void crop(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		int maxAge = block.growable != null && block.growable.max_age > 0
				? Math.min(block.growable.max_age, 7)
				: 7;

		Variant variant = new Variant();
		for (int age = 0; age <= 7; age++) {
			int stageIndex = Math.min(age, maxAge);
			Identifier stage = Utils.appendToPath(id, "_stage" + stageIndex);
			// A pack may name a texture per stage; without one every stage draws the block's own.
			if (age == stageIndex) {
				model(pack, stage, CROP, Map.of("crop", tex.pick("stage" + stageIndex, "crop", "side", "all")));
			}
			variant.put("age=" + age, BlockState.model(blockModel(stage)));
		}

		if (!hasBlockState(pack, id)) pack.addBlockState(BlockState.state(variant), id);
		flatItem(pack, block, id, tex.pick("stage" + maxAge, "crop", "side", "all"));
	}

	/**
	 * A berry bush: one cross model per growth stage, the way vanilla's berry bushes are drawn.
	 *
	 * <p>The state property holds more ages than the bush necessarily grows through — a bush stopping at 2
	 * still has an {@code age_3} property — and a blockstate that named only the ages it uses would leave
	 * the rest with no model at all, so the ones past the last stage draw it.
	 */
	private static void bush(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		BushProperties bush = BushProperties.of(block);
		int maxAge = bush.resolvedMaxAge();

		Variant variant = new Variant();
		for (int age = 0; age < bush.ageStateCount(); age++) {
			int stageIndex = Math.min(age, maxAge);
			Identifier stage = Utils.appendToPath(id, "_stage" + stageIndex);
			// A pack may name a texture per stage; without one every stage draws the block's own.
			if (age == stageIndex) {
				model(pack, stage, CROSS, Map.of("cross", tex.pick("stage" + stageIndex, "cross", "side", "all")));
			}
			variant.put("age=" + age, BlockState.model(blockModel(stage)));
		}

		if (!hasBlockState(pack, id)) pack.addBlockState(BlockState.state(variant), id);
		flatItem(pack, block, id, tex.pick("stage" + maxAge, "cross", "side", "all"));
	}

	/**
	 * A bed, which is two blocks: the foot where it was placed and the head beyond it. The models are
	 * only what the game draws before the bed's own renderer takes over, but without them it draws nothing.
	 */
	private static void bed(RuntimeResourcePack pack, Block block, Identifier id, Textures tex) {
		Identifier head = Utils.appendToPath(id, "_head");
		Identifier foot = Utils.appendToPath(id, "_foot");

		Map<String, Identifier> faces = new LinkedHashMap<>();
		faces.put("up", tex.top());
		faces.put("east", tex.side());
		faces.put("west", tex.pick("west", "side", "all"));
		faces.put("particle", tex.side());

		model(pack, head, BED_HEAD, faces);
		model(pack, foot, BED_FOOT, faces);

		if (!hasBlockState(pack, id)) {
			Variant variant = new Variant();
			for (String facing : HORIZONTAL) {
				int y = yForConnection(facing);
				put(variant, "facing=" + facing + ",part=head", blockModel(head), 0, y, false);
				put(variant, "facing=" + facing + ",part=foot", blockModel(foot), 0, y, false);
			}
			pack.addBlockState(BlockState.state(variant), id);
		}

		itemDefinition(pack, block, id, blockModel(foot));
	}

	/** The flat sprite a thin block is held as, rather than the block model. */
	private static void flatItem(RuntimeResourcePack pack, Block block, Identifier id, Identifier texture) {
		Identifier itemModel = Utils.prependToPath(id, "item/");
		if (!hasResource(pack, itemModel, "models/")) {
			ARRPGenerationHelper.generateItemModel1(pack, itemModel, ITEM_GENERATED, Map.of("layer0", texture));
		}
		itemDefinition(pack, block, id, itemModel);
	}

	/* ---------------------------------------------------------------------------------------------- */

	private static void put(Variant variant, String key, Identifier model, int x, int y, boolean uvlock) {
		variant.put(key, turned(model, x, y, uvlock));
	}

	/**
	 * A fresh {@link SimpleModel} for one variant. These builders mutate and return the same object, so a
	 * model instance can never be shared between two variants — the second turn would move the first one too.
	 */
	private static SimpleModel turned(Identifier model, int x, int y, boolean uvlock) {
		SimpleModel simple = BlockState.model(model);
		if (x != 0) simple.x(x);
		if (y != 0) simple.y(y);
		if (uvlock) simple.uvlock();
		return simple;
	}

	private static Identifier blockModel(Identifier id) {
		return Utils.prependToPath(id, "block/");
	}

	private static void model(RuntimeResourcePack pack, Identifier id, Identifier parent, Map<String, Identifier> textures) {
		if (hasResource(pack, blockModel(id), "models/")) return;
		ARRPGenerationHelper.generateBlockModel(pack, id, parent, textures);
	}

	private static void itemDefinition(RuntimeResourcePack pack, Block block, Identifier id, Identifier modelId) {
		if (hasResource(pack, id, "items/")) return;
		ARRPGenerationHelper.generateBasicItemDefinition(pack, block, id, modelId);
	}

	private static boolean hasBlockState(RuntimeResourcePack pack, Identifier id) {
		return hasResource(pack, id, "blockstates/");
	}

	private static boolean hasResource(RuntimeResourcePack pack, Identifier id, String directory) {
		return pack.getResource(PackType.CLIENT_RESOURCES,
				Identifier.fromNamespaceAndPath(id.getNamespace(), directory + id.getPath() + ".json")) != null;
	}

	/* ---------------------------------------------------------------------------------------------- */

	/** Where the base block's own model ends up, which is what a slab's {@code double} state draws. */
	private static Identifier resolveBaseModel(Block block, Identifier blockId) {
		if (block.rendering != null) {
			if (block.rendering.hasBlockModelString()) {
				return Identifier.parse(block.rendering.blockModel.getAsString());
			}
			if (block.rendering.hasLegacyModelString()) {
				return Identifier.parse(block.rendering.model.getAsString());
			}
		}
		return blockModel(blockId);
	}

	private static Textures resolveTextures(Block block, Identifier blockId) {
		Map<String, Identifier> declared = null;
		if (block.rendering != null) {
			TextureAndModelInformation blockModel = block.rendering.getBlockModel();
			if (blockModel != null && blockModel.textures != null && !blockModel.textures.isEmpty()) {
				declared = blockModel.textures;
			} else {
				TextureAndModelInformation legacy = block.rendering.getModel();
				if (legacy != null && legacy.textures != null && !legacy.textures.isEmpty()) {
					declared = legacy.textures;
				}
			}
		}
		return new Textures(declared, blockModel(blockId));
	}

	/**
	 * The three faces vanilla's shape models want, worked out from whatever the base block declared —
	 * a single {@code all} for a plain cube, {@code end}/{@code side} for a pillar, and so on. A block
	 * whose model is only a reference declares no textures at all, so its own texture path is the guess.
	 */
	private record Textures(Map<String, Identifier> declared, Identifier fallback) {

		Identifier side() {
			return pick("side", "all", "texture", "wall", "end", "top", "particle");
		}

		Identifier top() {
			return pick("top", "end", "all", "texture", "side", "particle");
		}

		Identifier bottom() {
			return pick("bottom", "end", "all", "texture", "side", "top", "particle");
		}

		Map<String, Identifier> sideTopBottom() {
			Map<String, Identifier> out = new LinkedHashMap<>();
			out.put("side", side());
			out.put("top", top());
			out.put("bottom", bottom());
			return out;
		}

		/** The one texture a shape with a single face variable wants, under the name it wants it. */
		Map<String, Identifier> named(String variable) {
			return Map.of(variable, side());
		}

		private Identifier pick(String... keys) {
			if (declared != null) {
				for (String key : keys) {
					Identifier texture = declared.get(key);
					if (texture != null) return texture;
				}
				for (Identifier texture : declared.values()) {
					if (texture != null) return texture;
				}
			}
			return fallback;
		}
	}

}
