package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {

	@Inject(method = "isInGroup", at = @At("HEAD"), cancellable = true)
	public void onAllowedIn(ItemGroup group, CallbackInfoReturnable<Boolean> cir) {
		if (getCreativeTabs().stream().anyMatch(tab -> tab == group)) cir.setReturnValue(true);
	}

}
