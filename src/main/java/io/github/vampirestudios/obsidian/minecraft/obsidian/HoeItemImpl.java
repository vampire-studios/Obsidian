package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class HoeItemImpl extends HoeItem {

	public Item item;

	public HoeItemImpl(Item item, ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
		this.item = item;
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return item.information.has_glint;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return item.information.is_enchantable;
	}

	@Override
	public int getEnchantability() {
		return item.information.enchantability;
	}

	@Override
	public Text getName() {
		return item.information.name.getName("item");
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if (item.display != null && item.display.lore.length != 0) {
			for (TooltipInformation tooltipInformation : item.display.lore) {
				tooltip.add(tooltipInformation.getTextType("tooltip"));
			}
		}
	}

}