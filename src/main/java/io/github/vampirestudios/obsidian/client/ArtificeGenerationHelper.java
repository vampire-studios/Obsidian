package io.github.vampirestudios.obsidian.client;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.StringResource;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;

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

    public static void generateLanternBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, blockStateBuilder ->
            blockStateBuilder
                .variant("hanging=false", variant -> variant.model(Utils.prependToPath(name, "block/")))
                .variant("hanging=true", variant -> variant.model(Utils.appendAndPrependToPath(name, "block/", "_hanging"))));
    }

    public static void generateLanternBlockModels(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures, Identifier parentHanging, Map<String, Identifier> texturesHanging) {
        clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
            modelBuilder.parent(parent);
            if (textures != null) textures.forEach(modelBuilder::texture);
        });
        clientResourcePackBuilder.addBlockModel(Utils.appendToPath(name, "_hanging"), modelBuilder -> {
            modelBuilder.parent(parentHanging);
            if (texturesHanging != null) textures.forEach(modelBuilder::texture);
        });
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

    public static void generateHorizontalFacingBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
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

    public static void generateEightDirectionalBlockState(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(name, blockStateBuilder -> {
            blockStateBuilder.variant("rotation=0", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(0));
            blockStateBuilder.variant("rotation=1", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(45));
            blockStateBuilder.variant("rotation=2", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(90));
            blockStateBuilder.variant("rotation=3", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(135));
            blockStateBuilder.variant("rotation=4", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(180));
            blockStateBuilder.variant("rotation=5", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(225));
            blockStateBuilder.variant("rotation=6", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(270));
            blockStateBuilder.variant("rotation=7", variant ->
                    variant.model(Utils.prependToPath(name, "block/")).rotationY(315));
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

    public static void generateBlockModel(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        clientResourcePackBuilder.addBlockModel(name, modelBuilder -> {
            modelBuilder.parent(parent);
            if (textures != null) textures.forEach(modelBuilder::texture);
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
        String JSON = JsonTemplates.STAIRS_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateStairsBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inner"), model -> {
            model.parent(new Identifier("block/inner_stairs"));
            Identifier particle = textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all");
            model.texture("particle", particle);
            model.texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"));
            model.texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
            model.texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        });
        pack.addBlockModel(Utils.appendToPath(name, "_outer"), model -> {
            model.parent(new Identifier("block/outer_stairs"));
            Identifier particle = textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all");
            model.texture("particle", particle);
            model.texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"));
            model.texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
            model.texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        });
        pack.addBlockModel(name, model -> {
            model.parent(new Identifier("block/stairs"));
            Identifier particle = textures.containsKey("particle") ? textures.get("particle") : textures.containsKey("end") ?
                    textures.get("end") : textures.get("all");
            model.texture("particle", particle);
            model.texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"));
            model.texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
            model.texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        });
    }

    public static void generateWallBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name) {
        String JSON = JsonTemplates.WALL_BLOCKSTATE
                .replace("%MOD_ID%", name.getNamespace())
                .replace("%BLOCK_ID%", name.getPath());
        pack.add(Utils.appendAndPrependToPath(name, "blockstates/", ".json"), new StringResource(JSON));
    }

    public static void generateWallBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_inventory"), model -> {
            model.parent(new Identifier("block/wall_inventory"));
            model.texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"));
        });
        pack.addBlockModel(Utils.appendToPath(name, "_post"), model -> {
            model.parent(new Identifier("block/template_wall_post"));
            model.texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"));
        });
        pack.addBlockModel(Utils.appendToPath(name, "_side"), model -> {
            model.parent(new Identifier("block/template_wall_side"));
            model.texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"));
        });
        pack.addBlockModel(Utils.appendToPath(name, "_side_tall"), model -> {
            model.parent(new Identifier("block/template_wall_side_tall"));
            model.texture("wall", textures.containsKey("wall") ? textures.get("wall") : textures.containsKey("side") ? textures.get("side") : textures.get("all"));
        });
    }

    public static void generateSlabBlockState(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Identifier doubleBlockName) {
        pack.addBlockState(name, state -> {
            for (SlabType t : SlabType.values()) {
                state.variant("type=" + t.asString(), var -> {
                    switch (t) {
                        case BOTTOM -> var.model(Utils.prependToPath(name, "block/"));
                        case TOP -> var.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                        case DOUBLE -> var.model(Utils.prependToPath(doubleBlockName, "block/"));
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

    public static void generateSlabBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, Identifier name, Map<String, Identifier> textures) {
        pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
            model.parent(new Identifier("block/slab_top"));
//            model.texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.get("all"));
            model.texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"));
            model.texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
            model.texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
        });
        pack.addBlockModel(name, model -> {
            model.parent(new Identifier("block/slab"));
//            model.texture("particle", textures.containsKey("particle") ? textures.get("particle") : textures.get("all"));
            model.texture("side", textures.containsKey("side") ? textures.get("side") : textures.get("all"));
            model.texture("top", textures.containsKey("top") ? textures.get("top") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
            model.texture("bottom", textures.containsKey("bottom") ? textures.get("bottom") : textures.containsKey("end") ? textures.get("end") : textures.get("all"));
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
                                case EAST -> var.rotationY(90);
                                case WEST -> var.rotationY(270);
                                case SOUTH -> var.rotationY(180);
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
            for (Direction direction : Direction.values()) {
                if (direction != Direction.UP && direction != Direction.DOWN) {
                    state.variant("facing=" + direction.asString() + ",in_wall=false,open=false", var -> {
                        var.model(Utils.prependToPath(name, "block/"));
                        var.uvlock(true);
                        switch (direction) {
                            case NORTH -> var.rotationY(180);
                            case WEST -> var.rotationY(90);
                            case EAST -> var.rotationY(270);
                        }
                    });
                    state.variant("facing=" + direction.asString() + ",in_wall=true,open=false", var -> {
                        var.model(Utils.appendAndPrependToPath(name, "block/", "_wall"));
                        var.uvlock(true);
                        switch (direction) {
                            case NORTH -> var.rotationY(180);
                            case WEST -> var.rotationY(90);
                            case EAST -> var.rotationY(270);
                        }
                    });
                    state.variant("facing=" + direction.asString() + ",in_wall=false,open=true", var -> {
                        var.model(Utils.appendAndPrependToPath(name, "block/", "_open"));
                        var.uvlock(true);
                        switch (direction) {
                            case NORTH -> var.rotationY(180);
                            case WEST -> var.rotationY(90);
                            case EAST -> var.rotationY(270);
                        }
                    });
                    state.variant("facing=" + direction.asString() + ",in_wall=true,open=true", var -> {
                        var.model(Utils.appendAndPrependToPath(name, "block/", "_wall_open"));
                        var.uvlock(true);
                        switch (direction) {
                            case NORTH -> var.rotationY(180);
                            case WEST -> var.rotationY(90);
                            case EAST -> var.rotationY(270);
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
        pack.addBlockModel(Utils.appendToPath(name, "_bottom"), model -> {
            model.parent(bottomParent != null ? bottomParent : new Identifier("block/door_bottom"));
            bottomTextures.forEach(model::texture);
        });
        pack.addBlockModel(Utils.appendToPath(name, "_bottom_hinge"), model -> {
            model.parent(bottomHingeParent != null ? bottomHingeParent : new Identifier("block/door_bottom_rh"));
            bottomHingeTextures.forEach(model::texture);
        });
        pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
            model.parent(topParent != null ? topParent : new Identifier("block/door_top"));
            topTextures.forEach(model::texture);
        });
        pack.addBlockModel(Utils.appendToPath(name, "_top_hinge"), model -> {
            model.parent(topHingeParent != null ? topHingeParent : new Identifier("block/door_top_rh"));
            topHingeTextures.forEach(model::texture);
        });
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
        pack.addBlockModel(Utils.appendToPath(name, "_bottom"), model -> {
            model.parent(bottomParent);
            bottomTextures.forEach(model::texture);
        });
        pack.addBlockModel(Utils.appendToPath(name, "_open"), model -> {
            model.parent(openParent);
            openTextures.forEach(model::texture);
        });
        pack.addBlockModel(Utils.appendToPath(name, "_top"), model -> {
            model.parent(topParent);
            topTextures.forEach(model::texture);
        });
    }

}