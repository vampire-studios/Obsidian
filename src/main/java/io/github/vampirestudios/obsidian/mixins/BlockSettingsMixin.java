package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.ExtendedBlockSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.Properties.class)
public class BlockSettingsMixin implements ExtendedBlockSettings {

    @Shadow Material material;

    @Shadow boolean collidable;
    @Shadow SoundType soundGroup;
    @Shadow float resistance;
    @Shadow float hardness;
    @Shadow boolean toolRequired;
    @Shadow boolean randomTicks;
    @Shadow float slipperiness;
    @Shadow float velocityMultiplier;
    @Shadow float jumpVelocityMultiplier;
    @Shadow ResourceLocation lootTableId;
    @Shadow boolean opaque;
    @Shadow boolean isAir;
    @Shadow boolean dynamicBounds;
    private MaterialColor obsidian$mapColor;

    @Override
    public void obsidian$write(FriendlyByteBuf buf) {
//        SimpleSerializers.writeMaterial(buf, material);
        buf.writeVarInt(obsidian$mapColor.id);
        buf.writeBoolean(collidable);
//        SimpleSerializers.writeBlockSoundGroup(buf, soundGroup);
        buf.writeFloat(resistance);
        buf.writeFloat(hardness);
        buf.writeBoolean(toolRequired);
        buf.writeBoolean(randomTicks);
        buf.writeFloat(slipperiness);
        buf.writeFloat(velocityMultiplier);
        buf.writeFloat(jumpVelocityMultiplier);

        buf.writeBoolean(lootTableId != null);
        if (lootTableId != null) {
            buf.writeResourceLocation(lootTableId);
        }

        buf.writeBoolean(opaque);
        buf.writeBoolean(isAir);
        buf.writeBoolean(dynamicBounds);
    }

    @Inject(method = "<init>(Lnet/minecraft/block/Material;Lnet/minecraft/block/MapColor;)V", at = @At("RETURN"))
    private void setMapColor(Material material, MaterialColor mapColorProvider, CallbackInfo ci) {
        obsidian$mapColor = mapColorProvider;
    }

    @Inject(method = "mapColor", at = @At("HEAD"))
    private void setMapColor(MaterialColor color, CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
        obsidian$mapColor = color;
    }
}