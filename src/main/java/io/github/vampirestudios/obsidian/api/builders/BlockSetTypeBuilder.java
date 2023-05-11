package io.github.vampirestudios.obsidian.api.builders;

import io.github.vampirestudios.obsidian.api.Utils;
import io.github.vampirestudios.obsidian.api.parsers.ThingParser;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BlockSetTypeBuilder extends BaseBuilder<BlockSetType, BlockSetTypeBuilder> {
    public static BlockSetTypeBuilder begin(ThingParser<BlockSetTypeBuilder> ownerParser, ResourceLocation registryName) {
        return new BlockSetTypeBuilder(ownerParser, registryName);
    }

    private ResourceLocation soundType;
    private ResourceLocation doorClose;
    private ResourceLocation doorOpen;
    private ResourceLocation trapdoorClose;
    private ResourceLocation trapdoorOpen;
    private ResourceLocation pressurePlateOff;
    private ResourceLocation pressurePlateOn;
    private ResourceLocation buttonOff;
    private ResourceLocation buttonOn;
    private boolean isWood;
    private boolean canOpenByHand;
    private ResourceLocation hangingSignSoundType;
    private ResourceLocation fenceGateClose;
    private ResourceLocation fenceGateOpen;

    private BlockSetTypeBuilder(ThingParser<BlockSetTypeBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }


    public void setSoundType(ResourceLocation resourceLocation) {
        soundType = resourceLocation;
    }

    public void setDoorClose(ResourceLocation resourceLocation) {
        doorClose = resourceLocation;
    }

    public void setDoorOpen(ResourceLocation resourceLocation) {
        doorOpen = resourceLocation;
    }

    public void setTrapdoorClose(ResourceLocation resourceLocation) {
        trapdoorClose = resourceLocation;
    }

    public void setTrapdoorOpen(ResourceLocation resourceLocation) {
        trapdoorOpen = resourceLocation;
    }

    public void setPressurePlateOff(ResourceLocation resourceLocation) {
        pressurePlateOff = resourceLocation;
    }

    public void setPressurePlateOn(ResourceLocation resourceLocation) {
        pressurePlateOn = resourceLocation;
    }

    public void setButtonOff(ResourceLocation resourceLocation) {
        buttonOff = resourceLocation;
    }

    public void setButtonOn(ResourceLocation resourceLocation) {
        buttonOn = resourceLocation;
    }

    public void setIsWood(boolean b) {
        isWood = b;
    }

    public void setCanOpenByHand(boolean canOpenByHand) {
        this.canOpenByHand = canOpenByHand;
    }

    public void setHangingSignSoundType(ResourceLocation resourceLocation) {
        hangingSignSoundType = resourceLocation;
    }

    public void setFenceGateClose(ResourceLocation resourceLocation) {
        fenceGateClose = resourceLocation;
    }

    public void setFenceGateOpen(ResourceLocation resourceLocation) {
        fenceGateOpen = resourceLocation;
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Block Set Type";
    }

    @Override
    protected BlockSetType buildInternal() {
        var soundTypeObj = Utils.getOrElse(Registries.SOUND_TYPES, soundType, SoundType.WOOD);
        var doorCloseEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, doorClose, SoundEvents.WOODEN_DOOR_CLOSE);
        var doorOpenEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, doorOpen, SoundEvents.WOODEN_DOOR_OPEN);
        var trapdoorCloseEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, trapdoorClose, SoundEvents.WOODEN_TRAPDOOR_CLOSE);
        var trapdoorOpenEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, trapdoorOpen, SoundEvents.WOODEN_TRAPDOOR_OPEN);
        var pressurePlateOffEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, pressurePlateOff, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF);
        var pressurePlateOnEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, pressurePlateOn, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON);
        var buttonOffEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, buttonOff, SoundEvents.WOODEN_BUTTON_CLICK_OFF);
        var buttonOnEvent = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, buttonOn, SoundEvents.WOODEN_BUTTON_CLICK_ON);
        return BlockSetTypeRegistry.register(getRegistryName(), canOpenByHand, soundTypeObj,
                doorCloseEvent, doorOpenEvent, trapdoorCloseEvent, trapdoorOpenEvent,
                pressurePlateOffEvent, pressurePlateOnEvent, buttonOffEvent, buttonOnEvent
        );
    }

    public boolean isWood() {
        return isWood;
    }

    public boolean canOpenByHand() {
        return canOpenByHand;
    }

    public WoodType buildWoodType(BlockSetType setType) {
        var hangingSignSoundType = Utils.getOrElse(Registries.SOUND_TYPES, pressurePlateOn, SoundType.HANGING_SIGN);
        var fenceGateClose = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, this.fenceGateClose, SoundEvents.FENCE_GATE_CLOSE);
        var fenceGateOpen = Utils.getOrElse(BuiltInRegistries.SOUND_EVENT, this.fenceGateOpen, SoundEvents.FENCE_GATE_OPEN);
        return new WoodType(getRegistryName().toString(), setType, setType.soundType(), hangingSignSoundType, fenceGateClose, fenceGateOpen);
    }
}