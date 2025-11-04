package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record BundleTooltip(BundleContents contents) implements TooltipComponent {
}
