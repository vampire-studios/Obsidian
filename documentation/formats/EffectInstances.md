# Effect Instances

A status effect with a duration and a strength. Used inside other files, never on its own.

## In an event

The [`apply_effect`](../Events.md#player-actions) action is the form you will actually use:

```json
{ "action": "apply_effect", "effect": "minecraft:poison", "duration": 200, "amplifier": 0 }
```

| Field | Required | Meaning |
| --- | --- | --- |
| `effect` | yes | Effect id. |
| `duration` | yes | Length in **ticks** — 20 per second, so `200` is ten seconds. |
| `amplifier` | yes | Strength. `0` is level I, `1` is level II. |

All three are required here: the action reads `duration` and `amplifier` without defaults, so omitting
either throws and the action is skipped with an error in the log.

## In a food component

[Food components](./Food.md) accept a longer form:

```json
{
  "effects": [
    {
      "effect": "minecraft:poison",
      "chance": 1.0,
      "duration": 100,
      "amplifier": 0,
      "ambient": false,
      "visible": true,
      "show_particles": true,
      "show_icon": true
    }
  ]
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `effect` | — | Effect id. |
| `chance` | `0` | Probability of applying, `0`–`1`. |
| `duration` | `0` | Ticks. `0` disappears instantly, so always set it. |
| `amplifier` | `0` | `0` is level I. |
| `ambient` | `false` | Beacon-style: different colour, no countdown. Leave off for food. |
| `visible` | `false` | Whether the effect is shown at all. |
| `show_particles` | `true` | Ambient particles around the player. |
| `show_icon` | `true` | Icon in the corner of the HUD. |

**Food effects are currently not applied** — the code that converts them into food properties is
commented out. Use an `on_use` [event](../Events.md) with `apply_effect` until that changes.

## See also

* [Food](./Food.md) — food components.
* [Events](../Events.md) — `apply_effect`, `remove_effect`, `clear_effects`.
