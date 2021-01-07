package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.network.PacketC2SLoopAnimation;
import io.github.vampirestudios.obsidian.network.PacketC2SRemoveAnimation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public abstract class AnimatableEntityRenderer<T extends Entity, M extends EntityGeometryModel<T>> extends EntityRenderer<T> {

    private final M model;

    protected AnimatableEntityRenderer(EntityRenderDispatcher dispatcher, M model, float shadowRadius) {
        super(dispatcher);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack stack, VertexConsumerProvider provider, int light) {
        stack.push();

        stack.scale(-1.0F, 1.0F, 1.0F);
        stack.translate(0.0D, (0.1 / 16F), 0.0D);

        Map<AnimationContext, Boolean> queue = new HashMap<>();

        ((EntityExt)entity).getClientAnimations().forEach((ctx, a) -> {
            int animTime = entity.age - a;
            ctx.applyData(this.model, animTime, tickDelta);
            if(ctx.shouldLoop())
                queue.put(ctx, true);
            else if(ctx.isDone())
                queue.put(ctx, false);
        });

        if(!queue.isEmpty()) {
            queue.forEach((ctx, b) -> {
                if(b) {
                    ((EntityExt) entity).getClientAnimations().replace(ctx, entity.age);
                    ClientInit.INSTANCE.networkHandler.sendToServer(new PacketC2SLoopAnimation(entity, ctx.id, entity.age));
                    ctx.loop();
                } else {
                    ((EntityExt)entity).getClientAnimations().removeInt(ctx);
                    ClientInit.INSTANCE.networkHandler.sendToServer(new PacketC2SRemoveAnimation(entity, ctx.id, false));
                }
            });
            queue.clear();
        }

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean translucent = !entity.isInvisibleTo(minecraftClient.player);
        boolean outline = minecraftClient.hasOutline(entity);
        RenderLayer renderLayer = this.getRenderLayer(entity, translucent, outline);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = provider.getBuffer(renderLayer);
            this.model.renderModel(stack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        //TODO Feature Renderer

        stack.pop();
        super.render(entity, yaw, tickDelta, stack, provider, light);
    }

    protected RenderLayer getRenderLayer(T entity, boolean translucent, boolean bl) {
        Identifier identifier = this.getTexture(entity);
        if (translucent)
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        else
            return bl ? RenderLayer.getOutline(identifier) : null;
    }

    @Override
    protected boolean hasLabel(T livingEntity) {
        double d = this.dispatcher.getSquaredDistanceToCamera(livingEntity);
        float f = livingEntity.isSneaky() ? 32.0F : 64.0F;
        if (d >= (double)(f * f)) {
            return false;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
            if (livingEntity != clientPlayerEntity) {
                AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
                AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
                if (abstractTeam != null) {
                    AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
                    switch(visibilityRule) {
                        case ALWAYS:
                            return bl;
                        case NEVER:
                            return false;
                        case HIDE_FOR_OTHER_TEAMS:
                            return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
                        case HIDE_FOR_OWN_TEAM:
                            return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
                        default:
                            return true;
                    }
                }
            }

            return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
        }
    }
}