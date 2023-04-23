package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(PartDefinition.class)
public interface ModelPartDataAccessor {
	@Invoker("<init>")
	static PartDefinition create(List<CubeDefinition> list, PartPose modelTransform) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	List<CubeDefinition> getCubes();

	@Accessor
	PartPose getPartPose();

	@Accessor
	Map<String, PartDefinition> getChildren();



}
