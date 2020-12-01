package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class GeometryModel {

    private final Function<Identifier, RenderLayer> layerFactory;
    private final Identifier data;

    public GeometryModel(Function<Identifier, RenderLayer> layerFactory, Identifier modelData) {
        this.layerFactory = layerFactory;
        this.data = modelData;
    }

    public final RenderLayer getLayer(Identifier texture) {
        return this.layerFactory.apply(texture);
    }

    public GeometryData getModelData() {
        return ClientInit.INSTANCE.geometryManager.getModelData(data);
    }

    public void renderModel(MatrixStack stack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        preRender(stack, consumer, light, overlay, red, green, blue, alpha);
        getModelData().getRootBones().forEach(b -> b.render(stack, consumer, light, overlay, red, green, blue, alpha));
        postRender(stack, consumer, light, overlay, red, green, blue, alpha);
    }

    public void preRender(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) { }
    public void postRender(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) { }
}