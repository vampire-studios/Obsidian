package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class PickaxeItemImpl extends PickaxeItem {

    public PickaxeItemImpl(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

}