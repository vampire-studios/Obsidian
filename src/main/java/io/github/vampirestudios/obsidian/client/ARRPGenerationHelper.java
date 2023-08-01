package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Map;

import static net.devtech.arrp.json.blockstate.JState.variant;
import static net.devtech.arrp.json.models.JModel.model;
import static net.devtech.arrp.json.models.JModel.textures;

public class ARRPGenerationHelper {

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(Utils.prependToPath(name, "block/")))), name);
    }

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(modelId))), name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        ResourceLocation modelPath = Utils.prependToPath(name, "block/");
        JState hangingModel = JState.state(
                variant().put("hanging=false", JState.model(modelPath)),
                variant().put("hanging=true", JState.model(Utils.prependToPath(modelPath, "_hanging")))
        );
        clientResourcePackBuilder.addBlockState(hangingModel, name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name,
                                                 ResourceLocation modelId, ResourceLocation hangingModel) {
        JState model = JState.state(
                variant().put("hanging=false", JState.model(modelId)),
                variant().put("hanging=true", JState.model(hangingModel))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateLanternBlockModels(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation parent,
                                                  Map<String, ResourceLocation> textures, ResourceLocation parentHanging,
                                                  Map<String, ResourceLocation> texturesHanging) {
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(model.textures(textures1), name);

        JModel hangingModel = JModel.model(parentHanging);
        JTextures textures2 = JModel.textures();
        if (texturesHanging != null)
            texturesHanging.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(hangingModel.textures(textures2), Utils.appendToPath(name, "_hanging"));
    }

    public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        ResourceLocation modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(
                variant().put("axis=y", JState.model(modelPath)),
                variant().put("axis=x", JState.model(modelPath).x(90).y(90)),
                variant().put("axis=z", JState.model(modelPath).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        JState model = JState.state(new JVariant()
                        .put("axis=y", JState.model(modelId))
                        .put("axis=x", JState.model(modelId).x(90).y(90))
                .put("axis=z", JState.model(modelId).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        ResourceLocation modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(new JVariant()
            .put("facing=north", JState.model(modelPath))
            .put("facing=south", JState.model(modelPath).y(180))
            .put("facing=east", JState.model(modelPath).y(90))
            .put("facing=west", JState.model(modelPath).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        JState model = JState.state(new JVariant()
            .put("facing=north", JState.model(modelId))
            .put("facing=south", JState.model(modelId).y(180))
            .put("facing=east", JState.model(modelId).y(90))
            .put("facing=west", JState.model(modelId).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        ResourceLocation modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(
                variant().put("facing=north", JState.model(modelPath).x(90)),
                variant().put("facing=south", JState.model(modelPath).y(180).x(90)),
                variant().put("facing=east", JState.model(modelPath).y(90).x(90)),
                variant().put("facing=west", JState.model(modelPath).y(270).x(90)),
                variant().put("facing=up", JState.model(modelPath)),
                variant().put("facing=down", JState.model(modelPath).x(180))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        JState model = JState.state(new JVariant()
                .put("facing=north", JState.model(modelId).x(90))
                .put("facing=south", JState.model(modelId).y(180).x(90))
                .put("facing=east", JState.model(modelId).y(90).x(90))
                .put("facing=west", JState.model(modelId).y(270).x(90))
                .put("facing=up", JState.model(modelId))
                .put("facing=down", JState.model(modelId).x(180))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
                .var("all", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation texture) {
        clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
                .var("all", Utils.prependToPath(texture, "block/").toString())
        ), name);
    }

    public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
                .var("cross", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation texture) {
        clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
                .var("cross", Utils.prependToPath(texture, "block/").toString())
        ), name);
    }

    public static void generateColumnBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation endTexture, ResourceLocation sideTexture) {
        clientResourcePackBuilder.addModel(model("block/cube_column").textures(textures()
                .var("end", Utils.prependToPath(endTexture, "block/").toString())
                .var("side", Utils.prependToPath(sideTexture, "block/").toString())
        ), name);
    }

    public static void generateModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        JModel itemModel = JModel.model(parent);
        if (textures != null)
            textures.forEach((s, location) -> itemModel.textures(JModel.textures().var(s, location.toString())));
        clientResourcePackBuilder.addModel(itemModel, Utils.prependToPath(name, "block/"));
    }

    public static void generateTopBottomBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation topTexture, ResourceLocation bottomTexture, ResourceLocation sideTexture) {
        clientResourcePackBuilder.addModel(model("block/cube_top_bottom").textures(textures()
                .var("top", Utils.prependToPath(topTexture, "block/").toString())
                .var("bottom", Utils.prependToPath(bottomTexture, "block/").toString())
                .var("side", Utils.prependToPath(sideTexture, "block/").toString())
        ), name);
    }

    public static void generateLadderBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addModel(model("block/ladder").textures(textures()
                .var("texture", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateBlockModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(model.textures(textures1), Utils.prependToPath(name, "block/"));
    }

    public static void generateBlockItemModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addModel(JModel.model(Utils.prependToPath(name, "block/")), Utils.prependToPath(name, "item/"));
    }

    public static void generateBlockItemModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        clientResourcePackBuilder.addModel(JModel.model(Utils.prependToPath(modelId, "block/")), Utils.prependToPath(name, "item/"));
    }

    public static void generateBlockItemModel1(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation modelId) {
        clientResourcePackBuilder.addModel(JModel.model(modelId), Utils.prependToPath(name, "item/"));
    }

    public static void generateItemModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        JModel itemModel = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(itemModel.textures(textures1), Utils.prependToPath(name, "item/"));
    }

    /*public static void generateSimpleItemModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
        clientResourcePackBuilder.addModel(JModel.model("item/generated"), name);
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(new ResourceLocation("item/generated")).texture("layer0", Utils.prependToPath(name, "item/")));
    }

    public static void generateSimpleItemModel(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name, ResourceLocation texture) {
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(new ResourceLocation("item/generated")).texture("layer0", texture));
    }*/

    public static void generateStairsBlockState(RuntimeResourcePack pack, ResourceLocation name) {
//        String JSON = JsonTemplates.STAIRS_BLOCKSTATE
//                .replace("%MOD_ID%", name.getNamespace())
//                .replace("%BLOCK_ID%", name.getPath());
//        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    /*public static void generateStairsBlockModels(RuntimeResourcePack pack, ResourceLocation name, Map<String, ResourceLocation> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inner"), new ModelBuilder()
                .parent(new ResourceLocation("block/inner_stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_outer"), new ModelBuilder()
                .parent(new ResourceLocation("block/outer_stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
        pack.addBlockModel(name, new ModelBuilder()
                .parent(new ResourceLocation("block/stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
    }*/

    public static void generateWallBlockState(RuntimeResourcePack pack, ResourceLocation name) {
//        String JSON = JsonTemplates.WALL_BLOCKSTATE
//                .replace("%MOD_ID%", name.getNamespace())
//                .replace("%BLOCK_ID%", name.getPath());
//        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    /*public static void generateWallBlockModels(RuntimeResourcePack pack, ResourceLocation name, Map<String, ResourceLocation> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inventory"), new ModelBuilder()
                .parent(new ResourceLocation("block/wall_inventory"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_post"), new ModelBuilder()
                .parent(new ResourceLocation("block/template_wall_post"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_side"), new ModelBuilder()
                .parent(new ResourceLocation("block/template_wall_side"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_side_tall"), new ModelBuilder()
                .parent(new ResourceLocation("block/template_wall_side_tall"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
    }*/

    public static void generateSlabBlockState(RuntimeResourcePack pack, ResourceLocation name, ResourceLocation doubleBlockName) {
        JState state = JState.state();
        for (SlabType t : SlabType.values()) {
            JBlockModel var = switch (t) {
                case BOTTOM -> JState.model(Utils.prependToPath(name, "block/"));
                case TOP -> JState.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                case DOUBLE -> JState.model(Utils.prependToPath(doubleBlockName, "block/"));
            };
            state.add(variant().put("type=" + t.name(), var));
        }
        pack.addBlockState(state, name);
    }

    /*public static void generateSlabBlockModels(RuntimeResourcePack pack, ResourceLocation name, ResourceLocation texture) {
        pack.addBlockModel(Utils.appendToPath(name, "_top"), new ModelBuilder()
                .parent(new ResourceLocation("block/slab_top"))
                .texture("particle", texture)
                .texture("side", texture)
                .texture("top", texture)
                .texture("bottom", texture)
        );
        pack.addBlockModel(name, new ModelBuilder()
                .parent(new ResourceLocation("block/slab"))
                .texture("particle", texture)
                .texture("side", texture)
                .texture("top", texture)
                .texture("bottom", texture)
        );
    }*/

    /*public static void generateSlabBlockModels(RuntimeResourcePack pack, ResourceLocation name, Map<String, ResourceLocation> textures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/slab_top"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_top"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/slab"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        pack.addBlockModel(name, modelBuilder);
    }*/

    public static void generateFenceBlockState(RuntimeResourcePack pack, ResourceLocation name) {
//        BlockStateBuilder state = new BlockStateBuilder().multipartCase(new BlockStateBuilder.Case().apply(new BlockStateBuilder.Variant().model(
//                Utils.appendAndPrependToPath(name, "block/", "_post")
//        )));
//        for (Direction d : Direction.values()) {
//            if (d != Direction.UP && d != Direction.DOWN) {
//                state.multipartCase(new BlockStateBuilder.Case()
//                        .when(d.asString(), "true")
//                        .apply(new BlockStateBuilder.Variant()
//                                .model(Utils.appendAndPrependToPath(name, "block/", "_side"))
//                                .uvlock(true)
//                                .y(switch (d) {
//                                    case EAST -> 90;
//                                    case WEST -> 270;
//                                    case SOUTH -> 180;
//                                    default -> throw new IllegalStateException("Unexpected value: " + d);
//                                })
//                        )
//                );
//            }
//        }
//        pack.addBlockState(name, state);
    }

    /*public static void generateFenceBlockModels(RuntimeResourcePack pack, ResourceLocation name, Map<String, ResourceLocation> textures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/fence_inventory"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_inventory"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/fence_post"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_post"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/fence_side"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_side"), modelBuilder);
    }

    public static void generateFenceGateBlockState(RuntimeResourcePack pack, ResourceLocation name) {
        BlockStateBuilder state = new BlockStateBuilder();
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP && direction != Direction.DOWN) {
                BlockStateBuilder.Variant var = new BlockStateBuilder.Variant()
                        .model(Utils.prependToPath(name, "block/"))
                        .uvlock(true)
                        .y(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=false,open=false", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_wall"))
                        .uvlock(true)
                        .y(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=true,open=false", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_open"))
                        .uvlock(true)
                        .y(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=false,open=true", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_wall_open"))
                        .uvlock(true)
                        .y(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=true,open=true", var);
            }
        }
        pack.addBlockState(name, state);
    }

    public static void generateFenceGateBlockModels(RuntimeResourcePack pack, ResourceLocation name, ResourceLocation texture) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/template_fence_gate_open"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_open"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/template_fence_gate_wall"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_wall"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/template_fence_gate_wall_open"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_wall_open"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new ResourceLocation("block/template_fence_gate"))
                .texture("texture", texture);
        pack.addBlockModel(name, modelBuilder);
    }*/

    public static void generateDoorBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
//        String JSON = JsonTemplates.DOOR_BLOCKSTATE
//                .replace("%MOD_ID%", name.getNamespace())
//                .replace("%BLOCK_ID%", name.getPath());
//        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    /*public static void generateDoorBlockModels(RuntimeResourcePack pack, ResourceLocation name,
                                               ResourceLocation topParent, Map<String, ResourceLocation> topTextures,
                                               ResourceLocation topHingeParent, Map<String, ResourceLocation> topHingeTextures,
                                               ResourceLocation bottomParent, Map<String, ResourceLocation> bottomTextures,
                                               ResourceLocation bottomHingeParent, Map<String, ResourceLocation> bottomHingeTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(bottomParent != null ? bottomParent : new ResourceLocation("block/door_bottom"));
        bottomTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_bottom"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(bottomHingeParent != null ? bottomHingeParent : new ResourceLocation("block/door_bottom_rh"));
        bottomHingeTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_bottom_hinge"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(topParent != null ? topParent : new ResourceLocation("block/door_top"));
        topTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_top"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(topHingeParent != null ? topHingeParent : new ResourceLocation("block/door_top_rh"));
        topHingeTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_top_hinge"), modelBuilder);
    }*/

    public static void generateTrapdoorBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation name) {
//        String JSON = JsonTemplates.TRAPDOOR_BLOCKSTATE
//                .replace("%MOD_ID%", name.getNamespace())
//                .replace("%BLOCK_ID%", name.getPath());
//        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    /*public static void generateTrapdoorBlockModels(RuntimeResourcePack pack, ResourceLocation name,
                                                   ResourceLocation topParent, Map<String, ResourceLocation> topTextures,
                                                   ResourceLocation openParent, Map<String, ResourceLocation> openTextures,
                                                   ResourceLocation bottomParent, Map<String, ResourceLocation> bottomTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(bottomParent);
        bottomTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_bottom"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(openParent);
        openTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_open"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(topParent);
        topTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_top"), modelBuilder);
    }*/

    public static void generateOnOffHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder,
                                                               ResourceLocation name) {
//        String JSON = JsonTemplates.HORIZONTAL_FACING_ON_OFF_BLOCKSTATE
//                .replace("%MOD_ID%", name.getNamespace())
//                .replace("%BLOCK_ID%", name.getPath());
//        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    /*public static void generateOnOffBlockModels(RuntimeResourcePack pack, ResourceLocation name,
                                                   ResourceLocation onParent, Map<String, ResourceLocation> onTextures,
                                                   ResourceLocation offParent, Map<String, ResourceLocation> offTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(offParent);
        offTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(name, modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(onParent);
        onTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_on"), modelBuilder);
    }*/

    public static void generatePistonBlockState(RuntimeResourcePack clientResourcePackBuilder, ResourceLocation blockId, ResourceLocation model, ResourceLocation stickyModel) {
//        String JSON = JsonTemplates.PISTON_BLOCKSTATE
//                .replace("%MOD_ID%", blockId.getNamespace())
//                .replace("%BLOCK_ID%", blockId.getPath());
//        String STICKY_JSON = JsonTemplates.PISTON_BLOCKSTATE
//                .replace("%MOD_ID%", blockId.getNamespace())
//                .replace("%BLOCK_ID%", blockId.getPath() + "_sticky");
//        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(blockId, "blockstates/", ".json"), new StringResource(JSON));
//        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(Utils.appendToPath(blockId, "_sticky"), "blockstates/", ".json"), new StringResource(STICKY_JSON));
    }

    public static void generatePistonModels(RuntimeResourcePack pack, ResourceLocation blockId,
                                                ResourceLocation normalModel, Map<String, ResourceLocation> normalTextures,
                                                ResourceLocation stickyModel, Map<String, ResourceLocation> stickyTextures) {
//        ModelBuilder modelBuilder = new ModelBuilder()
//                .parent(normalModel);
//        normalTextures.forEach(modelBuilder::texture);
//        pack.addBlockModel(blockId, modelBuilder);
//        modelBuilder = new ModelBuilder()
//                .parent(stickyModel);
//        stickyTextures.forEach(modelBuilder::texture);
//        pack.addBlockModel(Utils.appendToPath(blockId, "_sticky"), modelBuilder);
    }
}
