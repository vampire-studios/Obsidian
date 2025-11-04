package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
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
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ShieldItemImpl extends net.minecraft.world.item.ShieldItem {

    public NexoItem item;

	public ShieldItemImpl(NexoItem shieldItem, Item.Properties settings) {
		super(settings.durability(336)
				.component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
				.repairable(BuiltInRegistries.ITEM.getValue(shieldItem.repairItem))
				.equippableUnswappable(EquipmentSlot.OFFHAND)
				.component(
						DataComponents.BLOCKS_ATTACKS,
						new BlocksAttacks(
								shieldItem.cooldownTicks,
								1.0F,
								List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
								new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
								Optional.of(DamageTypeTags.BYPASSES_SHIELD),
								Optional.of(BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.blockSound))),
								Optional.of(BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.breakSound)))
						)
				)
				.component(DataComponents.BREAK_SOUND, BuiltInRegistries.SOUND_EVENT.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, shieldItem.breakSound)))
		);
		this.item = shieldItem;
	}

	@Override
	public Component getName(ItemStack stack) {
		return this.item.getName(this);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		if (item.lore != null) {
			for (String lore : item.lore) {
				consumer.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toText());
			}
		}
	}
}
