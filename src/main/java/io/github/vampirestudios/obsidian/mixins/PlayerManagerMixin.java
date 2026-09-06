package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.CosmeticSlotExt;
import io.github.vampirestudios.obsidian.CosmeticsData;
import io.github.vampirestudios.obsidian.IEntityDataSaver;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
	@Inject(
			method = "placeNewPlayer",
			at = @At(value = "TAIL")
	)
	void modifyHeadSlotItem(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		AbstractContainerMenu handler = player.containerMenu;
		ItemStack itemStack = CosmeticsData.getHeadCosmetics((IEntityDataSaver) player);
		if (itemStack != ItemStack.EMPTY) {
			((CosmeticSlotExt) handler).setHeadCosmetics(itemStack);
			player.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 5, itemStack));
		}
	}
}