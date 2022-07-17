package io.github.vampirestudios.obsidian.client;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class EightDirectionalBakedModel implements FabricBakedModel, BakedModel, UnbakedModel {

    private final Identifier modelId;
    private BakedModel model;
    private ModelTransformation transformation;
    private Mesh mesh;

    public EightDirectionalBakedModel(Identifier modelId) {
        this.modelId = modelId;
    }

    //////////////////
    // UnbakedModel //
    //////////////////

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.singletonList(modelId);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(modelId);
        this.transformation = defaultBlockModel.getTransformations();
        this.model = defaultBlockModel.bake(loader, textureGetter, rotationContainer, modelId);
        return this;
    }

    ////////////////
    // BakedModel //
    ////////////////

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, RandomGenerator random) { return null; }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return this.model.getParticleSprite();
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public ModelTransformation getTransformation() { return this.transformation; }

    @Override
    public ModelOverrideList getOverrides() { return null; }

    //////////////////////
    // FabricBakedModel //
    //////////////////////

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<RandomGenerator> supplier, RenderContext renderContext) {
        renderContext.fallbackConsumer().accept(this.model);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomGenerator> supplier, RenderContext renderContext) {

    }

}
