package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.client.renderer.BackToolFeatureRenderer;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {

	protected PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
		this.addLayer(new BackToolFeatureRenderer(this));
	}

	/** Supplies the direction vanilla's sleeping transform normally obtains from a bed block. */
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("RETURN")
	)
	private void obsidian$lyingSeatDirection(Avatar avatar, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		if (avatar.getVehicle() instanceof SeatEntity seat && seat.isLying()) {
			state.bedOrientation = Direction.fromYRot(seat.getYRot());
		}
	}

}
