package io.github.vampirestudios.obsidian.api.obsidian.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

	public Information information;
	public transient Map<String, Component> components = new HashMap<>();
	public List<ComponentTwo> components_two = new ArrayList<>();
	public Map<String, ComponentGroup> component_groups = new HashMap<>();
	public Map<String, Event> events;

	public EntityComponents entity_components;

}
