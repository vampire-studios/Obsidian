package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.Identifier;

public class ShieldItem extends Item {

    public boolean can_have_banner = true;
    public int cooldownTicks;
    public Identifier repairItem = Identifier.withDefaultNamespace("air");
    public Identifier blockSound = Identifier.withDefaultNamespace("item.shield.block");
    public Identifier breakSound = Identifier.withDefaultNamespace("item.shield.break");

}
