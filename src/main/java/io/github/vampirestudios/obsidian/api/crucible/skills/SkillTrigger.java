/*
package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EmptyMetadata;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;

public final class SkillTrigger<T extends SkillTriggerMetadata> extends Record {
  private final String name;
  
  private final Class<T> metadataClass;
  
  private final List<String> aliases;

  public String name() {
    return this.name;
  }
  
  public Class<T> metadataClass() {
    return this.metadataClass;
  }
  
  public List<String> aliases() {
    return this.aliases;
  }
  
  private static final Map<String, SkillTrigger> TRIGGERS = new HashMap<>();
  
  @Deprecated
  public static SkillTrigger trigger(String name) {
    String thatName = name.toUpperCase();
    if (TRIGGERS != null && 
      TRIGGERS.containsKey(thatName))
      return TRIGGERS.get(thatName); 
    return create(thatName, new String[0]);
  }
  
  public static SkillTrigger create(String name, String... aliases) {
    return new SkillTrigger<>(name, EmptyMetadata.class, Arrays.asList(aliases));
  }
  
  public static SkillTrigger create(String name, Class<? extends SkillTriggerMetadata> clazz, String... aliases) {
    return new SkillTrigger<>(name, clazz, Arrays.asList(aliases));
  }
  
  public static SkillTrigger get(String name) {
    String thatName = name.toUpperCase();
    if (TRIGGERS.containsKey(thatName))
      return TRIGGERS.get(thatName); 
    return TRIGGERS.get("DEFAULT");
  }
  
  public static void register(SkillTrigger trigger) {
    Validate.notNull(trigger, "trigger cannot be null", new Object[0]);
    String name = trigger.name();
    List<String> aliases = trigger.aliases;
    if (!TRIGGERS.containsKey(name))
      TRIGGERS.put(name, trigger); 
    aliases.forEach(alias -> {
          if (!TRIGGERS.containsKey(alias))
            TRIGGERS.put(alias, trigger); 
        });
  }
  
  public static Collection<SkillTrigger> values() {
    return TRIGGERS.values();
  }
  
  public SkillTrigger(String name, Class<T> metadataClass, List<String> aliases) {
    this.name = name.toUpperCase();
    this.metadataClass = metadataClass;
    this.aliases = aliases.stream().map(String::toUpperCase).toList();
  }
  
  public void register() {
    register(this);
  }
}*/
