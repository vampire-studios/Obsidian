package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** Extended-reach sword — adds reach_bonus blocks to entity interaction range. */
public class LongswordItemImpl extends ItemImpl {

	public LongswordItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		super(item, applyComponents(item, buildProps(item, toolMaterial, attackDamage, attackSpeed, settings)));
	}

	private static Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
		settings.sword(toolMaterial, attackDamage, attackSpeed);

		if (item.reach_bonus > 0) {
			// Re-build attribute modifiers to include reach on top of the sword defaults.
			settings.attributes(ItemAttributeModifiers.builder()
					.add(Attributes.ATTACK_DAMAGE,
							new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
									toolMaterial.attackDamageBonus() + attackDamage,
									AttributeModifier.Operation.ADD_VALUE),
							EquipmentSlotGroup.MAINHAND)
					.add(Attributes.ATTACK_SPEED,
							new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
									attackSpeed,
									AttributeModifier.Operation.ADD_VALUE),
							EquipmentSlotGroup.MAINHAND)
					.add(Attributes.ENTITY_INTERACTION_RANGE,
							new AttributeModifier(
									Identifier.fromNamespaceAndPath("obsidian", "longsword_reach"),
									item.reach_bonus,
									AttributeModifier.Operation.ADD_VALUE),
							EquipmentSlotGroup.MAINHAND)
					.build());
		}

		return settings;
	}

	public WeaponItem weapon() {
		return (WeaponItem) item;
	}
}
