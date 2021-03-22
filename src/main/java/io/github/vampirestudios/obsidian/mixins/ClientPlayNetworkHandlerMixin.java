package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private MinecraftClient client;

	@Shadow
	private ClientWorld world;

	// Overrides the trident spawning so that custom tridents can be created on the client correctly
	@Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
	public void ob_overrideClientTridentSpawn(EntitySpawnS2CPacket packet, CallbackInfo info) {
		if (packet.getEntityTypeId() != EntityType.TRIDENT) {
			return;
		}

		NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		Entity tridentEntity = new TridentEntity(EntityType.TRIDENT, world);
		tridentEntity.setPos(d, e, f);

		ItemStack tridentStack = new ItemStack(Registry.ITEM.get(packet.getEntityData()));

		if (tridentStack.getItem() != Items.TRIDENT) {
			tridentEntity = new SimpleTridentItemEntity(EntityType.TRIDENT, world, tridentStack);
			tridentEntity.setPos(d, e, f);
		}

		int i = packet.getId();
		tridentEntity.updateTrackedPosition(d, e, f);
		tridentEntity.refreshPositionAfterTeleport(d, e, f);
		tridentEntity.pitch = packet.getPitch() * 360 / 256.0F;
		tridentEntity.yaw = packet.getYaw() * 360 / 256.0F;
		tridentEntity.setEntityId(i);
		tridentEntity.setUuid(packet.getUuid());
		world.addEntity(i, tridentEntity);
		info.cancel();
	}
}