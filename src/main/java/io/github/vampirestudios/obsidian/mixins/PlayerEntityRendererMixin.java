package io.github.vampirestudios.obsidian.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
	private static void ob_getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		ItemStack stackInHand = abstractClientPlayerEntity.getStackInHand(hand);

		if (!abstractClientPlayerEntity.handSwinging && stackInHand.getItem() instanceof CrossbowInterface && CrossbowItem.isCharged(stackInHand)) {
			cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
		}
	}
}