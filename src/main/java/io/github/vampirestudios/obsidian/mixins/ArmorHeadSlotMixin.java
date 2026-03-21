/*
package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.CosmeticSlotExt;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.server.level.ServerPlayer$1")
public class ArmorHeadSlotMixin {
    @Final
    @Shadow
	private ServerPlayer cache;

    @ModifyVariable(
            method = "sendSlotChange(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ItemStack modifyHeadSlotItem(ItemStack stack, AbstractContainerMenu handler, int slot) {
        if(handler instanceof InventoryMenu && ((CosmeticSlotExt) handler).getHeadCosmetics() != ItemStack.EMPTY && slot == 5){
            return ((CosmeticSlotExt) handler).getHeadCosmetics();
        }
        return stack;
    }
    @Inject(
            method = "sendInitialData",
            at = @At(
                    value = "TAIL"
            )
    )
    void modifyHeadSlotItem (AbstractContainerMenu abstractContainerMenu, List<ItemStack> list, ItemStack itemStack, int[] is, CallbackInfo ci) {
        if(abstractContainerMenu instanceof InventoryMenu && ((CosmeticSlotExt) abstractContainerMenu).getHeadCosmetics() != ItemStack.EMPTY) {
            ItemStack itemStack1 = ((CosmeticSlotExt) abstractContainerMenu).getHeadCosmetics();
            this.cache.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 5, itemStack1));
        }
    }
}*/
