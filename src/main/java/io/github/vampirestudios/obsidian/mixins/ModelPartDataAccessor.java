package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(ModelPartData.class)
public interface ModelPartDataAccessor {
	@Invoker("<init>")
	static ModelPartData create(List<ModelCuboidData> list, ModelTransform modelTransform) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	List<ModelCuboidData> getCuboidData();

	@Accessor
	ModelTransform getRotationData();

	@Accessor
	Map<String, ModelPartData> getChildren();



}
