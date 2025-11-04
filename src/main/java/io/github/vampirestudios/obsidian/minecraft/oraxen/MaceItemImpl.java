package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class MaceItemImpl extends MaceItem {

    public NexoItem item;

    public MaceItemImpl(NexoItem item, float attackDamage, float attackSpeed, Properties settings) {
        super(settings.rarity(Rarity.EPIC)
                .durability(500)
                .component(DataComponents.TOOL, createToolProperties())
                .repairable(BuiltInRegistries.ITEM.getValue(item.repairItem))
                .attributes(createAttributes(attackDamage, attackSpeed))
                .enchantable(15)
                .component(DataComponents.WEAPON, new Weapon(1)));
        this.item = item;
    }

    public static @NotNull ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2, false);
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