package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CustomBlockItem extends BlockItem {

	public final Block block;

	public CustomBlockItem(Block block, net.minecraft.world.level.block.Block blockImpl, Properties settings) {
		super(blockImpl, settings);
		this.block = block;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return block.information.getItemSettings() != null
				? block.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted())
				: super.isFoil(stack);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
		block.addLore(consumer);
	}
}
