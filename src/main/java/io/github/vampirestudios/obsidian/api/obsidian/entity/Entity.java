package io.github.vampirestudios.obsidian.api.obsidian.entity;

import java.util.Map;

public class Entity {

    public Information information;
    public Map<String, Component> components;
    public Map<String, ComponentGroup> component_groups;
    public Map<String, Event> events;

    public EntityComponents entity_components;

}