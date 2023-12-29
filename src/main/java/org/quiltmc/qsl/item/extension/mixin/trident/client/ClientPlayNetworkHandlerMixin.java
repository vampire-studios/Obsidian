package org.quiltmc.qsl.item.extension.mixin.trident.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.quiltmc.qsl.item.extension.impl.trident.TridentClientModInitializer;
import org.quiltmc.qsl.item.extension.mixin.trident.AbstractArrowAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "handleAddEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;recreateFromPacket(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onEntitySpawnMixin(ClientboundAddEntityPacket packet, CallbackInfo ci, Entity entity) {
        if (entity.getType() == EntityType.TRIDENT) {
            ((AbstractArrowAccessor) entity).setPickupItemStack(TridentClientModInitializer.TRIDENT_QUEUE.remove());
        }
    }
}