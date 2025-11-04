/*
package io.github.vampirestudios.obsidian.api.crucible.skills;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleEventSkillMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleSkillMetadata;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SkillTriggerMetadata {
  private final Map<String, Object> data;
  
  private final Collection<String> tags;
  
  public static SimpleSkillMetadata simple(Consumer<SkillMetadata> eventMetadataConsumer) {
    return new SimpleSkillMetadata(eventMetadataConsumer);
  }

  public Map<String, Object> getData() {
    return this.data;
  }
  
  public Collection<String> getTags() {
    return this.tags;
  }
  
  public SkillTriggerMetadata() {
    this.data = new ConcurrentHashMap<>();
    this.tags = null;
  }
  
  public SkillTriggerMetadata(DamageMetadata damageMetadata) {
    if (damageMetadata == null) {
      this.data = Maps.newHashMap();
      this.tags = null;
    } else {
      this.data = damageMetadata.getData();
      this.tags = damageMetadata.getTags();
    } 
  }
  
  public void putObject(String key, Object value) {
    this.data.put(key, value);
  }
  
  public Object getValue(String key) {
    return this.data.get(key);
  }
  
  public Object getOrDefault(String key, Object def) {
    return this.data.getOrDefault(key, def);
  }
  
  public void putAll(Map<String, Object> data) {
    this.data.putAll(data);
  }
  
  public void putString(String key, String value) {
    this.data.put(key, value);
  }
  
  public void putBoolean(String key, boolean value) {
    this.data.put(key, Boolean.valueOf(value));
  }
  
  public void putFloat(String key, float value) {
    this.data.put(key, Float.valueOf(value));
  }
  
  public void putDouble(String key, double value) {
    this.data.put(key, Double.valueOf(value));
  }
  
  public String getString(String key) {
    return this.data.get(key).toString();
  }
  
  public boolean getBoolean(String key) {
    Object object = getValue(key);
    if (object instanceof Boolean) {
      Boolean bool = (Boolean)object;
      return bool.booleanValue();
    } 
    return false;
  }
  
  public float getFloat(String key) {
    Object object = getValue(key);
    if (object instanceof Float) {
      Float f = (Float)object;
      return f.floatValue();
    } 
    throw new NumberFormatException(getString(key) + " is not a decimal value");
  }
  
  public double getDouble(String key) {
    Object object = getValue(key);
    if (object instanceof Double) {
      Double f = (Double)object;
      return f.doubleValue();
    } 
    return getFloat(key);
  }
  
  public abstract void applyToSkillMetadata(SkillMetadata paramSkillMetadata);
}*/
