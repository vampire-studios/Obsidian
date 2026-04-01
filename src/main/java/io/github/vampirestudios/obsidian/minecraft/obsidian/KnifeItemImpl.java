package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Dagger that can optionally be thrown (throwable: true).
 * Right-click throws the knife, consuming one from the stack.
 * Also applies backstab bonus when striking from behind.
 */
public class KnifeItemImpl extends Item {

    public final WeaponItem item;

    public KnifeItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
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
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (item.throwable) {
            ItemStack stack = user.getItemInHand(hand);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                    0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (!world.isClientSide()) {
                ThrownKnifeEntity knife = new ThrownKnifeEntity(Obsidian.THROWN_KNIFE, world);
                knife.setOwner(user);
                knife.setItem(stack);
                knife.setDamage(item.attackDamage * 0.5f);
                knife.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, 1.5f, 1.0f);
                world.addFreshEntity(knife);
                if (!user.getAbilities().instabuild) stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        EventActionHandler.handleOnUse(user, item);
        return InteractionResult.PASS;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return;
        EventActionHandler.handleHurtEnemy(target, player, item);
        if (item.backstab_multiplier > 1.0f && DaggerItemImpl.isBackstab(attacker, target)) {
            float bonusDamage = item.attackDamage * (item.backstab_multiplier - 1.0f);
            target.hurt(player.damageSources().playerAttack(player), bonusDamage);
        }
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
    public InteractionResult useOn(UseOnContext context) {
        EventActionHandler.handleOnUseOn(context, item);
        return super.useOn(context);
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
