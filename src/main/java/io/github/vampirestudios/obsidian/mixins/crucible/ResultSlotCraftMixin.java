package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ResultSlotCraftMixin {

    @Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void crucible$craft(Player player, ItemStack crafted, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer sp)) return;

        CrucibleEvents.fire(
                SkillTrigger.ITEM_CRAFT,
                SkillContext.builder(sp)
                        .level(sp.level())
                        .stack(crafted)
                        .build()
        );
    }
}
