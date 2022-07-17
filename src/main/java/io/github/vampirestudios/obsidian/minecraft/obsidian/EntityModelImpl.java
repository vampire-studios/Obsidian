package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class EntityModelImpl extends SinglePartEntityModel<EntityImpl> {

    public ModelPart part;
    public EntityModel entityModel;

    public EntityModelImpl(EntityModel entityModelIn) {
        this.entityModel = entityModelIn;
        part = entityModelIn.getTexturedModelData().createModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.part;
    }

    @Override
    public void setAngles(EntityImpl entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        entity.animationStates.forEach((animationState, identifier) -> this.method_43781(
                animationState, Obsidian.ANIMATION_DEFINITIONS.get(identifier), animationProgress
        ));
    }

}
