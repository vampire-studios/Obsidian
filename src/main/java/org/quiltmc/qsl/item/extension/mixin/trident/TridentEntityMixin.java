package org.quiltmc.qsl.item.extension.mixin.trident;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.quiltmc.qsl.item.extension.impl.trident.TridentClientModInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public abstract class TridentEntityMixin extends Entity {
    public TridentEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createSpawnPacket", at = @At("HEAD"))
    public void sendTridentStackBeforeSpawnPacket(CallbackInfoReturnable<Packet<?>> cir) {
        if ((Object) this instanceof TridentEntity trident) {
            PacketByteBuf passedData = PacketByteBufs.create();
            passedData.writeItemStack(((TridentEntityAccessor) trident).getTridentStack());
            this.world.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity ->
                    ServerPlayNetworking.send(serverPlayerEntity, TridentClientModInitializer.TRIDENT_SPAWN_PACKET_ID, passedData));
        }
    }
}