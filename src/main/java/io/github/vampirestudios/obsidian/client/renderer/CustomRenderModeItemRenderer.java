/*
package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CustomRenderModeItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, IdentifiableResourceReloadListener {
    private final Identifier id;
    private final Identifier normalModel;
    private ItemRenderer itemRenderer;
    private BakedModel bakedModel;

    public CustomRenderModeItemRenderer(Identifier itemName, Identifier normalModel) {
        this.id = Identifier.fromNamespaceAndPath(itemName.getNamespace(), itemName.getPath() + "_renderer");
        this.normalModel = normalModel;
    }

    @Override
    public Identifier getFabricId() {
        return this.id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
        return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
            final Minecraft client = Minecraft.getInstance();
            this.itemRenderer = client.getItemRenderer();
            this.bakedModel = client.getModelManager().getModel(new ModelIdentifier(normalModel, "inventory"));
        }, applyExecutor);
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
//        if (stack.getItem() instanceof IRenderModeAware renderModeAware)
//            itemRenderer.renderStatic(stack, mode, false, matrices, vertexConsumers, light, overlay, renderModeAware.getModel(stack, mode, this.bakedModel));
        matrices.popPose();
    }
}*/
