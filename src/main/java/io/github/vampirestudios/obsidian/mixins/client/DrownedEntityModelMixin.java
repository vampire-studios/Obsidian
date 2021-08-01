package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.api.TridentInterface;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrownedEntityModel.class)
public class DrownedEntityModelMixin {
    @Redirect(method = "animateModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean ob_getItem(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof TridentInterface;
    }
}