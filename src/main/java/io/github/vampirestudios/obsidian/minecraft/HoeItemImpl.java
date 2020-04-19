package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolMaterial;

public class HoeItemImpl extends HoeItem {

    public HoeItemImpl(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

}