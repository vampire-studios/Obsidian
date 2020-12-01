package io.github.vampirestudios.obsidian;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class AnimatableEntityRenderer<E extends MobEntity, M extends EntityModel<E>> extends MobEntityRenderer<E, M> {

    public abstract Map<Identifier, Animation> getAnimationRegisterMap();
    
    public AnimatableEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
        super(context, entityModel, f);
        AnimationRegistry.INSTANCE.addAnimationMap(getAnimationRegisterMap());
    }

    public void render(E mobEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider provider, int i) {
        AnimationData data = Obsidian.COMPONENT_ANIMATION.get(mobEntity).getData();
        if(data != null) {
            matrixStack.push();
            data.processAnimation(delta);
            super.render(mobEntity, yaw, delta, matrixStack, provider, i);
            matrixStack.pop();
        } else {
            super.render(mobEntity, yaw, delta, matrixStack, provider, i);
        }

    }

}