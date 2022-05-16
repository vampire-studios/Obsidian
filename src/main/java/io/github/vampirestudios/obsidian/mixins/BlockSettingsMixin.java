package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.ExtendedBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.Settings.class)
public class BlockSettingsMixin implements ExtendedBlockSettings {

    @Shadow Material material;

    @Shadow boolean collidable;
    @Shadow BlockSoundGroup soundGroup;
    @Shadow float resistance;
    @Shadow float hardness;
    @Shadow boolean toolRequired;
    @Shadow boolean randomTicks;
    @Shadow float slipperiness;
    @Shadow float velocityMultiplier;
    @Shadow float jumpVelocityMultiplier;
    @Shadow Identifier lootTableId;
    @Shadow boolean opaque;
    @Shadow boolean isAir;
    @Shadow boolean dynamicBounds;
    private MapColor obsidian$mapColor;

    @Override
    public void obsidian$write(PacketByteBuf buf) {
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
            buf.writeIdentifier(lootTableId);
        }

        buf.writeBoolean(opaque);
        buf.writeBoolean(isAir);
        buf.writeBoolean(dynamicBounds);
    }

    @Inject(method = "<init>(Lnet/minecraft/block/Material;Lnet/minecraft/block/MapColor;)V", at = @At("RETURN"))
    private void setMapColor(Material material, MapColor mapColorProvider, CallbackInfo ci) {
        obsidian$mapColor = mapColorProvider;
    }

    @Inject(method = "mapColor", at = @At("HEAD"))
    private void setMapColor(MapColor color, CallbackInfoReturnable<AbstractBlock.Settings> cir) {
        obsidian$mapColor = color;
    }
}