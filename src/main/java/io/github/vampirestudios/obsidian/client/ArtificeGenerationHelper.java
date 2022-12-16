/*
package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.artifice.api.resource.StringResource;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class ArtificeGenerationHelper {

    public static void generateBasicBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder().variant("",
                new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/"))
        ));
    }

    public static void generateBasicBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder().variant("",
                new BlockStateBuilder.Variant().model(modelId)
        ));
    }

    public static void generateLanternBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("hanging=false", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")))
                .variant("hanging=true", new BlockStateBuilder.Variant().model(Utils.appendAndPrependToPath(name, "block/", "_hanging")))
        );
    }

    public static void generateLanternBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name,
                                                 Identifier model, Identifier hangingModel) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("hanging=false", new BlockStateBuilder.Variant().model(model))
                .variant("hanging=true", new BlockStateBuilder.Variant().model(hangingModel))
        );
    }

    public static void generateLanternBlockModels(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures, Identifier parentHanging, Map<String, Identifier> texturesHanging) {
        ModelBuilder modelBuilder = new ModelBuilder().parent(parent);
        if (textures != null) textures.forEach(modelBuilder::texture);
        clientResourcePackBuilder.addBlockModel(name, modelBuilder);
        modelBuilder = new ModelBuilder().parent(parentHanging);
        if (texturesHanging != null) texturesHanging.forEach(modelBuilder::texture);
        clientResourcePackBuilder.addBlockModel(Utils.appendToPath(name, "_hanging"), modelBuilder);
    }

    public static void generatePillarBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("axis=y", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")))
                .variant("axis=x", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationX(90).rotationY(90))
                .variant("axis=z", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationX(90))
        );
    }

    public static void generatePillarBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("axis=y", new BlockStateBuilder.Variant().model(modelId))
                .variant("axis=x", new BlockStateBuilder.Variant().model(modelId).rotationX(90).rotationY(90))
                .variant("axis=z", new BlockStateBuilder.Variant().model(modelId).rotationX(90))
        );
    }

    public static void generateHorizontalFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("facing=north", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")))
                .variant("facing=south", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(180))
                .variant("facing=east", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(90))
                .variant("facing=west", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(270))
        );
    }

    public static void generateHorizontalFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("facing=north", new BlockStateBuilder.Variant().model(modelId))
                .variant("facing=south", new BlockStateBuilder.Variant().model(modelId).rotationY(180))
                .variant("facing=east", new BlockStateBuilder.Variant().model(modelId).rotationY(90))
                .variant("facing=west", new BlockStateBuilder.Variant().model(modelId).rotationY(270))
        );
    }

    public static void generateFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("facing=north", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationX(90))
                .variant("facing=south", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(180).rotationX(90))
                .variant("facing=east", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(90).rotationX(90))
                .variant("facing=west", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationY(270).rotationX(90))
                .variant("facing=up", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")))
                .variant("facing=down", new BlockStateBuilder.Variant().model(Utils.prependToPath(name, "block/")).rotationX(180))
        );
    }

    public static void generateFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(name, new BlockStateBuilder()
                .variant("facing=north", new BlockStateBuilder.Variant().model(modelId).rotationX(90))
                .variant("facing=south", new BlockStateBuilder.Variant().model(modelId).rotationY(180).rotationX(90))
                .variant("facing=east", new BlockStateBuilder.Variant().model(modelId).rotationY(90).rotationX(90))
                .variant("facing=west", new BlockStateBuilder.Variant().model(modelId).rotationY(270).rotationX(90))
                .variant("facing=up", new BlockStateBuilder.Variant().model(modelId))
                .variant("facing=down", new BlockStateBuilder.Variant().model(modelId).rotationX(180))
        );
    }

    public static void generateAllBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cube_all"))
                .texture("all", Utils.prependToPath(name, "block/"))
        );
    }

    public static void generateAllBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cube_all"))
                .texture("all", Utils.prependToPath(texture, "block/"))
        );
    }

    public static void generateCrossBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cross"))
                .texture("cross", Utils.prependToPath(name, "block/"))
        );
    }

    public static void generateCrossBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cross"))
                .texture("cross", Utils.prependToPath(texture, "block/"))
        );
    }

    public static void generateColumnBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier endTexture, Identifier sideTexture) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cube_column"))
                .texture("end", Utils.prependToPath(endTexture, "block/"))
                .texture("side", Utils.prependToPath(sideTexture, "block/"))
        );
    }

    public static void generateTopBottomBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
        clientResourcePackBuilder.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/cube_top_bottom"))
                .texture("top", topTexture)
                .texture("bottom", bottomTexture)
                .texture("side", sideTexture)
        );
    }

    public static void generateLadderBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/ladder"))
                .texture("texture", Utils.prependToPath(name, "block/"));
        clientResourcePackBuilder.addBlockModel(name, modelBuilder);
    }

    public static void generateBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        ModelBuilder modelBuilder = new ModelBuilder().parent(parent);
        if (textures != null) textures.forEach(modelBuilder::texture);
        clientResourcePackBuilder.addBlockModel(name, modelBuilder);
    }

    public static void generateBlockItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(Utils.prependToPath(name, "block/")));
    }

    public static void generateBlockItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(Utils.prependToPath(modelId, "block/")));
    }

    public static void generateSimpleItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(new Identifier("item/generated")).texture("layer0", Utils.prependToPath(name, "item/")));
    }

    public static void generateSimpleItemModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier texture) {
        clientResourcePackBuilder.addItemModel(name, new ModelBuilder().parent(new Identifier("item/generated")).texture("layer0", texture));
    }

    public static void generateStairsBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
        String JSON = JsonTemplates.STAIRS_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateStairsBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inner"), new ModelBuilder()
                .parent(new Identifier("block/inner_stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_outer"), new ModelBuilder()
                .parent(new Identifier("block/outer_stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
        pack.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/stairs"))
                .texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
        );
    }

    public static void generateWallBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
        String JSON = JsonTemplates.WALL_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateWallBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inventory"), new ModelBuilder()
                .parent(new Identifier("block/wall_inventory"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_post"), new ModelBuilder()
                .parent(new Identifier("block/template_wall_post"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_side"), new ModelBuilder()
                .parent(new Identifier("block/template_wall_side"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
        pack.addBlockModel(Utils.appendToPath(name, "_side_tall"), new ModelBuilder()
                .parent(new Identifier("block/template_wall_side_tall"))
                .texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"))
        );
    }

    public static void generateSlabBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier doubleBlockName) {
        BlockStateBuilder state = new BlockStateBuilder();
        for (SlabType t : SlabType.values()) {
            BlockStateBuilder.Variant var = new BlockStateBuilder.Variant();
            switch (t) {
                case BOTTOM -> var.model(Utils.prependToPath(name, "block/"));
                case TOP -> var.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                case DOUBLE -> var.model(Utils.prependToPath(doubleBlockName, "block/"));
            }
            state.variant("type=" + t.asString(), var);
        }
        pack.addBlockState(name, state);
    }

    public static void generateSlabBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
        pack.addBlockModel(Utils.appendToPath(name, "_top"), new ModelBuilder()
                .parent(new Identifier("block/slab_top"))
                .texture("particle", texture)
                .texture("side", texture)
                .texture("top", texture)
                .texture("bottom", texture)
        );
        pack.addBlockModel(name, new ModelBuilder()
                .parent(new Identifier("block/slab"))
                .texture("particle", texture)
                .texture("side", texture)
                .texture("top", texture)
                .texture("bottom", texture)
        );
    }

    public static void generateSlabBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/slab_top"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_top"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/slab"))
                .texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"))
                .texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"))
                .texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        pack.addBlockModel(name, modelBuilder);
    }

    public static void generateFenceBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
        BlockStateBuilder state = new BlockStateBuilder().multipartCase(new BlockStateBuilder.Case().apply(new BlockStateBuilder.Variant().model(
                Utils.appendAndPrependToPath(name, "block/", "_post")
        )));
        for (Direction d : Direction.values()) {
            if (d != Direction.UP && d != Direction.DOWN) {
                state.multipartCase(new BlockStateBuilder.Case()
                        .when(d.asString(), "true")
                        .apply(new BlockStateBuilder.Variant()
                                .model(Utils.appendAndPrependToPath(name, "block/", "_side"))
                                .uvlock(true)
                                .rotationY(switch (d) {
                                    case EAST -> 90;
                                    case WEST -> 270;
                                    case SOUTH -> 180;
                                    default -> throw new IllegalStateException("Unexpected value: " + d);
                                })
                        )
                );
            }
        }
        pack.addBlockState(name, state);
    }

    public static void generateFenceBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/fence_inventory"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_inventory"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/fence_post"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_post"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/fence_side"))
                .texture("texture", textures.containsKey("texture") ? textures.get("texture") : textures.get("all"));
        pack.addBlockModel(Utils.appendToPath(name, "_side"), modelBuilder);
    }

    public static void generateFenceGateBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
        BlockStateBuilder state = new BlockStateBuilder();
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP && direction != Direction.DOWN) {
                BlockStateBuilder.Variant var = new BlockStateBuilder.Variant()
                        .model(Utils.prependToPath(name, "block/"))
                        .uvlock(true)
                        .rotationY(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=false,open=false", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_wall"))
                        .uvlock(true)
                        .rotationY(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=true,open=false", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_open"))
                        .uvlock(true)
                        .rotationY(switch (direction) {
                            case NORTH -> 180;
                            case WEST -> 90;
                            case EAST -> 270;
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        });
                state.variant("facing=" + direction.asString() + ",in_wall=false,open=true", var);
                var = new BlockStateBuilder.Variant()
                        .model(Utils.appendAndPrependToPath(name, "block/", "_wall_open"))
                        .uvlock(true)
                        .rotationY(switch (direction) {
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

    public static void generateFenceGateBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier texture) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/template_fence_gate_open"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_open"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/template_fence_gate_wall"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_wall"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/template_fence_gate_wall_open"))
                .texture("texture", texture);
        pack.addBlockModel(Utils.appendToPath(name, "_wall_open"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(new Identifier("block/template_fence_gate"))
                .texture("texture", texture);
        pack.addBlockModel(name, modelBuilder);
    }

    public static void generateDoorBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        String JSON = JsonTemplates.DOOR_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateDoorBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name,
                                               Identifier topParent, Map<String, Identifier> topTextures,
                                               Identifier topHingeParent, Map<String, Identifier> topHingeTextures,
                                               Identifier bottomParent, Map<String, Identifier> bottomTextures,
                                               Identifier bottomHingeParent, Map<String, Identifier> bottomHingeTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(bottomParent != null ? bottomParent : new Identifier("block/door_bottom"));
        bottomTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_bottom"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(bottomHingeParent != null ? bottomHingeParent : new Identifier("block/door_bottom_rh"));
        bottomHingeTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_bottom_hinge"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(topParent != null ? topParent : new Identifier("block/door_top"));
        topTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_top"), modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(topHingeParent != null ? topHingeParent : new Identifier("block/door_top_rh"));
        topHingeTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_top_hinge"), modelBuilder);
    }

    public static void generateTrapdoorBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        String JSON = JsonTemplates.TRAPDOOR_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateTrapdoorBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name,
                                                   Identifier topParent, Map<String, Identifier> topTextures,
                                                   Identifier openParent, Map<String, Identifier> openTextures,
                                                   Identifier bottomParent, Map<String, Identifier> bottomTextures) {
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
    }

    public static void generateOnOffHorizontalFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder,
                                                               Identifier name) {
        String JSON = JsonTemplates.HORIZONTAL_FACING_ON_OFF_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateOnOffBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name,
                                                   Identifier onParent, Map<String, Identifier> onTextures,
                                                   Identifier offParent, Map<String, Identifier> offTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(offParent);
        offTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(name, modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(onParent);
        onTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(name, "_on"), modelBuilder);
    }

    public static void generatePistonBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier blockId, Identifier model, Identifier stickyModel) {
        String JSON = JsonTemplates.PISTON_BLOCKSTATE
                .replace("%MOD_ID%", blockId.getNamespace())
                .replace("%BLOCK_ID%", blockId.getPath());
        String STICKY_JSON = JsonTemplates.PISTON_BLOCKSTATE
                .replace("%MOD_ID%", blockId.getNamespace())
                .replace("%BLOCK_ID%", blockId.getPath() + "_sticky");
        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(blockId, "blockstates/", ".json"), new StringResource(JSON));
        clientResourcePackBuilder.add(Utils.appendAndPrependToPath(Utils.appendToPath(blockId, "_sticky"), "blockstates/", ".json"), new StringResource(STICKY_JSON));
    }

    public static void generatePistonModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier blockId,
                                                Identifier normalModel, Map<String, Identifier> normalTextures,
                                                Identifier stickyModel, Map<String, Identifier> stickyTextures) {
        ModelBuilder modelBuilder = new ModelBuilder()
                .parent(normalModel);
        normalTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(blockId, modelBuilder);
        modelBuilder = new ModelBuilder()
                .parent(stickyModel);
        stickyTextures.forEach(modelBuilder::texture);
        pack.addBlockModel(Utils.appendToPath(blockId, "_sticky"), modelBuilder);
    }
}*/
