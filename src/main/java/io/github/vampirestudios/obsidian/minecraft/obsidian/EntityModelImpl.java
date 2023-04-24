package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;

public class EntityModelImpl extends HierarchicalModel<EntityImpl> {

    public ModelPart part;
    public EntityModel entityModel;

    public EntityModelImpl(EntityModel entityModelIn) {
        this.entityModel = entityModelIn;
        part = entityModelIn.getTexturedModelData().bakeRoot();
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.part;
    }

    @Override
    public void setupAnim(EntityImpl entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        entity.animationStates.forEach((animationState, identifier) -> this.animate(
                animationState, Registries.ANIMATION_DEFINITIONS.get(identifier), animationProgress
        ));
    }

}
