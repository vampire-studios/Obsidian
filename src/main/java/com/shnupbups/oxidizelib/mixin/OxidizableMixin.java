package com.shnupbups.oxidizelib.mixin;

import com.shnupbups.oxidizelib.OxidizeLib;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Inject(method = "getDecreasedOxidationBlock", at=@At("RETURN"))
	private static void getDecreasedOxidationBlock(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		cir.setReturnValue(OxidizeLib.getDecreasedOxidizationBlock(block));
	}

	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Inject(method = "getIncreasedOxidationBlock", at=@At("RETURN"))
	private static void getIncreasedOxidationBlock(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		cir.setReturnValue(OxidizeLib.getIncreasedOxidizationBlock(block));
	}

	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Inject(method = "getUnaffectedOxidationBlock", at=@At("RETURN"))
	private static void getUnaffectedOxidationBlock(Block block, CallbackInfoReturnable<Block> cir) {
		cir.setReturnValue(OxidizeLib.getUnaffectedOxidizationBlock(block));
	}
}