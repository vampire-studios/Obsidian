package io.github.vampirestudios.obsidian.mixins;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.elytra.FabricElytraExtensions;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	public static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
		Item item = stack.getItem();
		EquipmentSlotProvider equipmentSlotProvider = ((ItemExtensions) item).fabric_getEquipmentSlotProvider();

		if (equipmentSlotProvider != null) {
			return equipmentSlotProvider.getPreferredEquipmentSlot(stack);
		}

		if (!stack.isOf(Items.CARVED_PUMPKIN) && (!(item instanceof BlockItem) || !(((BlockItem) item).getBlock() instanceof AbstractSkullBlock))) {
			if (item instanceof ArmorItem armorItem) {
				return armorItem.getSlotType();
			} else if (stack.getItem() instanceof ElytraItem || stack.getItem() instanceof FabricElytraExtensions) {
				return EquipmentSlot.CHEST;
			} else {
				return stack.getItem() instanceof ShieldItem ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
			}
		} else {
			return EquipmentSlot.HEAD;
		}
	}
}
