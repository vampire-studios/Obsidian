package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class AnimatableModel<E extends Entity> extends EntityModel<E> {

    public abstract ImmutableMap<String, ModelPart> getAllParts();

    public AnimatableModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public AnimatableModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        getAllParts().get("root").render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
