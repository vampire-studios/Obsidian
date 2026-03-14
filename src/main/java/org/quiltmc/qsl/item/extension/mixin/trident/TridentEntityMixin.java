package org.quiltmc.qsl.item.extension.mixin.trident;

import io.github.vampirestudios.obsidian.api.obsidian.TridentStackPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class TridentEntityMixin extends Entity {
	public TridentEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "recreateFromPacket", at = @At("TAIL"))
	public void sendTridentStackOnSpawn(CallbackInfo ci) {
		if ((Object) this instanceof ThrownTrident trident && !this.level().isClientSide()) {
			ItemStack stack = ((AbstractArrowAccessor) trident).getPickupItemStack();
			TridentStackPayload payload = new TridentStackPayload(stack);
			this.level().getServer().getPlayerList().getPlayers().forEach(player ->
					ServerPlayNetworking.send(player, payload));
		}
	}
}
