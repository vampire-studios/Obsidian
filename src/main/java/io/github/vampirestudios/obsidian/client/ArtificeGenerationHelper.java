package io.github.vampirestudios.obsidian.client;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.StringResource;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ArtificeGenerationHelper {

	public static void generateBasicBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder ->
				blockStateBuilder.variant("", variant ->
						variant.model(Utils.prependToPath(name, "block/"))));
	}

	public static void generateBasicBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder ->
				blockStateBuilder.variant("", variant ->
						variant.model(Utils.prependToPath(modelId, "block/"))));
	}

	public static void generatePillarBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder -> {
			blockStateBuilder.variant("axis=y", variant ->
					variant.model(Utils.prependToPath(name, "block/")));
			blockStateBuilder.variant("axis=x", variant -> {
				variant.model(Utils.prependToPath(name, "block/"));
				variant.rotationX(90);
				variant.rotationY(90);
			});
			blockStateBuilder.variant("axis=z", variant -> {
				variant.model(Utils.prependToPath(name, "block/"));
				variant.rotationX(90);
			});
		});
	}

	public static void generatePillarBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder -> {
			blockStateBuilder.variant("axis=y", variant ->
					variant.model(Utils.prependToPath(modelId, "block/")));
			blockStateBuilder.variant("axis=x", variant -> {
				variant.model(Utils.prependToPath(modelId, "block/"));
				variant.rotationX(90);
				variant.rotationY(90);
			});
			blockStateBuilder.variant("axis=z", variant -> {
				variant.model(Utils.prependToPath(modelId, "block/"));
				variant.rotationX(90);
			});
		});
	}

	public static void generateHorizonalFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder -> {
			blockStateBuilder.variant("facing=north", variant ->
					variant.model(Utils.prependToPath(name, "block/")));
			blockStateBuilder.variant("facing=south", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(180));
			blockStateBuilder.variant("facing=east", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(90));
			blockStateBuilder.variant("facing=west", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(270));
		});
	}

	public static void generateFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockState(name, blockStateBuilder -> {
			blockStateBuilder.variant("facing=north", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationX(90));
			blockStateBuilder.variant("facing=south", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(180).rotationX(90));
			blockStateBuilder.variant("facing=east", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(90).rotationX(90));
			blockStateBuilder.variant("facing=west", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationY(270).rotationX(90));
			blockStateBuilder.variant("facing=up", variant ->
					variant.model(Utils.prependToPath(name, "block/")));
			blockStateBuilder.variant("facing=down", variant ->
					variant.model(Utils.prependToPath(name, "block/")).rotationX(180));
		});
	}

	public static void generateAllBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cube_all"));
			modelBuilder.texture("all", Utils.prependToPath(name, "block/"));
		});
	}

	public static void generateAllBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cube_all"));
			modelBuilder.texture("all", Utils.prependToPath(texture, "block/"));
		});
	}

	public static void generateCrossBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cross"));
			modelBuilder.texture("cross", Utils.prependToPath(name, "block/"));
		});
	}

	public static void generateCrossBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cross"));
			modelBuilder.texture("cross", Utils.prependToPath(texture, "block/"));
		});
	}

	public static void generateColumnBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier endTexture, Identifier sideTexture) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cube_column"));
			modelBuilder.texture("end", Utils.prependToPath(endTexture, "block/"));
			modelBuilder.texture("side", Utils.prependToPath(sideTexture, "block/"));
		});
	}

	public static void generateTopBottomBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/cube_top_bottom"));
			modelBuilder.texture("top", topTexture);
			modelBuilder.texture("bottom", bottomTexture);
			modelBuilder.texture("side", sideTexture);
		});
	}

	public static void generateLadderBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
			modelBuilder.parent(new Identifier("block/ladder"));
			modelBuilder.texture("texture", Utils.prependToPath(name, "block/"));
		});
	}

	public static void generateBlockItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addItemModel(name, modelBuilder -> modelBuilder.parent(Utils.prependToPath(name, "block/")));
	}

	public static void generateBlockItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
		clientResourcePackBuilder.addItemModel(name, modelBuilder -> modelBuilder.parent(Utils.prependToPath(modelId, "block/")));
	}

	public static void generateSimpleItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		clientResourcePackBuilder.addItemModel(name, modelBuilder -> modelBuilder.parent(new Identifier("item/generated")).texture("layer0", Utils.prependToPath(name, "item/")));
	}

	public static void generateSimpleItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
		clientResourcePackBuilder.addItemModel(name, modelBuilder -> modelBuilder.parent(new Identifier("item/generated")).texture("layer0", texture));
	}

	public static void generateStairsBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
		pack.addBlockState(name, state -> {
			for (Direction d : Direction.values()) {
				if (!(d.equals(Direction.DOWN) || d.equals(Direction.UP))) {
					for (BlockHalf h : BlockHalf.values()) {
						for (StairShape s : StairShape.values()) {
							String varname = "facing=" + d.asString() + ",half=" + h.asString() + ",shape=" + s.asString();
							state.variant(varname, var -> {
								var.uvlock(true);
								int y = 0;
								switch (s) {
									case STRAIGHT:
										var.model(Utils.prependToPath(name, "block/"));
										switch (d) {
											case EAST:
												y = 0;
												break;
											case WEST:
												y = 180;
												break;
											case NORTH:
												y = 270;
												break;
											case SOUTH:
												y = 90;
												break;
										}
										break;
									case OUTER_RIGHT:
										var.model(Utils.appendAndPrependToPath(name, "block/", "_outer"));
										switch (h) {
											case BOTTOM:
												switch (d) {
													case EAST:
														y = 0;
														break;
													case WEST:
														y = 180;
														break;
													case NORTH:
														y = 270;
														break;
													case SOUTH:
														y = 90;
														break;
												}
												break;
											case TOP:
												switch (d) {
													case EAST:
														y = 90;
														break;
													case WEST:
														y = 270;
														break;
													case NORTH:
														y = 0;
														break;
													case SOUTH:
														y = 180;
														break;
												}
												break;
										}
										break;
									case OUTER_LEFT:
										var.model(Utils.appendAndPrependToPath(name, "block/", "_outer"));
										switch (h) {
											case BOTTOM:
												switch (d) {
													case EAST:
														y = 270;
														break;
													case WEST:
														y = 90;
														break;
													case NORTH:
														y = 180;
														break;
													case SOUTH:
														y = 0;
														break;
												}
												break;
											case TOP:
												switch (d) {
													case EAST:
														y = 0;
														break;
													case WEST:
														y = 180;
														break;
													case NORTH:
														y = 270;
														break;
													case SOUTH:
														y = 90;
														break;
												}
												break;
										}
										break;
									case INNER_RIGHT:
										var.model(Utils.appendAndPrependToPath(name, "block/", "_inner"));
										switch (h) {
											case BOTTOM:
												switch (d) {
													case EAST:
														y = 0;
														break;
													case WEST:
														y = 180;
														break;
													case SOUTH:
														y = 90;
														break;
													case NORTH:
														y = 270;
														break;
												}
												break;
											case TOP:
												switch (d) {
													case EAST:
														y = 90;
														break;
													case WEST:
														y = 270;
														break;
													case NORTH:
														y = 0;
														break;
													case SOUTH:
														y = 180;
														break;
												}
												break;
										}
										break;
									case INNER_LEFT:
										var.model(Utils.appendAndPrependToPath(name, "block/", "_inner"));
										switch (h) {
											case BOTTOM:
												switch (d) {
													case EAST:
														y = 270;
														break;
													case WEST:
														y = 90;
														break;
													case NORTH:
														y = 180;
														break;
													case SOUTH:
														y = 0;
														break;
												}
												break;
											case TOP:
												switch (d) {
													case EAST:
														y = 0;
														break;
													case WEST:
														y = 180;
														break;
													case NORTH:
														y = 270;
														break;
													case SOUTH:
														y = 90;
														break;
												}
												break;
										}
										break;
								}
								if (h.equals(BlockHalf.TOP)) var.rotationX(180);
								var.rotationY(y);
							});
						}
					}
				}
			}
			state.variant("facing=east,half=bottom,shape=straight", var -> {
				var.model(Utils.prependToPath(name, "block/"));
			});
			state.variant("facing=west,half=bottom,shape=straight", var -> {
				var.model(Utils.prependToPath(name, "block/"));
				var.rotationY(180);
				var.uvlock(true);
			});
		});
	}

	public static void generateStairsBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
		pack.addBlockModel(Utils.appendToPath(name, "_inner"), model -> {
			model.parent(new Identifier("block/stairs_inner"));
			model.texture("particle", texture);
			model.texture("side", texture);
			model.texture("top", texture);
			model.texture("bottom", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_outer"), model -> {
			model.parent(new Identifier("block/stairs_inner"));
			model.texture("particle", texture);
			model.texture("side", texture);
			model.texture("top", texture);
			model.texture("bottom", texture);
		});
		pack.addBlockModel(name, model -> {
			model.parent(new Identifier("block/stairs"));
			model.texture("particle", texture);
			model.texture("side", texture);
			model.texture("top", texture);
			model.texture("bottom", texture);
		});
	}

	public static void generateSlabBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier doubleBlockName) {
		pack.addBlockState(name, state -> {
			for (SlabType t : SlabType.values()) {
				state.variant("type=" + t.asString(), var -> {
					switch (t) {
						case BOTTOM:
							var.model(Utils.prependToPath(name, "block/"));
							break;
						case TOP:
							var.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
							break;
						case DOUBLE:
							var.model(Utils.prependToPath(doubleBlockName, "block/"));
							break;
					}
				});
			}
		});
	}

	public static void generateSlabBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
		pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
			model.parent(new Identifier("block/slab_top"));
			model.texture("particle", texture);
			model.texture("side", texture);
			model.texture("top", texture);
			model.texture("bottom", texture);
		});
		pack.addBlockModel(name, model -> {
			model.parent(new Identifier("block/slab"));
			model.texture("particle", texture);
			model.texture("side", texture);
			model.texture("top", texture);
			model.texture("bottom", texture);
		});
	}

	public static void generateFenceBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
		pack.addBlockState(name, state -> {
			state.multipartCase(caze -> {
				caze.apply(var -> {
					var.model(Utils.appendAndPrependToPath(name, "block/", "_post"));
				});
			});
			for (Direction d : Direction.values()) {
				if (d != Direction.UP && d != Direction.DOWN) {
					state.multipartCase(caze -> {
						caze.when(d.asString(), "true");
						caze.apply(var -> {
							var.model(Utils.appendAndPrependToPath(name, "block/", "_side"));
							var.uvlock(true);
							switch (d) {
								case EAST:
									var.rotationY(90);
									break;
								case WEST:
									var.rotationY(270);
									break;
								case SOUTH:
									var.rotationY(180);
									break;
							}
						});
					});
				}
			}
		});
	}

	public static void generateFenceBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
		pack.addBlockModel(Utils.appendToPath(name, "_inventory"), model -> {
			model.parent(new Identifier("block/fence_inventory"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_post"), model -> {
			model.parent(new Identifier("block/fence_post"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_side"), model -> {
			model.parent(new Identifier("block/fence_side"));
			model.texture("texture", texture);
		});
	}

	public static void generateFenceGateBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
		pack.addBlockState(name, state -> {
			for (Direction d : Direction.values()) {
				if (d != Direction.UP && d != Direction.DOWN) {
					state.variant("facing=" + d.asString() + ",in_wall=false,open=false", var -> {
						var.model(Utils.prependToPath(name, "block/"));
						var.uvlock(true);
						switch (d) {
							case NORTH:
								var.rotationY(180);
								break;
							case WEST:
								var.rotationY(90);
								break;
							case EAST:
								var.rotationY(270);
								break;
						}
					});
					state.variant("facing=" + d.asString() + ",in_wall=true,open=false", var -> {
						var.model(Utils.appendAndPrependToPath(name, "block/", "_wall"));
						var.uvlock(true);
						switch (d) {
							case NORTH:
								var.rotationY(180);
								break;
							case WEST:
								var.rotationY(90);
								break;
							case EAST:
								var.rotationY(270);
								break;
						}
					});
					state.variant("facing=" + d.asString() + ",in_wall=false,open=true", var -> {
						var.model(Utils.appendAndPrependToPath(name, "block/", "_open"));
						var.uvlock(true);
						switch (d) {
							case NORTH:
								var.rotationY(180);
								break;
							case WEST:
								var.rotationY(90);
								break;
							case EAST:
								var.rotationY(270);
								break;
						}
					});
					state.variant("facing=" + d.asString() + ",in_wall=true,open=true", var -> {
						var.model(Utils.appendAndPrependToPath(name, "block/", "_wall_open"));
						var.uvlock(true);
						switch (d) {
							case NORTH:
								var.rotationY(180);
								break;
							case WEST:
								var.rotationY(90);
								break;
							case EAST:
								var.rotationY(270);
								break;
						}
					});
				}
			}
		});
	}

	public static void generateFenceGateBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
		pack.addBlockModel(Utils.appendToPath(name, "_open"), model -> {
			model.parent(new Identifier("block/template_fence_gate_open"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_wall"), model -> {
			model.parent(new Identifier("block/template_fence_gate_wall"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_wall_open"), model -> {
			model.parent(new Identifier("block/template_fence_gate_wall_open"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(name, model -> {
			model.parent(new Identifier("block/template_fence_gate"));
			model.texture("texture", texture);
		});
	}

	public static void generateDoorBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier topTexture, Identifier bottomTexture) {
		pack.addBlockModel(Utils.appendToPath(name, "_bottom"), model -> {
			model.parent(new Identifier("block/door_bottom"));
			model.texture("texture", bottomTexture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_bottom_hinge"), model -> {
			model.parent(new Identifier("block/door_bottom_rh"));
			model.texture("texture", bottomTexture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
			model.parent(new Identifier("block/door_top"));
			model.texture("texture", topTexture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_top_hinge"), model -> {
			model.parent(new Identifier("block/door_top_rh"));
			model.texture("texture", topTexture);
		});
	}

	public static void generateDoorBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		String DOOR_JSON_TEMPLATE = "{\n" +
				"    \"variants\": {\n" +
				"        \"facing=east,half=lower,hinge=left,open=false\":  { \"model\": \"$namespace$:block/$$_bottom\" },\n" +
				"        \"facing=south,half=lower,hinge=left,open=false\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 90 },\n" +
				"        \"facing=west,half=lower,hinge=left,open=false\":  { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 180 },\n" +
				"        \"facing=north,half=lower,hinge=left,open=false\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 270 },\n" +
				"        \"facing=east,half=lower,hinge=right,open=false\":  { \"model\": \"$namespace$:block/$$_bottom_hinge\" },\n" +
				"        \"facing=south,half=lower,hinge=right,open=false\": { \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 90 },\n" +
				"        \"facing=west,half=lower,hinge=right,open=false\":  { \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 180 },\n" +
				"        \"facing=north,half=lower,hinge=right,open=false\": { \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 270 },\n" +
				"        \"facing=east,half=lower,hinge=left,open=true\":\t{ \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 90 },\n" +
				"        \"facing=south,half=lower,hinge=left,open=true\": { \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 180 },\n" +
				"        \"facing=west,half=lower,hinge=left,open=true\":\t{ \"model\": \"$namespace$:block/$$_bottom_hinge\", \"y\": 270 },\n" +
				"        \"facing=north,half=lower,hinge=left,open=true\": { \"model\": \"$namespace$:block/$$_bottom_hinge\" },\n" +
				"        \"facing=east,half=lower,hinge=right,open=true\":  { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 270 },\n" +
				"        \"facing=south,half=lower,hinge=right,open=true\": { \"model\": \"$namespace$:block/$$_bottom\" },\n" +
				"        \"facing=west,half=lower,hinge=right,open=true\":  { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 90 },\n" +
				"        \"facing=north,half=lower,hinge=right,open=true\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 180 },\n" +
				"        \"facing=east,half=upper,hinge=left,open=false\":  { \"model\": \"$namespace$:block/$$_top\" },\n" +
				"        \"facing=south,half=upper,hinge=left,open=false\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 90 },\n" +
				"        \"facing=west,half=upper,hinge=left,open=false\":  { \"model\": \"$namespace$:block/$$_top\", \"y\": 180 },\n" +
				"        \"facing=north,half=upper,hinge=left,open=false\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 270 },\n" +
				"        \"facing=east,half=upper,hinge=right,open=false\":  { \"model\": \"$namespace$:block/$$_top_hinge\" },\n" +
				"        \"facing=south,half=upper,hinge=right,open=false\": { \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 90 },\n" +
				"        \"facing=west,half=upper,hinge=right,open=false\":  { \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 180 },\n" +
				"        \"facing=north,half=upper,hinge=right,open=false\": { \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 270 },\n" +
				"        \"facing=east,half=upper,hinge=left,open=true\":\t{ \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 90 },\n" +
				"        \"facing=south,half=upper,hinge=left,open=true\": { \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 180 },\n" +
				"        \"facing=west,half=upper,hinge=left,open=true\":\t{ \"model\": \"$namespace$:block/$$_top_hinge\", \"y\": 270 },\n" +
				"        \"facing=north,half=upper,hinge=left,open=true\": { \"model\": \"$namespace$:block/$$_top_hinge\" },\n" +
				"        \"facing=east,half=upper,hinge=right,open=true\":  { \"model\": \"$namespace$:block/$$_top\", \"y\": 270 },\n" +
				"        \"facing=south,half=upper,hinge=right,open=true\": { \"model\": \"$namespace$:block/$$_top\" },\n" +
				"        \"facing=west,half=upper,hinge=right,open=true\":  { \"model\": \"$namespace$:block/$$_top\", \"y\": 90 },\n" +
				"        \"facing=north,half=upper,hinge=right,open=true\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 180 }\n" +
				"    }\n" +
				"}";
		clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(DOOR_JSON_TEMPLATE.replace("$namespace$", name.getNamespace()).replace("$$", name.getPath())));
	}

	public static void generateTrapdoorBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
		String TRAPDOOR_JSON_TEMPLATE = "{\n" +
				"    \"variants\": {\n" +
				"        \"facing=north,half=bottom,open=false\": { \"model\": \"$namespace$:block/$$_bottom\" },\n" +
				"        \"facing=south,half=bottom,open=false\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 180 },\n" +
				"        \"facing=east,half=bottom,open=false\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 90 },\n" +
				"        \"facing=west,half=bottom,open=false\": { \"model\": \"$namespace$:block/$$_bottom\", \"y\": 270 },\n" +
				"        \"facing=north,half=top,open=false\": { \"model\": \"$namespace$:block/$$_top\" },\n" +
				"        \"facing=south,half=top,open=false\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 180 },\n" +
				"        \"facing=east,half=top,open=false\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 90 },\n" +
				"        \"facing=west,half=top,open=false\": { \"model\": \"$namespace$:block/$$_top\", \"y\": 270 },\n" +
				"        \"facing=north,half=bottom,open=true\": { \"model\": \"$namespace$:block/$$_open\" },\n" +
				"        \"facing=south,half=bottom,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"y\": 180 },\n" +
				"        \"facing=east,half=bottom,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"y\": 90 },\n" +
				"        \"facing=west,half=bottom,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"y\": 270 },\n" +
				"        \"facing=north,half=top,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"x\": 180, \"y\": 180 },\n" +
				"        \"facing=south,half=top,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"x\": 180, \"y\": 0 },\n" +
				"        \"facing=east,half=top,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"x\": 180, \"y\": 270 },\n" +
				"        \"facing=west,half=top,open=true\": { \"model\": \"$namespace$:block/$$_open\", \"x\": 180, \"y\": 90 }\n" +
				"    }\n" +
				"}\n";
		clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(TRAPDOOR_JSON_TEMPLATE.replace("$namespace$", name.getNamespace()).replace("$$", name.getPath())));
	}

	public static void generateTrapdoorBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
		pack.addBlockModel(Utils.appendToPath(name, "_bottom"), model -> {
			model.parent(new Identifier("block/template_orientable_trapdoor_bottom"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_open"), model -> {
			model.parent(new Identifier("block/template_orientable_trapdoor_open"));
			model.texture("texture", texture);
		});
		pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
			model.parent(new Identifier("block/template_orientable_trapdoor_top"));
			model.texture("texture", texture);
		});
	}

}