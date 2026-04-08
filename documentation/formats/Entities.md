# Obsidian Entities (Obsidian-Native Format)

Entity authoring is state-first:

- `components`: baseline capabilities
- `component_sets`: reusable component bundles
- `properties`: typed runtime state
- `states`: rules that apply component sets from properties/predicates
- `predicates`: named reusable checks
- `timers`: named delayed transitions
- `events`: actions that mutate state/timers and chain events
- `attributes`: optional entity attribute map (`minecraft:*` attribute id -> number)
- `interactions`: structured interaction entries
- `ai`: structured AI payload (`goal_selector` / `brain`)

Runtime note: `interactions` are now executable on entity right-click for Obsidian entities (`EntityImpl`), including `actions`, `sound`, `item_damage`, and optional equipment assignment.

## Top-Level Shape

```json
{
  "description": { "...": "entity metadata" },
  "properties": {},
  "components": {},
  "component_sets": {},
  "states": [],
  "predicates": {},
  "timers": {},
  "events": {},
  "interactions": [],
  "ai": {}
}
```

`information` and `component_groups` are still accepted as legacy aliases.

## Properties

Each property supports:

- `type`: `boolean | enum | string | int | float`
- `default` / `default_value`
- `enum_values` (for `enum`)
- `client_sync`

## States

Each state can match with:

- `property` + `equals`
- `property` + `in`
- `predicate`

And applies component sets via `apply`.

## Event Actions

Supported built-ins:

- `set_property`
- `trigger_event`
- `sequence`
- `random_choice`
- `if`
- `start_timer`
- `stop_timer`

## Behavior Component Notes

- Keep tempt distance as a real entity attribute, for example:
  - `"attributes": { "minecraft:tempt_range": 10.0 }`

## Timers

Timer definitions include:

- `duration_ticks`
- `on_complete_event`
- `loop`

## Example

```json
{
  "description": {
    "name": "Rubber Ball Creature",
    "spawnable": true,
    "summonable": true
  },
  "properties": {
    "size": { "type": "enum", "enum_values": ["small", "medium"], "default": "small", "client_sync": true },
    "has_block": { "type": "boolean", "default": false, "client_sync": true },
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
    "size_medium": { "minecraft:movement": { "value": 0.18 } },
    "with_block": { "minecraft:health": { "value": 30, "max": 30 } }
  },
  "states": [
    { "property": "size", "equals": "small", "apply": ["size_small"] },
    { "property": "size", "equals": "medium", "apply": ["size_medium"] },
    { "property": "has_block", "equals": true, "apply": ["with_block"] }
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
        { "type": "set_property", "property": "has_block", "value": true },
        { "type": "set_property", "property": "can_pickup", "value": false },
        { "type": "start_timer", "timer": "pickup_cooldown" }
      ]
    },
    "pickup_ready": {
      "actions": [
        { "type": "set_property", "property": "can_pickup", "value": true }
      ]
    }
  },
  "ai": {
    "goal_selector": {
      "backend": "goal_selector",
      "goals": [
        { "id": "minecraft:behavior.look_at_player", "priority": 4 }
      ]
    },
    "brain": {
      "backend": "brain",
      "sensors": [],
      "activities": [],
      "memories": []
    }
  }
}
```
