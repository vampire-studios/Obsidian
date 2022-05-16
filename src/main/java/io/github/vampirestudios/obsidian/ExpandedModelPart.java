package io.github.vampirestudios.obsidian;

import net.minecraft.client.model.ModelTransform;
import net.minecraft.util.math.Vec3f;

public interface ExpandedModelPart {
	ModelTransform getInitialModelTransform();
	void setInitialModelTransform(ModelTransform modelTransform);
	void resetModelTransform();

	boolean hasChild(String string);

	float getXScale();
	float getYScale();
	float getZScale();

	void offsetPos(Vec3f vector3f);
	void offsetRotation(Vec3f vector3f);
	void offsetScale(Vec3f vector3f);
}
