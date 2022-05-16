package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.ExpandedModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ExpandedModelPart {
	@Shadow public float pivotX;
	@Shadow public float pivotY;
	@Shadow public float pivotZ;
	@Shadow public float pitch;
	@Shadow public float yaw;
	@Shadow public float roll;
	@Shadow @Final private Map<String, ModelPart> children;

	@Shadow public abstract void setTransform(ModelTransform rotationData);

	public float xScale = 1.0F;
	public float yScale = 1.0F;
	public float zScale = 1.0F;
	private ModelTransform initialModelTransform = ModelTransform.NONE;

	@Override
	public ModelTransform getInitialModelTransform() {
		return initialModelTransform;
	}

	@Override
	public void setInitialModelTransform(ModelTransform modelTransform) {
		this.initialModelTransform = modelTransform;
	}

	@Override
	public void resetModelTransform() {
		this.setTransform(initialModelTransform);
	}

	@Override
	public boolean hasChild(String string) {
		return this.children.containsKey(string);
	}

	@Override
	public float getXScale() {
		return xScale;
	}

	@Override
	public float getYScale() {
		return yScale;
	}

	@Override
	public float getZScale() {
		return zScale;
	}

	@Override
	public void offsetPos(Vec3f vector3f) {
		this.pivotX += vector3f.getX();
		this.pivotY += vector3f.getY();
		this.pivotZ += vector3f.getZ();
	}

	@Override
	public void offsetRotation(Vec3f vector3f) {
		this.pitch += vector3f.getX();
		this.yaw += vector3f.getY();
		this.roll += vector3f.getZ();
	}

	@Override
	public void offsetScale(Vec3f vector3f) {
		this.xScale += vector3f.getX();
		this.yScale += vector3f.getY();
		this.zScale += vector3f.getZ();
	}

	@Inject(method = "setTransform", at=@At("RETURN"))
	public void expandedSetTransform(ModelTransform rotationData, CallbackInfo ci) {
		this.xScale = 1.0F;
		this.yScale = 1.0F;
		this.zScale = 1.0F;
	}

	@Inject(method = "copyTransform", at=@At("RETURN"))
	public void expandedCopyTransform(ModelPart part, CallbackInfo ci) {
		this.xScale = ((ExpandedModelPart)part).getXScale();
		this.yScale = ((ExpandedModelPart)part).getYScale();
		this.zScale = ((ExpandedModelPart)part).getZScale();
	}

	@Inject(method = "rotate", at=@At("RETURN"))
	public void expandedRotate(MatrixStack matrices, CallbackInfo ci) {
		if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
			matrices.scale(this.xScale, this.yScale, this.zScale);
		}
	}
}
