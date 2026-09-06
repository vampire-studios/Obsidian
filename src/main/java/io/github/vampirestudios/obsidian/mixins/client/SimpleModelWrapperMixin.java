package io.github.vampirestudios.obsidian.mixins.client;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SimpleModelWrapper.class)
public class SimpleModelWrapperMixin {

	/**
	 * @author a
	 * @reason a
	 */
//	@Overwrite
//	public static BlockStateModelPart bake(ModelBaker modelBakery, Identifier location, ModelState state) {
//		ResolvedModel model = modelBakery.getModel(location);
//		TextureSlots textureSlots = model.getTopTextureSlots();
//		boolean hasAmbientOcclusion = model.getTopAmbientOcclusion();
//		Material.Baked particleMaterial = model.resolveParticleMaterial(textureSlots, modelBakery);
//		QuadCollection geometry = model.bakeTopGeometry(textureSlots, modelBakery, state);
//		return new SimpleModelWrapper(geometry, hasAmbientOcclusion, particleMaterial);
//	}

	@WrapOperation(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/SimpleModelWrapper;findNonBlockSprites(Lnet/minecraft/client/resources/model/geometry/QuadCollection;)Lcom/google/common/collect/Multimap;"))
	private static @Nullable Multimap<Identifier, Identifier> wrapOperationOnBake(QuadCollection geometry, Operation<Multimap<Identifier, Identifier>> original, @Local(argsOnly = true) ModelBaker modelBakery) {
		return null;
	}

}
