package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Modify how the trident entity data is sent, changing it from the uuid to the Registry ID for the item
@Mixin(PersistentProjectileEntity.class)
public class TridentEntityMixin {
    @Inject(method = "createSpawnPacket", at = @At("HEAD"), cancellable = true)
    public void ob_sendTridentTypeToClient(CallbackInfoReturnable<Packet<?>> cir) {
        if ((PersistentProjectileEntity) (Object) this instanceof TridentEntity) {
            cir.setReturnValue(new EntitySpawnS2CPacket((PersistentProjectileEntity) (Object) this, Registry.ITEM.getRawId(((TridentEntityAccessor) this).getTridentStack().getItem())));
        }
    }
}