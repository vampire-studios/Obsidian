package org.quiltmc.qsl.item.extension.mixin.trident;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.quiltmc.qsl.item.extension.impl.trident.TridentClientModInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class TridentEntityMixin extends Entity {
    public TridentEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "createSpawnPacket", at = @At("HEAD"))
    public void sendTridentStackBeforeSpawnPacket(CallbackInfoReturnable<Packet<?>> cir) {
        if ((Object) this instanceof ThrownTrident trident) {
            FriendlyByteBuf passedData = PacketByteBufs.create();
            passedData.writeItem(((TridentEntityAccessor) trident).getTridentStack());
            this.level.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity ->
                    ServerPlayNetworking.send(serverPlayerEntity, TridentClientModInitializer.TRIDENT_SPAWN_PACKET_ID, passedData));
        }
    }
}