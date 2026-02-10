package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalBreedMixin {

    @org.jspecify.annotations.Nullable
    @Shadow
    public abstract ServerPlayer getLoveCause();

    @Inject(method = "spawnChildFromBreeding", at = @At("TAIL"))
    private void crucible$afterSpawnChildFromBreeding(ServerLevel level, Animal otherParent, CallbackInfo ci) {
        Player cause = this.getLoveCause();
        if (!(cause instanceof ServerPlayer sp)) return;

        // optional: if you want the baby as target, you'd need a different injection point that exposes the baby entity.
        CrucibleEvents.fire(SkillTrigger.BREED,
                SkillContext.builder(sp)
                        .level(level)
                        .build()
        );
    }
}
