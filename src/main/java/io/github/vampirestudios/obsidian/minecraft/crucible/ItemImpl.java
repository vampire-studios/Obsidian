package io.github.vampirestudios.obsidian.minecraft.crucible;

import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleItem;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillManager;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemImpl extends Item {

    public CrucibleItem item;
    public SkillManager skillManager;

    public ItemImpl(CrucibleItem item, SkillManager skillManager, Properties settings) {
        super(settings);
        this.item = item;
        this.skillManager = skillManager;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TagParser.QUICK_TEXT_WITH_STF.parseNode(item.Display).toText();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag context) {
        if (item.Lore != null) {
            for (String lore : item.Lore) {
                tooltip.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toText());
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (item.Skills != null && !item.Skills.isEmpty() && entity instanceof LivingEntity livingEntity) {
            skillManager.triggerSkills(SkillTrigger.ON_TICK, new SkillContext(livingEntity, null, null));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();

        if (item.Skills != null && !item.Skills.isEmpty()) {
            if (player.getUseItem().getItem() instanceof BlockItem) {
                skillManager.triggerSkills(SkillTrigger.ON_BLOCK_PLACE, new SkillContext(player, null, useOnContext.getClickedPos()));
            }
            skillManager.triggerSkills(SkillTrigger.ON_USE, new SkillContext(player, null, useOnContext.getClickedPos()));
        }

        return super.useOn(useOnContext);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (item.Skills != null && !item.Skills.isEmpty()) {
            skillManager.triggerSkills(SkillTrigger.ON_USE, new SkillContext(player, null, null));
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity caster) {
        if (item.Skills != null && !item.Skills.isEmpty()) {
            skillManager.triggerSkills(SkillTrigger.ON_COMBAT, new SkillContext(caster, target, null));
        }
    }

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity caster) {
        if (item.Skills != null && !item.Skills.isEmpty()) {
            skillManager.triggerSkills(SkillTrigger.ON_COMBAT, new SkillContext(caster, target, null));
        }
    }
}
