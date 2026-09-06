# Attribute Modifiers

A change to one of an entity's attributes: +3 attack damage, −20% movement speed. Used inside other
files, never on its own.

Two places take them, and they do not share a shape.

## On an item, as a component

The vanilla `minecraft:attribute_modifiers` component, exactly as data packs write it:

```json
{
  "components": {
    "minecraft:attribute_modifiers": [
      {
        "type": "minecraft:attack_damage",
        "id": "examplepack:cheese_damage",
        "amount": 3,
        "operation": "add_value",
        "slot": "mainhand"
      }
    ]
  }
}
```

| Field | Meaning |
| --- | --- |
| `type` | Attribute being modified. |
| `id` | Identifier for this modifier. Replaces the UUIDs used before 1.21. Two modifiers with the same `id` on the same attribute do not stack — the later one replaces the earlier. |
| `amount` | How much. Negative values subtract. |
| `operation` | See [below](#operations). |
| `slot` | Where the item must be for the modifier to apply: `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, `body`, `armor`, `hand`, `any`. |

## In an event, as an action

The [`modify_attribute`](../Events.md#player-actions) action applies one to the player directly:

```json
{ "action": "modify_attribute",
  "id": "examplepack:speed_boost",
  "attribute": "minecraft:movement_speed",
  "amount": 0.2,
  "operation": "add_multiplied_base" }
```

Same four values, but the attribute key is called `attribute` here rather than `type`, and there is no
`slot` — the modifier goes onto the player, not onto an item. All four are required; the action throws
and is logged if `operation` is anything other than the three names below.

## Operations

| Operation | Formula | Reads as |
| --- | --- | --- |
| `add_value` | `total = total + amount` | Flat addition. |
| `add_multiplied_base` | `total = total + amount × base` | A percentage of the *base* value, each modifier independent. |
| `add_multiplied_total` | `total = total × (1 + amount)` | A percentage of the *running* total, applied last and compounding. |

`amount: -0.2` with `add_multiplied_total` is a 20% reduction.

The pre-1.21 names — `addition`, `multiply_base`, `multiply_total` — and numeric operations are no
longer accepted.

## Display note

The tooltip does not show raw numbers. Multiplicative modifiers appear as percentages, and knockback
resistance is shown ten times its value, so `0.1` displays as `1`.

## See also

* [Items](./Items.md#components) — where the component goes.
* [Events](../Events.md) — where the action goes.
