package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugHudMixin {
	@Inject(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2,
			shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void quilt$addTestAttachment(CallbackInfoReturnable<List<String>> cir, long l, long m, long n, long o,
										List<String> list, BlockPos blockPos, BlockState blockState) {
		Boolean value = Obsidian.BASED.getNullable(blockState.getBlock());
		String valueStr;
		if (value == null) {
			valueStr = ChatFormatting.BLUE + "unset";
		} else if (value) {
			valueStr = ChatFormatting.GREEN + "yes";
		} else {
			valueStr = ChatFormatting.RED + "no";
		}

		list.add("[Quilt] based: " + valueStr);
	}
}