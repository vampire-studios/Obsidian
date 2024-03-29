package io.github.vampirestudios.obsidian.api.obsidian.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class Entity {

    public Information information;
    public Map<String, Component> components = new HashMap<>();
    public List<ComponentTwo> components_two = new ArrayList<>();
    public Map<String, ComponentGroup> component_groups = new HashMap<>();
    public Map<String, Event> events;
    public List<ResourceLocation> animations;
    @SerializedName("shadow_size")
    public float shadowSize = 1.0F;

    public EntityComponents entity_components;

}
