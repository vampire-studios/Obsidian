/*
package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import io.github.vampirestudios.obsidian.api.crucible.targets.TargetFactory;

public class SkillParserOld {

    public static Skill parseSkill(String skillString) {
        String skillId = "";
        String target = "";
        String trigger = "";
        int timer = -1;

        if (!skillString.startsWith("skill")) return null;

        // Extracting the skill ID
        int skillStart = skillString.indexOf("{s=") + 3;
        int skillEnd = skillString.indexOf("}", skillStart);
        if (skillStart > 2 && skillEnd > skillStart) {
            skillId = skillString.substring(skillStart, skillEnd);
        }

        // Extracting the target
        int targetStart = skillString.indexOf("@") + 1;
        int targetEnd = skillString.indexOf(" ", targetStart);
        if (targetStart > 0 && targetEnd > targetStart) {
            target = skillString.substring(targetStart, targetEnd);
        }

        // Extracting the trigger and optional timer
        int triggerStart = skillString.indexOf("~") + 1;
        if (triggerStart > 0) {
            if (skillString.contains(":")) {
                int triggerEnd = skillString.indexOf(":", triggerStart);
                trigger = skillString.substring(triggerStart, triggerEnd);
                timer = Integer.parseInt(skillString.substring(triggerEnd + 1));
            } else {
                trigger = skillString.substring(triggerStart);
            }
        }

        SkillTarget<?> target1 = TargetFactory.getTarget(target);

        Skill skill = new Skill();
        skill.skillId = skillId;
        skill.target = target1;
        skill.trigger = trigger;
        if (timer > -1)
            skill.timer = timer;
        return skill;
    }
}*/
