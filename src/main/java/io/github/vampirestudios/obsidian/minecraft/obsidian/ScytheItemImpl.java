package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Wide-arc harvesting weapon.
 * aoe_radius > 0 hits nearby entities on each melee strike.
 * Right-clicking farmable dirt tills it like a hoe.
 */
public class ScytheItemImpl extends Item {

    public final WeaponItem item;

    public ScytheItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(buildProps(item, toolMaterial, attackDamage, attackSpeed, settings));
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    private static <T> Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        settings.sword(toolMaterial, attackDamage, attackSpeed);
        if (item.components != null) {
            for (TypedDataComponent<?> entry : item.components) {
                applyTyped(settings, entry);
            }
        }
        return settings;
    }

    @SuppressWarnings("unchecked")
    private static <T> void applyTyped(Item.Properties props, TypedDataComponent<T> entry) {
        props.component(entry.type(), entry.value());
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return;
        EventActionHandler.handleHurtEnemy(target, player, item);
        if (item.aoe_radius > 0 && !attacker.level().isClientSide()) {
            float aoeDamage = item.attackDamage * item.aoe_damage_multiplier;
            List<LivingEntity> nearby = attacker.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(item.aoe_radius),
                    e -> e != attacker && e != target);
            for (LivingEntity nearby_entity : nearby) {
                nearby_entity.hurt(player.damageSources().playerAttack(player), aoeDamage);
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (context.getClickedFace() != Direction.DOWN) {
            BlockState tilled = MattockItemImpl.getTilledStateOf(state);
            if (tilled != null) {
                Player player = context.getPlayer();
                world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!world.isClientSide()) {
                    world.setBlock(pos, tilled, 11);
                    if (player != null) context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                return InteractionResult.SUCCESS;
            }
        }

        EventActionHandler.handleOnUseOn(context, item);
        return super.useOn(context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        item.addLore(tooltip);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(entity instanceof Player player)) { super.onUseTick(level, entity, stack, remaining); return; }
        EventActionHandler.handleOnUseTick(player, item);
        super.onUseTick(level, entity, stack, remaining);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return super.finishUsingItem(stack, level, entity);
        EventActionHandler.handleOnFinishUsing(player, item);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof Player player)) { super.inventoryTick(stack, level, entity, slot); return; }
        EventActionHandler.handleOnInventoryTick(player, item);
        super.inventoryTick(stack, level, entity, slot);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        EventActionHandler.handleOnItemCrafted(player, item);
        super.onCraftedBy(stack, player);
    }
}
