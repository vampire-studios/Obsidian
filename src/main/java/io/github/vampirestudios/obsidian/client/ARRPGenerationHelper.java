package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.devtech.arrp.json.iteminfo.JItemInfo;
import net.devtech.arrp.json.iteminfo.model.JModelBasic;
import net.devtech.arrp.json.iteminfo.tint.JTint;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JOverride;
import net.devtech.arrp.json.models.JTextures;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Map;

import static net.devtech.arrp.json.blockstate.JState.variant;
import static net.devtech.arrp.json.models.JModel.model;
import static net.devtech.arrp.json.models.JModel.textures;

public class ARRPGenerationHelper {

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(Utils.prependToPath(name, "block/")))), name);
    }

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(modelId))), name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        JState hangingModel = JState.state(
                variant().put("hanging=false", JState.model(modelPath)),
                variant().put("hanging=true", JState.model(Utils.prependToPath(modelPath, "_hanging")))
        );
        clientResourcePackBuilder.addBlockState(hangingModel, name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name,
                                                 Identifier modelId, Identifier hangingModel) {
        JState model = JState.state(
                variant().put("hanging=false", JState.model(modelId)),
                variant().put("hanging=true", JState.model(hangingModel))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateLanternBlockModels(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent,
                                                  Map<String, Identifier> textures, Identifier parentHanging,
                                                  Map<String, Identifier> texturesHanging) {
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(model.textures(textures1), name);

        JModel hangingModel = JModel.model(parentHanging);
        JTextures textures2 = JModel.textures();
        if (texturesHanging != null)
            texturesHanging.forEach((s, location) -> textures2.var(s, location.toString()));
        clientResourcePackBuilder.addModel(hangingModel.textures(textures2), Utils.appendToPath(name, "_hanging"));
    }

    public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        JState model = JState.state(new JVariant()
                        .put("axis=y", JState.model(modelId))
                        .put("axis=x", JState.model(modelId).x(90).y(90))
                .put("axis=z", JState.model(modelId).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        JState model = JState.state(new JVariant()
            .put("facing=north", JState.model(modelId))
            .put("facing=south", JState.model(modelId).y(180))
            .put("facing=east", JState.model(modelId).y(90))
            .put("facing=west", JState.model(modelId).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
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
        JModel itemModel = JModel.model(parent);
        JTextures tex = JModel.textures();
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
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
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

        JModel itemModel = JModel.model(parent);
        JTextures tex = JModel.textures();
        if (textures != null) {
            textures.forEach((k, v) -> tex.var(k, v.toString()));
        }

        // modelId is a FULL model identifier like "<ns>:item/foo_blocking" OR "<ns>:item/foo"
        // pack.addModel expects the *model identifier* (no "item/" prefix added here)
        pack.addModel(itemModel.textures(tex), modelId);
    }

    public static void generateItemModel(NexoItem item, RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        if (name == null || parent == null) return;
        JModel itemModel = JModel.model(parent);

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

        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(itemModel.textures(textures1), Utils.prependToPath(name, "item/"));
    }

    public static void generateSimpleItemModel(NexoItem item, RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent) {
        JModel itemModel = JModel.model(parent);

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

    public static void generateSlabBlockState(RuntimeResourcePack pack, Identifier name, Identifier doubleBlockName) {
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

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Identifier name) {
        JItemInfo itemInfo = new JItemInfo()
                .model(JModelBasic.model(Utils.prependToPath(name, "item/").toString()));
        pack.addItemModelInfo(itemInfo, name);
    }

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Block block, Identifier name, Identifier directModelId) {
        JItemInfo itemInfo = new JItemInfo();
        var itemModel = JModelBasic.model(directModelId.toString());

        if (block.additional_information != null && block.additional_information.dyable) {
            itemModel.tint(JTint.dye(block.additional_information.defaultColor));
        }

        itemInfo.model(itemModel);
        pack.addItemModelInfo(itemInfo, name);
    }

}
