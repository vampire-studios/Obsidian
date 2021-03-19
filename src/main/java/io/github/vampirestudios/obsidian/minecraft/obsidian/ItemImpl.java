package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ItemImpl extends Item {

	public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

	public ItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Settings settings) {
		super(settings);
		this.item = item;
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return item.information.has_glint;
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
