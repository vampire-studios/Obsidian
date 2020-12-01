package io.github.vampirestudios.obsidian;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public abstract class GeometryLivingEntityRenderer<T extends LivingEntity, M extends EntityGeometryModel<T>> extends EntityRenderer<T> {

    private final M model;

    protected GeometryLivingEntityRenderer(EntityRendererFactory.Context context, M model, float shadowRadius) {
        super(context);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected float getLyingAngle(T entity) { return 90.0F; }
    protected void scale(T entity, MatrixStack matrices, float amount) { }
    protected float getAnimationCounter(T entity, float tickDelta) {
        return 0.0F;
    }

    public abstract Identifier getTexture(T entity);

    protected RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean bl) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        } else if (showBody) {
            return this.model.getLayer(identifier);
        } else {
            return bl ? RenderLayer.getOutline(identifier) : null;
        }
    }

    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack stack, VertexConsumerProvider provider, int light) {
        stack.push();

        float bodyYaw = MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        float headYaw = MathHelper.lerp(tickDelta, entity.prevHeadYaw, entity.headYaw);
        float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        float yawProgress = headYaw - bodyYaw;

        if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity vehicle = (LivingEntity)entity.getVehicle();
            bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, vehicle.prevBodyYaw, vehicle.bodyYaw);
            yawProgress = headYaw - bodyYaw;
            float o = MathHelper.clamp(MathHelper.wrapDegrees(yawProgress), -85F, 85F);

            bodyYaw = headYaw - o;
            if (o * o > 2500.0F)
                bodyYaw += o * 0.2F;

            yawProgress = headYaw - bodyYaw;
        }

        float adjustedEyeHeight ;
        if (entity.getPose() == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            if (direction != null) {
                adjustedEyeHeight = entity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                stack.translate((float)(-direction.getOffsetX()) * adjustedEyeHeight, 0.0D, (float)(-direction.getOffsetZ()) * adjustedEyeHeight);
            }
        }
        float animationProgress = this.getAnimationProgress(entity, tickDelta);
        this.setupTransforms(entity, stack, animationProgress, bodyYaw, tickDelta);
        stack.scale(-1.0F, 1.0F, 1.0F);
        this.scale(entity, stack, tickDelta);
        stack.translate(0.0D, (0.1 / 16F), 0.0D);

        float limbDistance = 0.0F;
        float limbAngle = 0.0F;
        if (!entity.hasVehicle() && entity.isAlive()) {
            limbDistance = Math.min(MathHelper.lerp(tickDelta, entity.lastLimbDistance, entity.limbDistance), 1.0F);
            limbAngle = entity.limbAngle - entity.limbDistance * (1.0F - tickDelta);
            if (entity.isBaby())
                limbAngle *= 3.0F;
        }

        this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, yawProgress, pitch);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean showBody = this.isVisible(entity);
        boolean translucent = !showBody && !entity.isInvisibleTo(minecraftClient.player);
        boolean outline = minecraftClient.hasOutline(entity);
        RenderLayer renderLayer = this.getRenderLayer(entity, showBody, translucent, outline);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = provider.getBuffer(renderLayer);
            int r = getOverlay(entity, this.getAnimationCounter(entity, tickDelta));
            this.model.renderModel(stack, vertexConsumer, light, r, 1.0F, 1.0F, 1.0F, translucent ? 0.15F : 1.0F);
        }

        //TODO Feature Renderer

        stack.pop();
        super.render(entity, yaw, tickDelta, stack, provider, light);
    }

    protected float getAnimationProgress(T entity, float tickDelta) {
        return (float)entity.age + tickDelta;
    }

    protected boolean isVisible(T entity) {
        return !entity.isInvisible();
    }

    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        EntityPose entityPose = entity.getPose();
        if (entityPose != EntityPose.SLEEPING) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - bodyYaw));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * this.getLyingAngle(entity)));
        } else if (entity.isUsingRiptide()) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F - entity.pitch));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(((float)entity.age + tickDelta) * -75.0F));
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(entity)));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
        } else if (entity.hasCustomName() || entity instanceof PlayerEntity) {
            String string = Formatting.strip(entity.getName().getString());
            if (("Dinnerbone".equals(string) || "Grumm".equals(string)) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE))) {
                matrices.translate(0.0D, entity.getHeight() + 0.1F, 0.0D);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
            }
        }

    }

    private static float getYaw(Direction direction) {
        switch(direction) {
            case SOUTH:
                return 90.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
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