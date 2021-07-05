package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class EntityModelImpl<T extends LivingEntity> extends BipedEntityModel<T> {

    private static EntityModel entityModel;

    public EntityModelImpl(ModelPart modelPart, EntityModel entityModelIn) {
        super(modelPart);
        entityModel = entityModelIn;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
        ModelPartData modelPartData = modelData.getRoot();
        for (EntityModel.ModelPart part : entityModel.parts) {
            ModelPartBuilder modelPartBuilder = ModelPartBuilder.create();
            for (EntityModel.ModelPart.Cuboid cuboid : part.cuboids) {
                modelPartBuilder.uv(cuboid.textureX, cuboid.textureY).cuboid(cuboid.offsetX, cuboid.offsetY, cuboid.offsetZ, cuboid.sizeX, cuboid.sizeY, cuboid.sizeZ);
            }
            modelPartData.addChild(part.name, modelPartBuilder, ModelTransform.of(part.pivotX, part.pivotY, part.pivotZ, part.pitch, part.yaw, part.roll));
        }
        return TexturedModelData.of(modelData, entityModel.textureWidth, entityModel.textureHeight);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        for(EntityModel.ModelPart part : entityModel.parts) {
            part.visible = true;
        }
    }
}
