# Entities

Mobs, described as state rather than as script: components give the entity its capabilities, properties
hold its runtime state, and states swap component bundles in and out as those properties change.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/entity/rubber_ball.json
```

The file name is the entity id.

## Shape of the file

```json
{
  "description": { "name": "Rubber Ball", "spawnable": true, "summonable": true },
  "properties": {},
  "components": {},
  "component_sets": {},
  "states": [],
  "predicates": {},
  "timers": {},
  "events": {},
  "interactions": [],
  "attributes": {},
  "ai": {}
}
```

| Section | Holds |
| --- | --- |
| `description` | Identity, spawn egg, model. Required. |
| `components` | Baseline capabilities. |
| `component_sets` | Named bundles of components, applied conditionally. |
| `properties` | Typed runtime state. |
| `states` | Rules that apply component sets based on properties or predicates. |
| `predicates` | Named reusable checks. |
| `timers` | Named delayed transitions. |
| `events` | Actions that change state, run timers, and chain into other events. |
| `interactions` | What happens on right-click. |
| `attributes` | `minecraft:*` attribute id → number. |
| `ai` | Goal selector and/or brain. |
| `animations`, `shadow_size` | Display. |

`information` is accepted as an alias for `description`, and `component_groups` for `component_sets`.

## Three components are mandatory

Whatever else the file contains, the entity fails to register without these:

```json
{
  "components": {
    "minecraft:collision_box": { "width": 0.6, "height": 0.6 },
    "minecraft:health": { "value": 20, "max": 20 },
    "minecraft:movement": { "value": 0.25 }
  }
}
```

They feed the entity's dimensions, its starting health and its base speed, all of which are needed
before the entity type can be built. A missing one shows up as `Failed to register entity` with no
further explanation.

## description

| Field | Meaning |
| --- | --- |
| `name` | Display name. |
| `spawnable` | Generates a spawn egg. |
| `summonable` | Allowed as a `/summon` target. |
| `spawn_egg` | `{ "base_color": "#RRGGBB", "overlay_color": "#RRGGBB" }`. Defaults to black on white. |
| `vanilla_entity_type` | Which vanilla model to borrow when the entity has no model of its own. Default `minecraft:pig`. |
| `custom_model`, `entityModelPath`, `textureLocation` | Use a custom model instead. |

Recognised borrowed models are pig, villager, chicken, squid, zombie, skeleton, fox, horse and bear.
Anything else falls back to the cow model.

## Properties

Typed values that live on the entity and persist:

```json
{
  "properties": {
    "size": { "type": "enum", "enum_values": ["small", "medium"], "default": "small", "client_sync": true },
    "has_block": { "type": "boolean", "default": false, "client_sync": true }
  }
}
```

| Field | Meaning |
| --- | --- |
| `type` | `boolean`, `enum`, `string`, `int` or `float`. |
| `default` | Starting value. `default_value` also accepted. |
| `enum_values` | The allowed values, for `enum`. |
| `client_sync` | Send to clients. Required if the model or renderer reads the property. |

## States

A state rule watches a property and applies component sets while it matches:

```json
{
  "states": [
    { "property": "size", "equals": "small", "apply": ["size_small"] },
    { "property": "size", "in": ["medium", "large"], "apply": ["size_big"] },
    { "predicate": "can_pickup_now", "apply": ["pickup_ready"] }
  ]
}
```

Match on `property` + `equals`, `property` + `in`, or a named `predicate`. `apply` names entries in
`component_sets`, whose components override the baseline ones while the rule holds.

## Timers and events

```json
{
  "timers": {
    "pickup_cooldown": { "duration_ticks": 100, "on_complete_event": "pickup_ready", "loop": false }
  },
  "events": {
    "picked_up_block": {
      "actions": [
        { "type": "set_property", "property": "has_block", "value": true },
        { "type": "start_timer", "timer": "pickup_cooldown" }
      ]
    }
  }
}
```

Event actions: `set_property`, `trigger_event`, `sequence`, `random_choice`, `if`, `start_timer`,
`stop_timer`.

Note these use `type`, not `action` — entity events are a separate system from the item and block
[events](../Events.md), and the two do not share action names.

## Interactions

Right-click behaviour, executed on Obsidian entities:

```json
{
  "interactions": [
    {
      "id": "feed_cheese",
      "display_text": "Feed",
      "predicate": "is_hungry",
      "actions": [{ "type": "set_property", "property": "fed", "value": true }],
      "sound": "minecraft:entity.generic.eat",
      "item_damage": 1,
      "equipment_slot": "chest",
      "equipment_item": "examplepack:cheese_armor"
    }
  ]
}
```

## AI

```json
{
  "ai": {
    "goal_selector": {
      "backend": "goal_selector",
      "goals": [{ "id": "minecraft:behavior.look_at_player", "priority": 4 }],
      "targets": []
    },
    "brain": { "backend": "brain", "sensors": [], "activities": [], "memories": [] }
  }
}
```

`goal_selector` is the classic priority-ordered goal list; `brain` is the newer activity system. The `ai`
section is folded in as a `minecraft:ai` component, so a component set can swap an entity's whole AI.

## Attributes

Plain attribute id to number, applied when the entity type is built:

```json
{ "attributes": { "minecraft:tempt_range": 10.0, "minecraft:follow_range": 32.0 } }
```

Prefer this over duplicating a value inside a behaviour component — a real attribute is what the rest of
the game reads.

## Full example

```json
{
  "description": { "name": "Rubber Ball Creature", "spawnable": true, "summonable": true },
  "properties": {
    "size": { "type": "enum", "enum_values": ["small", "medium"], "default": "small", "client_sync": true },
    "can_pickup": { "type": "boolean", "default": true }
  },
  "components": {
    "minecraft:collision_box": { "width": 0.6, "height": 0.6 },
    "minecraft:health": { "value": 20, "max": 20 },
    "minecraft:movement": { "value": 0.25 },
    "minecraft:movement.basic": {}
  },
  "component_sets": {
    "size_small": { "minecraft:movement": { "value": 0.25 } },
    "size_medium": { "minecraft:movement": { "value": 0.18 } }
  },
  "states": [
    { "property": "size", "equals": "small", "apply": ["size_small"] },
    { "property": "size", "equals": "medium", "apply": ["size_medium"] }
  ],
  "predicates": {
    "can_pickup_now": { "property": "can_pickup", "value": true }
  },
  "timers": {
    "pickup_cooldown": { "duration_ticks": 100, "on_complete_event": "pickup_ready" }
  },
  "events": {
    "picked_up_block": {
      "actions": [
        { "type": "set_property", "property": "can_pickup", "value": false },
        { "type": "start_timer", "timer": "pickup_cooldown" }
      ]
    },
    "pickup_ready": {
      "actions": [{ "type": "set_property", "property": "can_pickup", "value": true }]
    }
  },
  "ai": {
    "goal_selector": {
      "backend": "goal_selector",
      "goals": [{ "id": "minecraft:behavior.look_at_player", "priority": 4 }]
    }
  }
}
```

## See also

* [Events](../Events.md) — the separate item and block event system.
* [Pack Structure](../PackStructure.md) — where entity files live.
