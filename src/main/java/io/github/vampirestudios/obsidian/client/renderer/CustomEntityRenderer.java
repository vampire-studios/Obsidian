package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class CustomEntityRenderer extends MobRenderer<EntityImpl, EntityModel<EntityImpl>> {

    private final Entity entity;

    public CustomEntityRenderer(EntityRendererProvider.Context context, Entity entity) {
        super(context, entity.information.getNewEntityModel(context), entity.shadowSize);
        this.entity = entity;
    }

    @Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
        return entity.information.getEntityTexture();
    }

	@Override
	protected boolean shouldShowName(EntityImpl entity, double distanceToCameraSq) {
        return false;
    }

	@Override
	public EntityRenderState createRenderState() {
		return null;
	}

    @Override
    protected void scale(EntityImpl entity, PoseStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(g, g, g);
    }
}
