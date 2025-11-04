package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.ResourceLocation;

public class ShieldItem extends Item {

    public boolean can_have_banner = true;
    public int cooldownTicks;
    public ResourceLocation repairItem = ResourceLocation.withDefaultNamespace("air");
    public ResourceLocation blockSound = ResourceLocation.withDefaultNamespace("item.shield.block");
    public ResourceLocation breakSound = ResourceLocation.withDefaultNamespace("item.shield.break");

}
