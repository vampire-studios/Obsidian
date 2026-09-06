package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianItemHolder;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ShieldItemImpl extends net.minecraft.world.item.ShieldItem implements ObsidianItemHolder {

	public ShieldItem item;

	public ShieldItemImpl(ShieldItem shieldItem, Item.Properties settings) {
		super(settings
				.equippableUnswappable(EquipmentSlot.OFFHAND)
				.repairable(BuiltInRegistries.ITEM.getValue(shieldItem.repairItem))
				.delayedComponent(
						DataComponents.BLOCKS_ATTACKS,
						context -> new BlocksAttacks(
								shieldItem.cooldownTicks,
								1.0F,
								List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
								new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
								Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
								Optional.of(BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.blockSound))),
								Optional.of(BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.breakSound)))
						)
				)
				.component(DataComponents.BREAK_SOUND, BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.breakSound)))
		);
		this.item = shieldItem;
	}

	@Override
	public io.github.vampirestudios.obsidian.api.obsidian.item.Item obsidianItem() {
		return item;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		item.addLore(consumer);
	}
}
