package com.shnupbups.oxidizelib.mixin;

import com.shnupbups.oxidizelib.OxidizeLib;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

	@Redirect(method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	public <T> Optional<T> redirectGetUnwaxedState(T state, ItemUsageContext context) {
		return (Optional<T>) OxidizeLib.getUnwaxedState(context.getWorld().getBlockState(context.getBlockPos()));
	}

}