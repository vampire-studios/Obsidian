package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CrucibleSkill {
    public Identifier id;

    public int Cooldown;
    public List<String> Skills;
    public List<String> Conditions;
    public List<String> TargetConditions;
    public List<String> TriggerConditions;
    public List<Skill> internalSkills;

    public CrucibleSkill() {
        this.Skills = new ArrayList<>();
        this.Conditions = new ArrayList<>();
        this.TargetConditions = new ArrayList<>();
        this.TriggerConditions = new ArrayList<>();
        this.internalSkills = new ArrayList<>();
    }
}