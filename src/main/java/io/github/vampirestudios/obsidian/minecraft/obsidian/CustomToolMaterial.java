package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

public class CustomToolMaterial implements ToolMaterial {
	
	public io.github.vampirestudios.obsidian.api.obsidian.item.ToolMaterial toolMaterial;

	public CustomToolMaterial(io.github.vampirestudios.obsidian.api.obsidian.item.ToolMaterial toolMaterial) {
		this.toolMaterial = toolMaterial;
	}

	@Override
	public int getDurability() {
		return toolMaterial.durability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return toolMaterial.miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return toolMaterial.attackDamage;
	}

	@Override
	public int getMiningLevel() {
		return toolMaterial.miningLevel;
	}

	@Override
	public int getEnchantability() {
		return toolMaterial.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(Registry.ITEM.get(toolMaterial.repairItem));
	}

}