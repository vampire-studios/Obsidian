package io.github.vampirestudios.obsidian.api.nexo.mechanics;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.nexo.ItemMechanic;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Locale;

public class ArmorMechanic implements ItemMechanic {
	public String type;
	public ResourceLocation material;
	public ResourceLocation texture;
	public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial armor_material;

	@Override
	public boolean applies(NexoItem item) {
		return item.mechanics != null && item.mechanics.armor != null;
	}

	@Override
	public Item wrap(Item base, NexoItem item, Item.Properties props) {
		var mech = item.mechanics.armor;
		// 1) look up your custom armor‐material source
		io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial matSrc;
		if (mech.material != null && ContentRegistries.ARMOR_MATERIALS.containsKey(mech.material)) {
			matSrc = ContentRegistries.ARMOR_MATERIALS.getValue(mech.material);
		} else if (mech.armor_material != null) {
			matSrc = mech.armor_material;
		} else {
			throw new IllegalStateException("Missing armor.material for " + item.id);
		}

		// 2) build the vanilla ArmorMaterial wrapper
		ResourceKey<EquipmentAsset> assetKey =
				ResourceKey.create(Obsidian.ROOT_ID, mech.material);
		ArmorMaterial vanillaMat = new ArmorMaterial(
				matSrc.getDurability(item.getEquipmentSlot()),
				matSrc.defense,
				matSrc.enchantability,
				SoundEvents.ARMOR_EQUIP_LEATHER,
				matSrc.toughness,
				matSrc.knockback_resistance,
				TagKey.create(Registries.ITEM, matSrc.repair_tag),
				assetKey
		);

		// 3) tell the props which slot this is
		ArmorType slot = ArmorType.valueOf(mech.type.toUpperCase(Locale.ROOT));
		props.humanoidArmor(vanillaMat, slot);

		// 4) pick your actual Item subclass
		if (item.mechanics.dyeable != null) {
			return new io.github.vampirestudios.obsidian.minecraft.oraxen.DyeableArmorItemImpl(item, props);
		} else {
			return new io.github.vampirestudios.obsidian.minecraft.oraxen.CustomArmorItem(item, props);
		}
	}
}
