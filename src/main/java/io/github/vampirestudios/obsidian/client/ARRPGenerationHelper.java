package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.assets.blockstates.BlockState;
import net.vampirestudios.arrp.assets.blockstates.Variant;
import net.vampirestudios.arrp.assets.models.Model;
import net.vampirestudios.arrp.assets.models.Textures;
import net.vampirestudios.arrp.json.blockstate.JBlockModel;
import net.vampirestudios.arrp.json.iteminfo.JItemInfo;
import net.vampirestudios.arrp.json.iteminfo.model.ModelBasic;
import net.vampirestudios.arrp.json.iteminfo.tint.JTint;
import net.vampirestudios.arrp.json.loot.JCondition;
import net.vampirestudios.arrp.json.models.JOverride;
import net.vampirestudios.arrp.json.models.Textures;

import java.util.Map;

import static net.vampirestudios.arrp.assets.blockstates.BlockState.variant;
import static net.vampirestudios.arrp.assets.models.Model.model;
import static net.vampirestudios.arrp.assets.models.Model.textures;

public class ARRPGenerationHelper {

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(BlockState.state(variant(BlockState.model(Utils.prependToPath(name, "block/")))), name);
    }

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(BlockState.state(variant(BlockState.model(modelId))), name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        BlockState hangingModel = BlockState.state(
                variant().put("hanging=false", BlockState.model(modelPath)),
                variant().put("hanging=true", BlockState.model(Utils.prependToPath(modelPath, "_hanging")))
        );
        clientResourcePackBuilder.addBlockState(hangingModel, name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name,
                                                 Identifier modelId, Identifier hangingModel) {
        BlockState model = BlockState.state(
                variant().put("hanging=false", BlockState.model(modelId)),
                variant().put("hanging=true", BlockState.model(hangingModel))
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
        BlockState model = BlockState.state(new Variant()
                        .put("axis=y", BlockState.model(modelId))
                        .put("axis=x", BlockState.model(modelId).x(90).y(90))
                .put("axis=z", BlockState.model(modelId).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        generateHorizontalFacingBlockState(clientResourcePackBuilder, name, modelId, 0);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId, int yOffset) {
        BlockState model = BlockState.state(new Variant()
            .put("facing=north", BlockState.model(modelId).y(Math.floorMod(yOffset,       360)))
            .put("facing=south", BlockState.model(modelId).y(Math.floorMod(180 + yOffset, 360)))
            .put("facing=east",  BlockState.model(modelId).y(Math.floorMod(90  + yOffset, 360)))
            .put("facing=west",  BlockState.model(modelId).y(Math.floorMod(270 + yOffset, 360)))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        generateFacingBlockState(clientResourcePackBuilder, name, modelId, 0);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId, int yOffset) {
        BlockState model = BlockState.state(new Variant()
                .put("facing=north", BlockState.model(modelId).y(Math.floorMod(yOffset,       360)))
                .put("facing=south", BlockState.model(modelId).y(Math.floorMod(180 + yOffset, 360)))
                .put("facing=east",  BlockState.model(modelId).y(Math.floorMod(90  + yOffset, 360)))
                .put("facing=west",  BlockState.model(modelId).y(Math.floorMod(270 + yOffset, 360)))
                .put("facing=up",    BlockState.model(modelId).y(Math.floorMod(yOffset,       360)))
                .put("facing=down",  BlockState.model(modelId).x(180).y(Math.floorMod(yOffset, 360)))
        );
        clientResourcePackBuilder.addBlockState(model, name);
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
        if (textures != null) textures.forEach((k,v) -> tex.var(k, v.toString()));
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
        Model itemModel = model(parent);

        if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
            if (item.pack.blocking_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("blocking", 1), item.pack.blocking_model.toString()));
            }
        } else if (item.getItemType().equals(NexoItem.ItemType.BOW)) {
            if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.65f), item.pack.pulling_models.get(0).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.9f), item.pack.pulling_models.get(1).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 1.0f), item.pack.pulling_models.get(2).toString()));
            }
        } else if(item.getItemType().equals(NexoItem.ItemType.CROSSBOW)) {
            // Assuming crossbow has three stages similar to the bow for pulling, plus a loaded and a firing state.
            if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.58f), item.pack.pulling_models.get(0).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.79f), item.pack.pulling_models.get(1).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 1.0f), item.pack.pulling_models.get(2).toString()));
            }
            // Handle charged state with normal arrow
            if (item.pack.charged_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("charged", 1).parameter("firework", 0), item.pack.charged_model.toString()));
            }
            // Handle charged state with firework
            if (item.pack.firework_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("charged", 1).parameter("firework", 1), item.pack.firework_model.toString()));
            }
        }

        Textures textures1 = textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(itemModel.textures(textures1), Utils.prependToPath(name, "item/"));
    }

    public static void generateSimpleItemModel(NexoItem item, RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent) {
        Model itemModel = model(parent);

        if (item.getItemType().equals(NexoItem.ItemType.SHIELD)) {
            if (item.pack.blocking_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("blocking", 1), item.pack.blocking_model.toString()));
            }
        } else if (item.getItemType().equals(NexoItem.ItemType.BOW)) {
            if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.65f), item.pack.pulling_models.get(0).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.9f), item.pack.pulling_models.get(1).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 1.0f), item.pack.pulling_models.get(2).toString()));
            }
        } else if(item.getItemType().equals(NexoItem.ItemType.CROSSBOW)) {
            // Assuming crossbow has three stages similar to the bow for pulling, plus a loaded and a firing state.
            if (item.pack.pulling_models != null && item.pack.pulling_models.size() >= 3) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.58f), item.pack.pulling_models.get(0).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 0.79f), item.pack.pulling_models.get(1).toString()));
                itemModel.addOverride(new JOverride(new JCondition().parameter("pulling", 1).parameter("pull", 1.0f), item.pack.pulling_models.get(2).toString()));
            }
            // Handle charged state with normal arrow
            if (item.pack.charged_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("charged", 1).parameter("firework", 0), item.pack.charged_model.toString()));
            }
            // Handle charged state with firework
            if (item.pack.firework_model != null) {
                itemModel.addOverride(new JOverride(new JCondition().parameter("charged", 1).parameter("firework", 1), item.pack.firework_model.toString()));
            }
        }
        clientResourcePackBuilder.addModel(itemModel, Utils.prependToPath(name, "item/"));
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
        BlockState state = BlockState.state(new Variant()
                .put("facing=south,powered=false", BlockState.model(unpoweredModelId))
                .put("facing=west,powered=false", BlockState.model(unpoweredModelId).y(90))
                .put("facing=north,powered=false", BlockState.model(unpoweredModelId).y(180))
                .put("facing=east,powered=false", BlockState.model(unpoweredModelId).y(270))
                .put("facing=south,powered=true", BlockState.model(poweredModelId))
                .put("facing=west,powered=true", BlockState.model(poweredModelId).y(90))
                .put("facing=north,powered=true", BlockState.model(poweredModelId).y(180))
                .put("facing=east,powered=true", BlockState.model(poweredModelId).y(270))
        );
        pack.addBlockState(state, name);
    }

    public static void generateSlabBlockState(RuntimeResourcePack pack, Identifier name, Identifier doubleBlockName) {
        BlockState state = BlockState.state();
        for (SlabType t : SlabType.values()) {
            JBlockModel var = switch (t) {
                case BOTTOM -> BlockState.model(Utils.prependToPath(name, "block/"));
                case TOP -> BlockState.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                case DOUBLE -> BlockState.model(Utils.prependToPath(doubleBlockName, "block/"));
            };
            state.add(variant().put("type=" + t.name(), var));
        }
        pack.addBlockState(state, name);
    }

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Identifier name) {
        JItemInfo itemInfo = new JItemInfo()
                .model(ModelBasic.model(Utils.prependToPath(name, "item/").toString()));
        pack.addItemModelInfo(itemInfo, name);
    }

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Block block, Identifier name, Identifier directModelId) {
        JItemInfo itemInfo = new JItemInfo();
        var itemModel = ModelBasic.model(directModelId.toString());

        if (block.additional_information != null && block.additional_information.dyable) {
            itemModel.tint(JTint.dye(block.additional_information.defaultColor));
        }

        itemInfo.model(itemModel);
        pack.addItemModelInfo(itemInfo, name);
    }

}
