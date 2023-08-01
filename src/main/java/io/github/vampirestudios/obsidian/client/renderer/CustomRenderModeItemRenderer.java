package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.vampirestudios.obsidian.api.IRenderModeAware;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CustomRenderModeItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, IdentifiableResourceReloadListener {
    private final ResourceLocation id;
    private final ResourceLocation normalModel;
    private final List<ItemInformation.RenderModeModel> renderModeModels;
    private ItemRenderer itemRenderer;
    private BakedModel bakedModel;

    public CustomRenderModeItemRenderer(ResourceLocation itemName, List<ItemInformation.RenderModeModel> renderModeModels, ResourceLocation normalModel) {
        this.id = new ResourceLocation(itemName.getNamespace(), itemName.getPath() + "_renderer");
        this.normalModel = normalModel;
        this.renderModeModels = renderModeModels;
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
            final Minecraft client = Minecraft.getInstance();
            this.itemRenderer = client.getItemRenderer();
            this.bakedModel = client.getModelManager().getModel(new ModelResourceLocation(normalModel, "inventory"));
        }, applyExecutor);
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.popPose();
        matrices.pushPose();

        if (stack.getItem() instanceof IRenderModeAware renderModeAware) {
            itemRenderer.render(stack, mode, false, matrices, vertexConsumers, light, overlay, renderModeAware.getModel(stack, mode, this.bakedModel));
        }
        /*for (ItemInformation.RenderModeModel renderModeModel : renderModeModels) {
            List<ItemDisplayContext> displayContexts = Stream.of(renderModeModel.modes)
                    .map(s -> ItemDisplayContext.valueOf(s.toUpperCase(Locale.ROOT)))
                    .toList();
            if (displayContexts.contains(mode)) {
                final Minecraft client = Minecraft.getInstance();
                BakedModel model =  client.getModelManager().getModel(new ModelResourceLocation(renderModeModel.model, "inventory"));
                itemRenderer.render(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
            } else {
                itemRenderer.render(stack, mode, false, matrices, vertexConsumers, light, overlay, this.bakedModel);
            }
        }*/
    }
}