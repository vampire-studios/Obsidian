package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class CrucibleEvents {
    private CrucibleEvents() {}

    private static SkillManager manager;

    public static void bootstrap(SkillManager m) {
        if (manager != null) return; // only once
        manager = m;
    }

    public static void fire(SkillTrigger trigger, SkillContext ctx) {
        if (manager == null) return;

        // 1) run global skills
        manager.triggerSkills(trigger, ctx, SkillScope.GLOBAL);

        // 2) run held-item skills (main/off hand)
        if (ctx.caster != null) {
            fireHeldItem(trigger, ctx, InteractionHand.MAIN_HAND);
            fireHeldItem(trigger, ctx, InteractionHand.OFF_HAND);
        }

        // 3) run augment skills from all equipment slots
        if (ctx.caster instanceof ServerPlayer sp) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                fireAugmentSkills(trigger, ctx, sp, slot);
            }
        }
    }

    private static void fireAugmentSkills(SkillTrigger trigger, SkillContext base,
                                          ServerPlayer player, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty()) return;
        var sockets = stack.get(OItemComponents.AUGMENT_SOCKETS);
        if (sockets == null) return;

        var skills = AugmentManager.getInstance().getAugmentSkills(player, slot);
        if (skills.isEmpty()) return;

        SkillContext ctx = SkillContext.builder(player)
                .target(base.target)
                .position(base.position)
                .level(base.level)
                .stack(stack)
                .projectile(base.projectile)
                .build();
        manager.executeSkills(skills, trigger, ctx);
    }

    private static void fireHeldItem(SkillTrigger trigger, SkillContext base, InteractionHand hand) {
        ItemStack stack = base.caster.getItemInHand(hand);
        if (stack == null || stack.isEmpty()) return;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        CrucibleItem crucibleItem = itemId != null ? ContentRegistries.CRUCIBLE_ITEMS.getValue(itemId) : null;
        if (crucibleItem == null || crucibleItem.internalSkills == null || crucibleItem.internalSkills.isEmpty()) return;

        SkillContext ctx = SkillContext.builder(base.caster)
                .target(base.target)
                .position(base.position)
                .level(base.level)
                .hand(hand)
                .stack(stack)
                .projectile(base.projectile)
                .build();

        manager.executeSkills(crucibleItem.internalSkills, trigger, ctx);
    }
}
