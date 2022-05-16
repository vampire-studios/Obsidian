package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.AnimationState;
import io.github.vampirestudios.obsidian.KeyframeAnimations;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;

public class EntityModelImpl<T extends LivingEntity> extends SinglePartEntityModel<T> {

    public ModelPart part;
    public EntityModel entityModel;
    public AnimationState testing = new AnimationState();

    public EntityModelImpl(EntityModel entityModelIn) {
        this.entityModel = entityModelIn;
        part = entityModelIn.getTexturedModelData().createModel();
        testing.start();
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
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        testing.ifStarted(
                animationStatex -> KeyframeAnimations.animate(this, Obsidian.ANIMATION_DEFINITIONS.get(entityModel.animation), Util.getMeasuringTimeMs() - animationStatex.startTime(), 1.0F, new Vec3f())
        );
    }

}
