package org.quiltmc.qsl.enchantment.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.enchantment.impl.EnchantmentContext;
import org.quiltmc.qsl.enchantment.impl.EnchantmentGodClass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentScreenHandlerMixin extends AbstractContainerMenu {
	@Shadow
	@Final
	private ContainerLevelAccess access;

	private Player player;
	private int bookcases;

	// Empty constructor... ignore
	protected EnchantmentScreenHandlerMixin(@Nullable MenuType<?> type, int syncId) { super(type, syncId); }

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	public void capturePlayer(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo callback) {
		this.player = playerInventory.player;
	}

	@Inject(method = "method_17411", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;setSeed(J)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onContentChanged(ItemStack stack, Level world, BlockPos pos, CallbackInfo ci, int i) {
		this.bookcases = i;
	}

	@Inject(method = "getEnchantmentList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Ljava/util/List;"))
	private void setEnchantmentContext(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentInstance>> callback) {
		this.access.execute((world, pos) -> EnchantmentGodClass.context.set(new EnchantmentContext(0, 0, this.bookcases, stack, world, this.player, pos, world.getBlockState(pos), world.getBlockEntity(pos))));
	}

	@Inject(method = "getEnchantmentList", at = @At("RETURN"))
	private void clearEnchantmentContext(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentInstance>> callback) {
		EnchantmentGodClass.context.remove();
	}
}