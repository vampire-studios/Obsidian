package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;

public class SetCustomModelDataSkill extends Skill {
    private final int customModelData;

    public SetCustomModelDataSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, int customModelData) {
        super(skillId, target, trigger);
        this.customModelData = customModelData;
    }

    @Override
    public void applyEffect(LivingEntity caster) {
        // Get the item in the main hand of the target (or modify as needed)
        ItemStack itemStack = caster.getMainHandItem();
        if (!itemStack.isEmpty()) {
            itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(), List.of(customModelData)));
        }
    }

    @Override
    public List<String> aliases() {
        // Define aliases for this skill type
        return List.of("setitemmodel", "setmodel");
    }
}
