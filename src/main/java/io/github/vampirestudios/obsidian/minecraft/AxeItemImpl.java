package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

public class AxeItemImpl extends AxeItem {

    public AxeItemImpl(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

}