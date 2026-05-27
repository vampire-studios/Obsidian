package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.assets.blockstates.BlockState;
import net.vampirestudios.arrp.assets.blockstates.SimpleModel;
import net.vampirestudios.arrp.assets.blockstates.Variant;
import net.vampirestudios.arrp.assets.item.ItemModel;
import net.vampirestudios.arrp.assets.item.ItemModelDefinition;
import net.vampirestudios.arrp.assets.item.RangeEntry;
import net.vampirestudios.arrp.assets.item.SelectCase;
import net.vampirestudios.arrp.assets.item.models.ModelBasic;
import net.vampirestudios.arrp.assets.item.models.ModelCondition;
import net.vampirestudios.arrp.assets.item.models.ModelRangeDispatch;
import net.vampirestudios.arrp.assets.item.models.ModelSelect;
import net.vampirestudios.arrp.assets.item.properties.PropertyChargeType;
import net.vampirestudios.arrp.assets.item.properties.PropertyCrossbowPull;
import net.vampirestudios.arrp.assets.item.properties.PropertyUseDuration;
import net.vampirestudios.arrp.assets.item.properties.PropertyUsingItem;
import net.vampirestudios.arrp.assets.item.tints.Tint;
import net.vampirestudios.arrp.assets.models.Model;
import net.vampirestudios.arrp.assets.models.Textures;

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

        var tex = textures();
        if (textures != null) textures.forEach((k, v) -> tex.var(k, v.toString()));
        Identifier modelId = Utils.prependToPath(name, "item/");
        clientResourcePackBuilder.addModel(model(parent).textures(tex), modelId);

        ItemModelDefinition itemInfo = new ItemModelDefinition();
        var fallbackModel = net.vampirestudios.arrp.assets.item.models.ModelBasic.model(modelId);
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
                chargeType.addCase(SelectCase.of(new String[]{"arrow"}, ItemModel.model(item.pack.charged_model)));
            }
            if (item.pack.firework_model != null) {
                chargeType.addCase(SelectCase.of(new String[]{"rocket"}, ItemModel.model(item.pack.firework_model)));
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
        var fallbackModel = net.vampirestudios.arrp.assets.item.models.ModelBasic.model(modelId);
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
                chargeType.addCase(SelectCase.of(new String[]{"arrow"}, ItemModel.model(item.pack.charged_model)));
            }
            if (item.pack.firework_model != null) {
                chargeType.addCase(SelectCase.of(new String[]{"rocket"}, ItemModel.model(item.pack.firework_model)));
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
            SimpleModel var = switch (t) {
                case BOTTOM -> BlockState.model(Utils.prependToPath(name, "block/"));
                case TOP -> BlockState.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                case DOUBLE -> BlockState.model(Utils.prependToPath(doubleBlockName, "block/"));
            };
            state.add(variant().put("type=" + t.name(), var));
        }
        pack.addBlockState(state, name);
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
