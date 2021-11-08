package com.shnupbups.oxidizelib.mixin;

import com.shnupbups.oxidizelib.OxidizeLib;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

	@Inject(method = "getWaxedState", at = @At("HEAD"), cancellable = true)
	private static void obsidian_customWaxedBlocks(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
		var block = OxidizeLib.getWaxedState(state);
		if (block.isPresent()) {
			cir.setReturnValue(block.map(b -> b.getBlock().getStateWithProperties(state)));
		}
	}

}